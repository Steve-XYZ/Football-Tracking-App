package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.GoalAlert
import com.example.data.model.HistoricalMatchEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NetworkSimState
import com.example.data.model.NewsHighlightEntity
import com.example.data.model.StandingEntity
import com.example.data.repository.FutbolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class HomeTab(val label: String) {
    MATCHES("Matches"),
    STANDINGS("Standings"),
    FEED("Feed & Highlights"),
    HISTORY("History"),
    FAVORITES("Favorites")
}

enum class MatchFilter(val label: String) {
    ALL("All"),
    LIVE("🔴 Live"),
    FAVORITES("⭐ Favorites"),
    FINISHED("Finished"),
    UPCOMING("Upcoming")
}

data class FutbolUiState(
    val matches: List<MatchEntity> = emptyList(),
    val filteredMatches: List<MatchEntity> = emptyList(),
    val standings: List<StandingEntity> = emptyList(),
    val news: List<NewsHighlightEntity> = emptyList(),
    val historicalMatches: List<HistoricalMatchEntity> = emptyList(),
    val favoriteTeams: List<FavoriteTeamEntity> = emptyList(),
    val selectedTab: HomeTab = HomeTab.MATCHES,
    val selectedLeagueFilter: String = "All",
    val selectedMatchFilter: MatchFilter = MatchFilter.ALL,
    val selectedStandingsLeague: String = "Premier League",
    val selectedNewsTeamFilter: String? = null,
    val historySearchQuery: String = "",
    val historySeasonFilter: String = "All",
    val selectedMatchDetail: MatchEntity? = null,
    val activeGoalAlert: GoalAlert? = null,
    val networkSimState: NetworkSimState = NetworkSimState.ONLINE_FAST,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val isSyncing: Boolean = false,
    val userNotificationGranted: Boolean = true,
    val snackbarMessage: String? = null
)

