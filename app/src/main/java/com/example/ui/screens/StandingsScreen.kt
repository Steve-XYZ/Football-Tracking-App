package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.StandingEntity
import com.example.ui.theme.DrawYellow
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LossRed
import com.example.ui.theme.NeonGrassGreen
import com.example.ui.theme.PitchBorder
import com.example.ui.theme.PitchCard
import com.example.ui.theme.PitchCardElevated
import com.example.ui.theme.PitchDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold
import com.example.ui.theme.WinGreen

@Composable
fun StandingsScreen(
    standings: List<StandingEntity>,
    favoriteTeams: List<FavoriteTeamEntity>,
    selectedLeague: String,
    onSelectLeague: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val leagues = listOf(
        "Premier League",
        "La Liga",
        "UEFA Champions League",
        "Serie A",
        "Bundesliga"
    )

    val currentStandings = standings.filter { it.league.equals(selectedLeague, ignoreCase = true) }
        .sortedBy { it.position }

    val favTeamNames = favoriteTeams.map { it.teamName.lowercase() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDark)
            .padding(top = 10.dp)
            .testTag("standings_screen")
    ) {
        // League Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(leagues) { league ->
                val isSelected = league == selectedLeague
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectLeague(league) },
                    label = {
                        Text(
                            text = league,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) PitchDark else TextSecondary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonGrassGreen,
                        containerColor = PitchCard
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) NeonGrassGreen else PitchBorder
                    ),
                    modifier = Modifier.testTag("league_chip_$league")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Standings Table Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PitchCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PitchCardElevated)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        modifier = Modifier.width(26.dp)
                    )
                    Text(
                        text = "Club",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "P",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(26.dp)
                    )
                    Text(
                        text = "W",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = "D",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = "L",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = "GD",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = "PTS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = NeonGrassGreen,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(34.dp)
                    )
                    Text(
                        text = "Form",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(62.dp)
                    )
                }

                // Table Rows
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(currentStandings) { standing ->
                        val isFavorite = favTeamNames.contains(standing.teamName.lowercase())

                        val posColor = when {
                            standing.position <= 4 -> NeonGrassGreen
                            standing.position <= 6 -> ElectricCyan
                            standing.position >= 7 && currentStandings.size >= 8 && standing.position >= currentStandings.size - 1 -> LossRed
                            else -> TextSecondary
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isFavorite) TrophyGold.copy(alpha = 0.08f) else PitchCard)
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Position
                            Box(
                                modifier = Modifier
                                    .width(26.dp)
                                    .height(20.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = "${standing.position}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = posColor
                                )
                            }

                            // Club Name & Favorite Star
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = standing.teamName,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isFavorite) FontWeight.Black else FontWeight.SemiBold
                                    ),
                                    color = if (isFavorite) TrophyGold else TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (isFavorite) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Favorite Club",
                                        tint = TrophyGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // P, W, D, L
                            Text(
                                text = "${standing.played}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(26.dp)
                            )
                            Text(
                                text = "${standing.won}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(24.dp)
                            )
                            Text(
                                text = "${standing.drawn}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(24.dp)
                            )
                            Text(
                                text = "${standing.lost}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(24.dp)
                            )

                            // GD
                            Text(
                                text = if (standing.goalDifference > 0) "+${standing.goalDifference}" else "${standing.goalDifference}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = if (standing.goalDifference > 0) TextPrimary else TextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(32.dp)
                            )

                            // PTS
                            Text(
                                text = "${standing.points}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                color = NeonGrassGreen,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.width(34.dp)
                            )

                            // Form Badges
                            Row(
                                modifier = Modifier.width(62.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val forms = standing.form.split(",").takeLast(5)
                                forms.forEach { result ->
                                    val formColor = when (result.trim()) {
                                        "W" -> WinGreen
                                        "D" -> DrawYellow
                                        else -> LossRed
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(formColor)
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(PitchBorder.copy(alpha = 0.5f))
                        )
                    }
                }

                // Legend at Bottom
                Surface(
                    color = PitchCardElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(NeonGrassGreen, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("UCL", color = TextSecondary, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.size(8.dp).background(ElectricCyan, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("UEL", color = TextSecondary, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.size(8.dp).background(LossRed, CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Relegation", color = TextSecondary, fontSize = 10.sp)
                        }
                        Text(
                            text = "Cached Offline 💾",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
