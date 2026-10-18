package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchEntity
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiveAlertRed
import com.example.ui.theme.NeonGrassGreen
import com.example.ui.theme.PitchBorder
import com.example.ui.theme.PitchCard
import com.example.ui.theme.PitchCardElevated
import com.example.ui.theme.PitchDark
import com.example.ui.theme.PitchSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    match: MatchEntity,
    isHomeFavorite: Boolean,
    isAwayFavorite: Boolean,
    onBack: () -> Unit,
    onTestGoalForMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    // BackHandler mandated by guidelines for custom state navigation
    BackHandler {
        onBack()
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview & Events", "Match Stats", "Info & Venue")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = match.league,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "${match.matchDate} • ${match.stadium}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("match_detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PitchSurface)
            )
        },
        containerColor = PitchDark,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("match_detail_scroll")
        ) {
            // Main Scoreboard Hero Header
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PitchCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Match Status Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (match.status == "LIVE") LiveAlertRed.copy(alpha = 0.2f) else PitchCardElevated,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (match.status == "LIVE") LiveAlertRed else PitchBorder
                            )
                        ) {
                            Text(
                                text = if (match.status == "LIVE") "🔴 ${match.minute} LIVE" else match.minute,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                color = if (match.status == "LIVE") LiveAlertRed else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Score Display Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Home Team
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(PitchCardElevated)
                                        .border(1.dp, PitchBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = match.homeTeam.take(3).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = NeonGrassGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = match.homeTeam,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    if (isHomeFavorite) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Favorite",
                                            tint = TrophyGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Score Numbers
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = PitchDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGrassGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${match.homeScore}",
                                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                                        color = if (match.status == "LIVE") NeonGrassGreen else TextPrimary
                                    )
                                    Text(
                                        text = " - ",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "${match.awayScore}",
                                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
                                        color = if (match.status == "LIVE") NeonGrassGreen else TextPrimary
                                    )
                                }
                            }

                            // Away Team
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(PitchCardElevated)
                                        .border(1.dp, PitchBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = match.awayTeam.take(3).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = ElectricCyan
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = match.awayTeam,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    if (isAwayFavorite) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = "Favorite",
                                            tint = TrophyGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (match.status == "LIVE") {
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = onTestGoalForMatch,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGrassGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("simulate_goal_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsSoccer,
                                    contentDescription = "Goal",
                                    tint = PitchDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Simulate Goal Alert ⚽",
                                    color = PitchDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Tab Navigation
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = PitchDark,
                    contentColor = NeonGrassGreen,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = NeonGrassGreen
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (selectedTabIndex == index) NeonGrassGreen else TextSecondary
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Overview & Events Timeline
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Match Timeline & Highlights",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            val events = match.eventsSummary.split(";").map { it.trim() }.filter { it.isNotBlank() }
                            if (events.isEmpty()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = PitchCard),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "No recorded incidents yet. Match starts soon.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                events.forEach { event ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = PitchCard),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(PitchCardElevated, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = if (event.contains("⚽")) "⚽" else if (event.contains("🟨")) "🟨" else "⏱",
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                text = event,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Full Statistics
                    item {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "Detailed Match Statistics",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            StatComparisonBar(
                                label = "Ball Possession",
                                homeValue = "${match.homePossession}%",
                                awayValue = "${match.awayPossession}%",
                                homeRatio = match.homePossession / 100f
                            )
                            StatComparisonBar(
                                label = "Expected Goals (xG)",
                                homeValue = String.format("%.2f", match.homeXg),
                                awayValue = String.format("%.2f", match.awayXg),
                                homeRatio = if (match.homeXg + match.awayXg > 0f) match.homeXg / (match.homeXg + match.awayXg) else 0.5f
                            )
                            StatComparisonBar(
                                label = "Total Shots",
                                homeValue = "${match.homeShots}",
                                awayValue = "${match.awayShots}",
                                homeRatio = if (match.homeShots + match.awayShots > 0) match.homeShots.toFloat() / (match.homeShots + match.awayShots) else 0.5f
                            )
                            StatComparisonBar(
                                label = "Shots on Target",
                                homeValue = "${match.homeShotsOnTarget}",
                                awayValue = "${match.awayShotsOnTarget}",
                                homeRatio = if (match.homeShotsOnTarget + match.awayShotsOnTarget > 0) match.homeShotsOnTarget.toFloat() / (match.homeShotsOnTarget + match.awayShotsOnTarget) else 0.5f
                            )
                            StatComparisonBar(
                                label = "Corner Kicks",
                                homeValue = "${match.homeCorners}",
                                awayValue = "${match.awayCorners}",
                                homeRatio = if (match.homeCorners + match.awayCorners > 0) match.homeCorners.toFloat() / (match.homeCorners + match.awayCorners) else 0.5f
                            )
                            StatComparisonBar(
                                label = "Fouls Committed",
                                homeValue = "${match.homeFouls}",
                                awayValue = "${match.awayFouls}",
                                homeRatio = if (match.homeFouls + match.awayFouls > 0) match.homeFouls.toFloat() / (match.homeFouls + match.awayFouls) else 0.5f
                            )
                        }
                    }
                }
                2 -> {
                    // Info & Venue
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PitchCard),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Fixture Details",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                InfoRow(label = "Stadium", value = match.stadium)
                                InfoRow(label = "Referee", value = match.referee)
                                InfoRow(label = "Competition", value = match.league)
                                InfoRow(label = "Date / Phase", value = match.matchDate)
                                InfoRow(label = "Status", value = match.status)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun StatComparisonBar(
    label: String,
    homeValue: String,
    awayValue: String,
    homeRatio: Float
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PitchCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homeValue,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonGrassGreen
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = TextSecondary
                )
                Text(
                    text = awayValue,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two-tone comparison bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(PitchCardElevated)
            ) {
                Box(
                    modifier = Modifier
                        .weight(homeRatio.coerceIn(0.05f, 0.95f))
                        .fillMaxSize()
                        .background(NeonGrassGreen)
                )
                Box(
                    modifier = Modifier
                        .weight((1f - homeRatio).coerceIn(0.05f, 0.95f))
                        .fillMaxSize()
                        .background(ElectricCyan)
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}
