package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val league: String, // "Premier League", "La Liga", "UEFA Champions League", "Serie A", "Bundesliga"
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val status: String, // "LIVE", "FINISHED", "UPCOMING"
    val minute: String, // "68'", "FT", "20:45"
    val matchDate: String, // "Today", "Yesterday", "Tomorrow"
    val stadium: String,
    val referee: String,
    val eventsSummary: String, // Semicolon-separated events: "23' ⚽ V. Junior; 45' 🟨 R. Araujo; 68' ⚽ J. Bellingham"
    val homePossession: Int, // e.g. 54
    val awayPossession: Int, // e.g. 46
    val homeShots: Int,
    val awayShots: Int,
    val homeShotsOnTarget: Int,
    val awayShotsOnTarget: Int,
    val homeCorners: Int,
    val awayCorners: Int,
    val homeFouls: Int,
    val awayFouls: Int,
    val homeXg: Float,
    val awayXg: Float,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "standings")
data class StandingEntity(
    @PrimaryKey val id: String, // league_position
    val league: String,
    val position: Int,
    val teamName: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int,
    val form: String // e.g. "W,W,D,W,L"
)

@Entity(tableName = "news_highlights")
data class NewsHighlightEntity(
    @PrimaryKey val id: String,
    val matchId: String? = null,
    val title: String,
    val category: String, // "Goal Highlight", "Match Recap", "Tactics", "Breaking News"
    val summary: String,
    val keyTimestamp: String? = null, // "68' Goal", "88' Red Card"
    val relatedTeam: String,
    val publishedAgo: String,
    val views: String,
    val videoDuration: String? = null, // e.g. "0:45"
    val isBookmarked: Boolean = false
)

@Entity(tableName = "historical_matches")
data class HistoricalMatchEntity(
    @PrimaryKey val id: String,
    val tournament: String,
    val season: String, // "2024/25", "2023/24", "2022/23"
    val matchDate: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val stadium: String,
    val homePossession: Int,
    val awayPossession: Int,
    val homeShots: Int,
    val awayShots: Int,
    val homeShotsOnTarget: Int,
    val awayShotsOnTarget: Int,
    val homeXg: Float,
    val awayXg: Float,
    val homeFouls: Int,
    val awayFouls: Int,
    val homeCorners: Int,
    val awayCorners: Int,
    val keyHighlight: String
)

@Entity(tableName = "favorite_teams")
data class FavoriteTeamEntity(
    @PrimaryKey val teamName: String,
    val league: String,
    val notifyGoals: Boolean = true
)

enum class NetworkSimState(val label: String, val description: String) {
    ONLINE_FAST("Online ⚡", "Instant real-time updates & low latency"),
    POOR_CONNECTION("Weak 2G 📶", "High latency, packets queued, aggressive cache use"),
    OFFLINE_CACHE("Offline ✈️", "Zero network, full local Room database cache")
}

data class GoalAlert(
    val matchId: String,
    val team: String,
    val opponent: String,
    val minute: String,
    val scorer: String,
    val newHomeScore: Int,
    val newAwayScore: Int,
    val isFavoriteTeam: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
