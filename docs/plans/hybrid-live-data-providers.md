# Plan: Hybrid live football data providers

## Goal

Replace seed-only / simulated sync with real football data while staying inside free (or cheap) API budgets.

The app keeps Room as the offline source of truth. Two external providers split by job. Live polling runs every **120 seconds**, which is frequent enough for a phone scores UX and roughly halves quota burn versus a 60-second cadence.

## Why two providers

No free tier is both high-volume and truly live.

| Need | Constraint |
|------|------------|
| Fixtures, schedules, standings, history | Many calls; delay is acceptable |
| Live scores and goal events | Few calls; freshness matters |

Mixing providers only works if each owns a clear job and the app merges into one internal model. Random failover between providers for the same field creates conflicting scores and wasted quota.

## Provider roles

| Job | Provider | Tier assumption | Cadence |
|-----|----------|-----------------|---------|
| Fixtures, schedules, standings, historical results | [football-data.org](https://www.football-data.org) | Free (~10 calls/min); scores may be delayed | On app open, pull-to-refresh, and periodic background refresh (minutes to hours) |
| Live scores / in-play events while matches are LIVE | [API-Football](https://www.api-football.com) (api-sports) | Free (~100 requests/day) | Every **120s**, only while at least one tracked match is LIVE |
| Offline cache, favorites, bookmarks | Local Room (`futbol_pulse_cache.db`) | Unlimited | Always |

Optional later upgrade: football-data.org “Free w/ Livescores” (~€12/mo) or API-Football Pro if live quota becomes the bottleneck.

## Architecture

```
┌─────────────────┐     ┌──────────────────────────┐
│  Android app    │────▶│  Thin backend (recommended)│
│  Compose + Room │◀────│  cache + keys + budget     │
└─────────────────┘     └───────────┬──────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
          football-data.org                  API-Football
          (workhorse / delayed OK)           (live burst budget)
```

### Why a thin backend

- API keys stay off the APK.
- One shared cache serves all installs; free quotas are not multiplied per device.
- Daily live-budget accounting lives in one place.

MVP exception: the app may talk to both APIs directly for a personal prototype, with keys via the existing secrets plugin (`.env`). Do not ship that pattern to multiple users.

### App-side data flow

1. Repository exposes the same Flows the UI already collects (`matches`, `standings`, `news`, `historicalMatches`, `favoriteTeams`).
2. Sync workers write normalized entities into Room.
3. UI never branches on provider. Provider choice is repository/backend policy only.
4. Simulated network modes and `simulateLiveGoalScored` remain available behind a debug/settings flag until live path is proven; then demote or remove them.

## Routing rules

Deterministic rules only. No “try A then B” for the same field on every request.

1. **Cold start / no LIVE matches**  
   Use football-data.org for fixtures + standings. Write to Room. Do not call API-Football.

2. **Matchday / LIVE window**  
   If any match in Room (or any favorited team’s match) has status LIVE or is within a short kickoff window, enable the live poller.

3. **Live poller (120s)**  
   One batched live request (e.g. all live fixtures, or live fixtures filtered to followed competitions/teams). Upsert scores, minute, and events into Room. Live provider wins for score/status while LIVE.

4. **Quota gate (API-Football)**  
   Track remaining daily calls. If remaining budget is low, slow to 180–300s or pause live polling and surface “live scores paused; showing cached / delayed data.”

5. **Favorites-first (when budget is tight)**  
   Prefer live polling only for matches involving favorited teams. Expand to all LIVE matches only when budget allows.

6. **Standings and schedules**  
   Always football-data.org (or Room cache). Never burn live quota on tables.

7. **Conflict policy**  
   - LIVE score/minute/events: API-Football  
   - Non-live fixtures, final scores after settle, standings: football-data.org  
   - If providers disagree after FT, prefer football-data.org on next schedule sync and clear the live overlay

## Identity and normalization

Providers use different team and match IDs. Introduce stable internal keys before mixing data.

Suggested match key shape:

```text
{competitionCode}:{homeTeamSlug}_vs_{awayTeamSlug}:{yyyy-MM-dd}
```

Requirements:

- Normalize team display names (`Man City` → `Manchester City`) via a small alias table.
- Store `providerMatchIdFootballData` and `providerMatchIdApiFootball` on the entity or a side mapping table for debugging and re-fetch.
- All UI and Room primary keys use the internal id, not a raw provider id.

Without this mapping layer, hybrid sync will duplicate matches and break favorites.

## Polling math (120s)

Assuming one batched live call every 120s from the backend:

| Scenario | Approx. API-Football calls |
|----------|----------------------------|
| 1 hour continuous LIVE | ~30 |
| 3 hour matchday window | ~90 |
| Favorites-only, sparse LIVE | often well under 100/day |

120s is the default. Optional enhancement: 60s only for favorited LIVE matches when daily budget is healthy; otherwise stay at 120s.

## Secrets and config

| Secret / config | Purpose |
|-----------------|--------|
| `FOOTBALL_DATA_API_KEY` | football-data.org |
| `API_FOOTBALL_KEY` | API-Football (`x-apisports-key`) |
| `LIVE_POLL_INTERVAL_MS` | Default `120000` |
| `LIVE_BUDGET_DAILY` | Default `100` (free tier); soft gate |

Extend `.env.example`. Keep `GEMINI_API_KEY` separate; Gemini is out of scope for this plan unless used later for highlight copy.

## Implementation phases

### Phase 0 — Prep (this plan)

- Document provider split, 120s cadence, routing, and mapping rules.
- No production behavior change yet.

### Phase 1 — Contracts and Room

- Add provider DTO mappers → existing `MatchEntity` / `StandingEntity` (extend entities only if needed for provider ids).
- Add team alias / match key helper with unit tests.
- Add sync status fields already partially present (`lastSyncTimestamp`, network state) so UI can show “live”, “cached”, or “delayed”.

### Phase 2 — Workhorse sync (football-data.org)

- Implement fixtures + standings fetch (app-direct MVP or backend).
- Replace empty-DB seed path: seed only if sync fails and DB is empty; otherwise provider data wins.
- Wire pull-to-refresh to real sync; remove success snackbar that claims sync when only `delay()` ran.
- Fix goal notification gate separately if still `shouldNotify \|\| !isFavorite` (notify only when favorite + `notifyGoals`).

### Phase 3 — Live burst (API-Football)

- Implement LIVE poller at 120s with daily budget counter.
- Favorites-first option.
- Update Room match rows; refresh `selectedMatchDetail` from the matches Flow so detail is not stale.
- Drive in-app banner / notifications from real score deltas (favorite + notify), not only `simulateLiveGoalScored`.

### Phase 4 — Backend cache (before multi-user)

- Move keys and poller to a small server or Cloud Function.
- App reads cached JSON; Room remains the on-device cache.
- Add rate-limit and cache headers.

### Phase 5 — Cleanup

- Demote or remove network simulation modes and fake goal simulation from default UX (keep behind debug if useful).
- Drop unused HTTP/AI dependencies or wire them intentionally.
- Update `metadata.json` so “real-time” claims match actual behavior.
- Add Gradle wrapper files if still missing so CI can build.

## Explicit non-goals (this plan)

- Scraping Sofascore / ESPN / FotMob
- Shipping Gemini as the live data source
- Per-device direct polling of API-Football at 60s for many users
- Perfect multi-league coverage on free tiers

## Success criteria

- Opening the app shows provider-backed fixtures/standings in Room without requiring seed data when the network works.
- During LIVE matches, scores update about every 120s without exceeding the configured daily live budget under normal personal use.
- Favorites + `notifyGoals` control notifications; non-favorites do not spam.
- Offline mode still serves Room.
- UI has no provider-specific branches.

## Open decisions

1. **MVP transport:** app-direct dual API vs backend-first from day one. Recommendation: app-direct for Phase 2–3 personal testing; backend before any shared install.
2. **Competition set:** start with the leagues already in the UI sample (e.g. Premier League, La Liga, Bundesliga, UCL) to limit mapping work.
3. **News / highlights:** stay on sample or a later third source; do not spend live quota on feed content.

## References

- football-data.org pricing / free tier: https://www.football-data.org/pricing  
- API-Football getting started: https://www.api-football.com  
- Current app gaps motivating this plan: sample-only `FutbolRepository.refreshData()`, unused Retrofit stack, simulated goals
