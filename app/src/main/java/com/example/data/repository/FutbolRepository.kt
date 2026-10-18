package com.example.data.repository

import android.content.Context
import com.example.data.local.FutbolDao
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.GoalAlert
import com.example.data.model.HistoricalMatchEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NetworkSimState
import com.example.data.model.NewsHighlightEntity
import com.example.data.model.StandingEntity
import com.example.data.sample.InitialData
import com.example.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FutbolRepository(
    private val dao: FutbolDao,
    private val scope: CoroutineScope
) {
    val matches: Flow<List<MatchEntity>> = dao.getAllMatchesFlow()
    val standings: Flow<List<StandingEntity>> = dao.getAllStandingsFlow()
    val news: Flow<List<NewsHighlightEntity>> = dao.getAllNewsFlow()
    val historicalMatches: Flow<List<HistoricalMatchEntity>> = dao.getAllHistoricalMatchesFlow()
    val favoriteTeams: Flow<List<FavoriteTeamEntity>> = dao.getFavoriteTeamsFlow()

    private val _networkSimState = MutableStateFlow(NetworkSimState.ONLINE_FAST)
    val networkSimState: StateFlow<NetworkSimState> = _networkSimState.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _goalAlerts = MutableSharedFlow<GoalAlert>(extraBufferCapacity = 5)
    val goalAlerts: SharedFlow<GoalAlert> = _goalAlerts.asSharedFlow()

    init {
        scope.launch(Dispatchers.IO) {
            seedDatabaseIfEmpty()
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        val existingMatches = dao.getAllMatchesFlow().firstOrNull() ?: emptyList()
        if (existingMatches.isEmpty()) {
            dao.insertMatches(InitialData.sampleMatches)
            dao.insertStandings(InitialData.sampleStandings)
            dao.insertNews(InitialData.sampleNewsAndHighlights)
            dao.insertHistoricalMatches(InitialData.sampleHistoricalMatches)
            for (fav in InitialData.defaultFavorites) {
                dao.insertFavoriteTeam(fav)
            }
        }
    }

    fun setNetworkSimState(state: NetworkSimState) {
        _networkSimState.value = state
    }

    suspend fun refreshData(context: Context): Result<String> = withContext(Dispatchers.IO) {
        if (_networkSimState.value == NetworkSimState.OFFLINE_CACHE) {
            return@withContext Result.success("Offline Mode: Seamlessly loaded cached data from local Room database")
        }

        _isSyncing.value = true
        try {
            if (_networkSimState.value == NetworkSimState.POOR_CONNECTION) {
                // High latency simulation for bad internet
                delay(1800)
            } else {
                delay(500)
            }
            _lastSyncTimestamp.value = System.currentTimeMillis()
            Result.success("Sync completed successfully")
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isSyncing.value = false
        }
    }

    suspend fun toggleFavorite(teamName: String, league: String) = withContext(Dispatchers.IO) {
        val currentFavorites = dao.getFavoriteTeamsFlow().firstOrNull() ?: emptyList()
        val existing = currentFavorites.find { it.teamName.equals(teamName, ignoreCase = true) }
        if (existing != null) {
            dao.deleteFavoriteTeam(teamName)
        } else {
            dao.insertFavoriteTeam(FavoriteTeamEntity(teamName = teamName, league = league, notifyGoals = true))
        }
    }

    suspend fun toggleFavoriteNotification(teamName: String, notify: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavoriteNotification(teamName, notify)
    }

    suspend fun toggleNewsBookmark(news: NewsHighlightEntity) = withContext(Dispatchers.IO) {
        dao.updateNews(news.copy(isBookmarked = !news.isBookmarked))
    }

    suspend fun searchHistorical(query: String): Flow<List<HistoricalMatchEntity>> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            dao.getAllHistoricalMatchesFlow()
        } else {
            dao.searchHistoricalMatchesFlow(query.trim())
        }
    }

    suspend fun simulateLiveGoalScored(context: Context) = withContext(Dispatchers.IO) {
        val currentMatches = dao.getAllMatchesFlow().firstOrNull() ?: emptyList()
        val liveMatches = currentMatches.filter { it.status == "LIVE" }
        if (liveMatches.isEmpty()) return@withContext

        // Pick one match to update
        val targetMatch = liveMatches.random()
        val currentFavorites = dao.getFavoriteTeamsFlow().firstOrNull() ?: emptyList()

        // Choose scoring team (home or away)
        val scoreHome = (0..1).random() == 1
        val scoringTeam = if (scoreHome) targetMatch.homeTeam else targetMatch.awayTeam
        val opponentTeam = if (scoreHome) targetMatch.awayTeam else targetMatch.homeTeam
        val newHomeScore = if (scoreHome) targetMatch.homeScore + 1 else targetMatch.homeScore
        val newAwayScore = if (!scoreHome) targetMatch.awayScore + 1 else targetMatch.awayScore

        val currentMinNum = targetMatch.minute.replace("'", "").toIntOrNull() ?: 70
        val newMinNum = (currentMinNum + (1..3).random()).coerceAtMost(90)
        val newMinute = "$newMinNum'"

        val scorersPool = when (scoringTeam) {
            "Real Madrid" -> listOf("Vinicius Jr", "Jude Bellingham", "Kylian Mbappé", "Rodrygo")
            "Barcelona" -> listOf("Robert Lewandowski", "Lamine Yamal", "Raphinha", "Dani Olmo")
            "Arsenal" -> listOf("Bukayo Saka", "Kai Havertz", "Gabriel Martinelli", "Martin Ødegaard")
            "Manchester City" -> listOf("Erling Haaland", "Kevin De Bruyne", "Phil Foden", "Bernardo Silva")
            "Bayern Munich" -> listOf("Harry Kane", "Jamal Musiala", "Leroy Sané", "Michael Olise")
            "Borussia Dortmund" -> listOf("Serhou Guirassy", "Julian Brandt", "Karim Adeyemi")
            else -> listOf("Star Striker", "Midfield Maestro")
        }
        val scorer = scorersPool.random()

        val updatedEvents = if (targetMatch.eventsSummary.isBlank()) {
            "$newMinute ⚽ $scorer"
        } else {
            "${targetMatch.eventsSummary}; $newMinute ⚽ $scorer"
        }

        val updatedMatch = targetMatch.copy(
            homeScore = newHomeScore,
            awayScore = newAwayScore,
            minute = newMinute,
            eventsSummary = updatedEvents,
            lastUpdated = System.currentTimeMillis()
        )

        dao.updateMatch(updatedMatch)

        // Check if favorite team
        val favEntry = currentFavorites.find { it.teamName.equals(scoringTeam, ignoreCase = true) }
        val isFavorite = favEntry != null
        val shouldNotify = isFavorite && (favEntry.notifyGoals)

        val goalAlert = GoalAlert(
            matchId = targetMatch.id,
            team = scoringTeam,
            opponent = opponentTeam,
            minute = newMinute,
            scorer = scorer,
            newHomeScore = newHomeScore,
            newAwayScore = newAwayScore,
            isFavoriteTeam = isFavorite
        )

        _goalAlerts.emit(goalAlert)

        if (shouldNotify || !isFavorite) {
            NotificationHelper.postGoalNotification(context, goalAlert)
        }
    }
}
