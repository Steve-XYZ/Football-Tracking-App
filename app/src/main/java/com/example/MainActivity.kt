package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FutbolViewModel
import com.example.ui.HomeTab
import com.example.ui.components.GoalCelebrationBanner
import com.example.ui.components.TopFutbolBar
import com.example.ui.screens.FavoritesSettingsScreen
import com.example.ui.screens.FeedHighlightsScreen
import com.example.ui.screens.HistoricalStatsScreen
import com.example.ui.screens.MatchDetailScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.StandingsScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGrassGreen
import com.example.ui.theme.PitchBorder
import com.example.ui.theme.PitchDark
import com.example.ui.theme.PitchSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as FutbolApplication

        setContent {
            MyApplicationTheme {
                val viewModel: FutbolViewModel = viewModel(
                    factory = FutbolViewModel.Factory(app.repository)
                )
                FutbolAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun FutbolAppContent(viewModel: FutbolViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Runtime Permission for Android 13+ Notifications
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationPermissionGranted(isGranted)
    }

    LaunchedEffect(Unit) {
        val hasPermission = NotificationHelper.hasNotificationPermission(context)
        viewModel.setNotificationPermissionGranted(hasPermission)
        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Show Snackbars from ViewModel state
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (uiState.selectedMatchDetail == null) {
                TopFutbolBar(
                    networkSimState = uiState.networkSimState,
                    isSyncing = uiState.isSyncing,
                    notificationsEnabled = uiState.userNotificationGranted,
                    onNetworkStateChanged = { viewModel.setNetworkSimState(it) },
                    onRefreshClicked = { viewModel.refreshMatches(context) },
                    onRequestNotifications = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (uiState.selectedMatchDetail == null) {
                FutbolBottomBar(
                    currentTab = uiState.selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        },
        containerColor = PitchDark
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Content Area
            if (uiState.selectedMatchDetail != null) {
                val detail = uiState.selectedMatchDetail!!
                val favNames = uiState.favoriteTeams.map { it.teamName.lowercase() }
                MatchDetailScreen(
                    match = detail,
                    isHomeFavorite = favNames.contains(detail.homeTeam.lowercase()),
                    isAwayFavorite = favNames.contains(detail.awayTeam.lowercase()),
                    onBack = { viewModel.selectMatchDetail(null) },
                    onTestGoalForMatch = { viewModel.testGoalScored(context) }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // In-app Goal Alert Banner
                    GoalCelebrationBanner(
                        goalAlert = uiState.activeGoalAlert,
                        onDismiss = { viewModel.dismissGoalAlert() }
                    )

                    // Tab View Transitions
                    AnimatedContent(
                        targetState = uiState.selectedTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tabTransition",
                        modifier = Modifier.fillMaxSize()
                    ) { tab ->
                        when (tab) {
                            HomeTab.MATCHES -> {
                                MatchesScreen(
                                    matches = uiState.filteredMatches,
                                    favoriteTeams = uiState.favoriteTeams,
                                    networkSimState = uiState.networkSimState,
                                    selectedMatchFilter = uiState.selectedMatchFilter,
                                    selectedLeagueFilter = uiState.selectedLeagueFilter,
                                    onSelectMatchFilter = { viewModel.selectMatchFilter(it) },
                                    onSelectLeagueFilter = { viewModel.selectLeagueFilter(it) },
                                    onToggleFavorite = { team, league -> viewModel.toggleFavoriteTeam(team, league) },
                                    onSelectMatch = { viewModel.selectMatchDetail(it) },
                                    onSimulateGoal = { viewModel.testGoalScored(context) }
                                )
                            }
                            HomeTab.STANDINGS -> {
                                StandingsScreen(
                                    standings = uiState.standings,
                                    favoriteTeams = uiState.favoriteTeams,
                                    selectedLeague = uiState.selectedStandingsLeague,
                                    onSelectLeague = { viewModel.selectStandingsLeague(it) }
                                )
                            }
                            HomeTab.FEED -> {
                                FeedHighlightsScreen(
                                    newsList = uiState.news,
                                    favoriteTeams = uiState.favoriteTeams,
                                    onToggleBookmark = { viewModel.toggleNewsBookmark(it) }
                                )
                            }
                            HomeTab.HISTORY -> {
                                HistoricalStatsScreen(
                                    historicalMatches = uiState.historicalMatches,
                                    searchQuery = uiState.historySearchQuery,
                                    selectedSeason = uiState.historySeasonFilter,
                                    onSearchQueryChanged = { viewModel.setHistorySearchQuery(it) },
                                    onSeasonChanged = { viewModel.setHistorySeasonFilter(it) }
                                )
                            }
                            HomeTab.FAVORITES -> {
                                FavoritesSettingsScreen(
                                    favoriteTeams = uiState.favoriteTeams,
                                    networkSimState = uiState.networkSimState,
                                    notificationsEnabled = uiState.userNotificationGranted,
                                    lastSyncTimestamp = uiState.lastSyncTimestamp,
                                    onToggleFavorite = { team, league -> viewModel.toggleFavoriteTeam(team, league) },
                                    onToggleNotification = { team, notify -> viewModel.toggleFavoriteNotification(team, notify) },
                                    onNetworkStateChanged = { viewModel.setNetworkSimState(it) },
                                    onRequestNotifications = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                    },
                                    onTestGoalAlert = { viewModel.testGoalScored(context) },
                                    onRefreshCache = { viewModel.refreshMatches(context) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FutbolBottomBar(
    currentTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = PitchSurface,
        contentColor = TextPrimary,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("futbol_bottom_bar")
    ) {
        NavigationBarItem(
            selected = currentTab == HomeTab.MATCHES,
            onClick = { onTabSelected(HomeTab.MATCHES) },
            icon = {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "Matches"
                )
            },
            label = {
                Text(
                    text = "Matches",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PitchDark,
                selectedTextColor = NeonGrassGreen,
                indicatorColor = NeonGrassGreen,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_matches")
        )

        NavigationBarItem(
            selected = currentTab == HomeTab.STANDINGS,
            onClick = { onTabSelected(HomeTab.STANDINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = "Standings"
                )
            },
            label = {
                Text(
                    text = "Table",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PitchDark,
                selectedTextColor = ElectricCyan,
                indicatorColor = ElectricCyan,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_standings")
        )

        NavigationBarItem(
            selected = currentTab == HomeTab.FEED,
            onClick = { onTabSelected(HomeTab.FEED) },
            icon = {
                Icon(
                    imageVector = Icons.Default.OndemandVideo,
                    contentDescription = "Highlights"
                )
            },
            label = {
                Text(
                    text = "Highlights",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PitchDark,
                selectedTextColor = NeonGrassGreen,
                indicatorColor = NeonGrassGreen,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_highlights")
        )

        NavigationBarItem(
            selected = currentTab == HomeTab.HISTORY,
            onClick = { onTabSelected(HomeTab.HISTORY) },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History"
                )
            },
            label = {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PitchDark,
                selectedTextColor = TrophyGold,
                indicatorColor = TrophyGold,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_history")
        )

        NavigationBarItem(
            selected = currentTab == HomeTab.FAVORITES,
            onClick = { onTabSelected(HomeTab.FAVORITES) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favorites"
                )
            },
            label = {
                Text(
                    text = "Teams",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PitchDark,
                selectedTextColor = TrophyGold,
                indicatorColor = TrophyGold,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextMuted
            ),
            modifier = Modifier.testTag("tab_favorites")
        )
    }
}