class FutbolViewModel(
    private val repository: FutbolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FutbolUiState())
    val uiState: StateFlow<FutbolUiState> = _uiState.asStateFlow()

    init {
        // Collect matches
        viewModelScope.launch {
            repository.matches.collect { matchesList ->
                _uiState.update { current ->
                    current.copy(
                        matches = matchesList,
                        filteredMatches = computeFilteredMatches(
                            matches = matchesList,
                            leagueFilter = current.selectedLeagueFilter,
                            matchFilter = current.selectedMatchFilter,
                            favorites = current.favoriteTeams
                        )
                    )
                }
            }
        }

        // Collect standings
        viewModelScope.launch {
            repository.standings.collect { standingsList ->
                _uiState.update { it.copy(standings = standingsList) }
            }
        }

        // Collect news
        viewModelScope.launch {
            repository.news.collect { newsList ->
                _uiState.update { it.copy(news = newsList) }
            }
        }

        // Collect historical matches
        viewModelScope.launch {
            repository.historicalMatches.collect { historyList ->
                _uiState.update { it.copy(historicalMatches = historyList) }
            }
        }

        // Collect favorites
        viewModelScope.launch {
            repository.favoriteTeams.collect { favsList ->
                _uiState.update { current ->
                    current.copy(
                        favoriteTeams = favsList,
                        filteredMatches = computeFilteredMatches(
                            matches = current.matches,
                            leagueFilter = current.selectedLeagueFilter,
                            matchFilter = current.selectedMatchFilter,
                            favorites = favsList
                        )
                    )
                }
            }
        }

        // Collect network simulation state
        viewModelScope.launch {
            repository.networkSimState.collect { netState ->
                _uiState.update { it.copy(networkSimState = netState) }
            }
        }

        // Collect sync timestamp
        viewModelScope.launch {
            repository.lastSyncTimestamp.collect { syncTime ->
                _uiState.update { it.copy(lastSyncTimestamp = syncTime) }
            }
        }

        // Collect syncing status
        viewModelScope.launch {
            repository.isSyncing.collect { syncing ->
                _uiState.update { it.copy(isSyncing = syncing) }
            }
        }

        // Collect goal alerts for in-app banner
        viewModelScope.launch {
            repository.goalAlerts.collect { alert ->
                _uiState.update { it.copy(activeGoalAlert = alert) }
            }
        }
    }

    private fun computeFilteredMatches(
        matches: List<MatchEntity>,
        leagueFilter: String,
        matchFilter: MatchFilter,
        favorites: List<FavoriteTeamEntity>
    ): List<MatchEntity> {
        val favNames = favorites.map { it.teamName.lowercase() }
        return matches.filter { match ->
            val leagueOk = leagueFilter == "All" || match.league.equals(leagueFilter, ignoreCase = true)
            val filterOk = when (matchFilter) {
                MatchFilter.ALL -> true
                MatchFilter.LIVE -> match.status == "LIVE"
                MatchFilter.FAVORITES -> favNames.contains(match.homeTeam.lowercase()) || favNames.contains(match.awayTeam.lowercase())
                MatchFilter.FINISHED -> match.status == "FINISHED"
                MatchFilter.UPCOMING -> match.status == "UPCOMING"
            }
            leagueOk && filterOk
        }
    }

    fun selectTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectLeagueFilter(league: String) {
        _uiState.update { current ->
            current.copy(
                selectedLeagueFilter = league,
                filteredMatches = computeFilteredMatches(
                    matches = current.matches,
                    leagueFilter = league,
                    matchFilter = current.selectedMatchFilter,
                    favorites = current.favoriteTeams
                )
            )
        }
    }

    fun selectMatchFilter(filter: MatchFilter) {
        _uiState.update { current ->
            current.copy(
                selectedMatchFilter = filter,
                filteredMatches = computeFilteredMatches(
                    matches = current.matches,
                    leagueFilter = current.selectedLeagueFilter,
                    matchFilter = filter,
                    favorites = current.favoriteTeams
                )
            )
        }
    }

    fun selectStandingsLeague(league: String) {
        _uiState.update { it.copy(selectedStandingsLeague = league) }
    }

    fun selectNewsFilter(teamName: String?) {
        _uiState.update { it.copy(selectedNewsTeamFilter = teamName) }
    }

    fun setHistorySearchQuery(query: String) {
        _uiState.update { it.copy(historySearchQuery = query) }
    }

    fun setHistorySeasonFilter(season: String) {
        _uiState.update { it.copy(historySeasonFilter = season) }
    }

    fun selectMatchDetail(match: MatchEntity?) {
        _uiState.update { it.copy(selectedMatchDetail = match) }
    }

    fun dismissGoalAlert() {
        _uiState.update { it.copy(activeGoalAlert = null) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun setNotificationPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(userNotificationGranted = granted) }
    }

    fun setNetworkSimState(state: NetworkSimState) {
        repository.setNetworkSimState(state)
        val msg = when (state) {
            NetworkSimState.ONLINE_FAST -> "Network Mode: Fast Online (Real-time updates active)"
            NetworkSimState.POOR_CONNECTION -> "Network Mode: Weak 2G (High latency, relying on local cache)"
            NetworkSimState.OFFLINE_CACHE -> "Network Mode: Offline Mode (Using Room cache seamlessly)"
        }
        _uiState.update { it.copy(snackbarMessage = msg) }
    }

    fun toggleFavoriteTeam(teamName: String, league: String) {
        viewModelScope.launch {
            repository.toggleFavorite(teamName, league)
        }
    }

    fun toggleFavoriteNotification(teamName: String, notify: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteNotification(teamName, notify)
        }
    }

    fun toggleNewsBookmark(news: NewsHighlightEntity) {
        viewModelScope.launch {
            repository.toggleNewsBookmark(news)
        }
    }

    fun refreshMatches(context: Context) {
        viewModelScope.launch {
            val result = repository.refreshData(context)
            _uiState.update { it.copy(snackbarMessage = result.getOrElse { err -> "Sync failed: ${err.message}" }) }
        }
    }

    fun testGoalScored(context: Context) {
        viewModelScope.launch {
            repository.simulateLiveGoalScored(context)
        }
    }

    class Factory(private val repository: FutbolRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FutbolViewModel(repository) as T
        }
    }
}
