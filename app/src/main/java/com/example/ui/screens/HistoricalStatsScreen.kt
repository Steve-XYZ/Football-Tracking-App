package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HistoricalMatchEntity
import com.example.ui.theme.ElectricCyan
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
fun HistoricalStatsScreen(
    historicalMatches: List<HistoricalMatchEntity>,
    searchQuery: String,
    selectedSeason: String,
    onSearchQueryChanged: (String) -> Unit,
    onSeasonChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val seasons = listOf("All", "2024/25", "2023/24", "2022/23")

    val filteredMatches = historicalMatches.filter { match ->
        val seasonMatches = selectedSeason == "All" || match.season == selectedSeason
        val queryMatches = searchQuery.isBlank() ||
                match.homeTeam.contains(searchQuery, ignoreCase = true) ||
                match.awayTeam.contains(searchQuery, ignoreCase = true) ||
                match.tournament.contains(searchQuery, ignoreCase = true) ||
                match.stadium.contains(searchQuery, ignoreCase = true)
        seasonMatches && queryMatches
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDark)
            .padding(top = 10.dp)
            .testTag("historical_stats_screen")
    ) {
        // Search TextField
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search matches, teams, finals...", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeonGrassGreen
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = PitchCard,
                unfocusedContainerColor = PitchCard,
                focusedBorderColor = NeonGrassGreen,
                unfocusedBorderColor = PitchBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("history_search_field")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Season Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(seasons) { season ->
                val isSelected = season == selectedSeason
                FilterChip(
                    selected = isSelected,
                    onClick = { onSeasonChanged(season) },
                    label = {
                        Text(
                            text = if (season == "All") "All Seasons" else season,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) PitchDark else TextSecondary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TrophyGold,
                        containerColor = PitchCard
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) TrophyGold else PitchBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Historical Records List
        if (filteredMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = "No records",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No historical games found",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try searching for teams like Real Madrid, Arsenal, or Manchester City.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        text = "Historical Game Database (${filteredMatches.size} records)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(filteredMatches) { match ->
                    HistoricalMatchCard(match = match)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HistoricalMatchCard(match: HistoricalMatchEntity) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PitchCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("historical_card_${match.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Tournament & Season & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TrophyGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = match.tournament,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                        color = TrophyGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${match.season} • ${match.matchDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scoreline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.homeTeam,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PitchDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "${match.homeScore} - ${match.awayScore}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = NeonGrassGreen,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = match.awayTeam,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Venue
            Text(
                text = "📍 ${match.stadium}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Highlight Moment Note
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PitchCardElevated.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🏆 Key Moment: ${match.keyHighlight}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Deep Stats Comparison Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PitchDark.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Match Statistics Breakdown",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                HistoricalStatRow(
                    label = "Possession",
                    homeVal = "${match.homePossession}%",
                    awayVal = "${match.awayPossession}%"
                )
                HistoricalStatRow(
                    label = "xG (Expected Goals)",
                    homeVal = String.format("%.2f", match.homeXg),
                    awayVal = String.format("%.2f", match.awayXg)
                )
                HistoricalStatRow(
                    label = "Shots (On Target)",
                    homeVal = "${match.homeShots} (${match.homeShotsOnTarget})",
                    awayVal = "${match.awayShots} (${match.awayShotsOnTarget})"
                )
                HistoricalStatRow(
                    label = "Corners",
                    homeVal = "${match.homeCorners}",
                    awayVal = "${match.awayCorners}"
                )
                HistoricalStatRow(
                    label = "Fouls",
                    homeVal = "${match.homeFouls}",
                    awayVal = "${match.awayFouls}"
                )
            }
        }
    }
}

@Composable
fun HistoricalStatRow(label: String, homeVal: String, awayVal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = homeVal,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = NeonGrassGreen,
            modifier = Modifier.width(55.dp),
            textAlign = TextAlign.Start
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = awayVal,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = ElectricCyan,
            modifier = Modifier.width(55.dp),
            textAlign = TextAlign.End
        )
    }
}
