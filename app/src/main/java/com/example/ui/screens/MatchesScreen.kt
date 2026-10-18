package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NetworkSimState
import com.example.ui.MatchFilter
import com.example.ui.components.MatchCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiveAlertRed
import com.example.ui.theme.NeonGrassGreen
import com.example.ui.theme.PitchBorder
import com.example.ui.theme.PitchCard
import com.example.ui.theme.PitchCardElevated
import com.example.ui.theme.PitchDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@Composable
fun MatchesScreen(
    matches: List<MatchEntity>,
    favoriteTeams: List<FavoriteTeamEntity>,
    networkSimState: NetworkSimState,
    selectedMatchFilter: MatchFilter,
    selectedLeagueFilter: String,
    onSelectMatchFilter: (MatchFilter) -> Unit,
    onSelectLeagueFilter: (String) -> Unit,
    onToggleFavorite: (String, String) -> Unit,
    onSelectMatch: (MatchEntity) -> Unit,
    onSimulateGoal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val leagues = listOf("All", "Premier League", "La Liga", "UEFA Champions League", "Serie A", "Bundesliga")
    val favNames = favoriteTeams.map { it.teamName.lowercase() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDark)
            .testTag("matches_screen")
    ) {
        // Hero Stadium Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_stadium_hero),
                    contentDescription = "Stadium Atmosphere",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    PitchDark.copy(alpha = 0.3f),
                                    PitchDark.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LIVE MATCHDAY TRACKER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp
                                ),
                                color = NeonGrassGreen
                            )
                            Text(
                                text = "Real-Time Goal Alerts & Fixtures",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                        }

                        // Instant Simulate Goal Button
                        Button(
                            onClick = onSimulateGoal,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonGrassGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("simulate_goal_live_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "Simulate Goal",
                                tint = PitchDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Goal! ⚽",
                                color = PitchDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Offline / Bad Connection Warning Banner (if not online fast)
        if (networkSimState != NetworkSimState.ONLINE_FAST) {
            item {
                Surface(
                    color = if (networkSimState == NetworkSimState.OFFLINE_CACHE) LiveAlertRed.copy(alpha = 0.15f) else TrophyGold.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (networkSimState == NetworkSimState.OFFLINE_CACHE) LiveAlertRed.copy(alpha = 0.4f) else TrophyGold.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (networkSimState == NetworkSimState.OFFLINE_CACHE) Icons.Default.Flight else Icons.Default.SportsSoccer,
                            contentDescription = "Connection Status",
                            tint = if (networkSimState == NetworkSimState.OFFLINE_CACHE) LiveAlertRed else TrophyGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (networkSimState == NetworkSimState.OFFLINE_CACHE)
                                "Offline Mode: Seamlessly displaying results cached in Room database"
                            else
                                "Weak 2G Network: Responses optimized with local database cache",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Match Filter Row (All, Live, Favorites, Finished, Upcoming)
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MatchFilter.entries) { filter ->
                    val isSelected = filter == selectedMatchFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectMatchFilter(filter) },
                        label = {
                            Text(
                                text = filter.label,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) PitchDark else TextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (filter == MatchFilter.LIVE) LiveAlertRed else if (filter == MatchFilter.FAVORITES) TrophyGold else NeonGrassGreen,
                            containerColor = PitchCard
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonGrassGreen else PitchBorder
                        ),
                        modifier = Modifier.testTag("match_filter_${filter.name}")
                    )
                }
            }
        }

        // League Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(leagues) { league ->
                    val isSelected = league == selectedLeagueFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectLeagueFilter(league) },
                        label = {
                            Text(
                                text = league,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) PitchDark else TextMuted
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan,
                            containerColor = PitchDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) ElectricCyan else PitchBorder
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Match Cards
        if (matches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "No matches",
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matches found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try switching the league or match status filter above.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(matches) { match ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    MatchCard(
                        match = match,
                        isHomeFavorite = favNames.contains(match.homeTeam.lowercase()),
                        isAwayFavorite = favNames.contains(match.awayTeam.lowercase()),
                        onToggleFavorite = onToggleFavorite,
                        onClickMatch = onSelectMatch
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
