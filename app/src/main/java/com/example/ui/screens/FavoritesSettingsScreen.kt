package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.NetworkSimState
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FavoritesSettingsScreen(
    favoriteTeams: List<FavoriteTeamEntity>,
    networkSimState: NetworkSimState,
    notificationsEnabled: Boolean,
    lastSyncTimestamp: Long,
    onToggleFavorite: (String, String) -> Unit,
    onToggleNotification: (String, Boolean) -> Unit,
    onNetworkStateChanged: (NetworkSimState) -> Unit,
    onRequestNotifications: () -> Unit,
    onTestGoalAlert: () -> Unit,
    onRefreshCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allPopularClubs = listOf(
        Pair("Real Madrid", "La Liga"),
        Pair("Barcelona", "La Liga"),
        Pair("Arsenal", "Premier League"),
        Pair("Manchester City", "Premier League"),
        Pair("Liverpool", "Premier League"),
        Pair("Bayern Munich", "Bundesliga"),
        Pair("Inter Milan", "Serie A"),
        Pair("PSG", "UEFA Champions League"),
        Pair("Borussia Dortmund", "Bundesliga"),
        Pair("Juventus", "Serie A"),
        Pair("Chelsea", "Premier League"),
        Pair("Atletico Madrid", "La Liga")
    )

    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val lastSyncStr = dateFormat.format(Date(lastSyncTimestamp))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDark)
            .padding(16.dp)
            .testTag("favorites_settings_screen")
    ) {
        // Section: Real-time Goal Notifications Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PitchCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Goal alerts",
                                tint = NeonGrassGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Real-Time Goal Alerts",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (notificationsEnabled) "Push notifications active" else "Permission required",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (notificationsEnabled) NeonGrassGreen else TrophyGold
                                )
                            }
                        }

                        if (!notificationsEnabled) {
                            Button(
                                onClick = onRequestNotifications,
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGrassGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Enable", color = PitchDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTestGoalAlert,
                        colors = ButtonDefaults.buttonColors(containerColor = PitchCardElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGrassGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("test_goal_alert_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Test goal",
                            tint = NeonGrassGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simulate Instant Goal Alert ⚽",
                            color = NeonGrassGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Network Condition & Offline Mode Resilience
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PitchCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CellTower,
                            contentDescription = "Network Resilience",
                            tint = ElectricCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Network Resilience & Offline Cache",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Futbol Pulse uses a Room database single source of truth. It operates flawlessly with poor internet or completely offline.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Radio options for network simulation
                    NetworkModeOption(
                        state = NetworkSimState.ONLINE_FAST,
                        currentState = networkSimState,
                        title = "Fast Online ⚡",
                        subtitle = "Instant live goal streams and zero latency",
                        icon = Icons.Default.Speed,
                        color = NeonGrassGreen,
                        onSelect = { onNetworkStateChanged(NetworkSimState.ONLINE_FAST) }
                    )

                    NetworkModeOption(
                        state = NetworkSimState.POOR_CONNECTION,
                        currentState = networkSimState,
                        title = "Weak 2G / High Latency 📶",
                        subtitle = "Simulates stadium crowded networks; serves cached data",
                        icon = Icons.Default.CellTower,
                        color = TrophyGold,
                        onSelect = { onNetworkStateChanged(NetworkSimState.POOR_CONNECTION) }
                    )

                    NetworkModeOption(
                        state = NetworkSimState.OFFLINE_CACHE,
                        currentState = networkSimState,
                        title = "Offline Cache Mode ✈️",
                        subtitle = "Zero network calls; reads 100% from Room database",
                        icon = Icons.Default.Flight,
                        color = LiveAlertRed,
                        onSelect = { onNetworkStateChanged(NetworkSimState.OFFLINE_CACHE) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Cache status footer
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = PitchCardElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Room Cache: Active",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonGrassGreen
                                )
                                Text(
                                    text = "Last synced: $lastSyncStr",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            IconButton(
                                onClick = onRefreshCache,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh cache",
                                    tint = ElectricCyan
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Section: Favorite Teams List
        item {
            Text(
                text = "Favorite Teams & Club Goal Alerts",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Star clubs to prioritize in live match views and get instant goal notifications.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(allPopularClubs) { clubPair ->
            val teamName = clubPair.first
            val league = clubPair.second
            val favEntity = favoriteTeams.find { it.teamName.equals(teamName, ignoreCase = true) }
            val isFavorite = favEntity != null
            val isNotifyOn = favEntity?.notifyGoals ?: true

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFavorite) TrophyGold.copy(alpha = 0.08f) else PitchCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isFavorite) TrophyGold.copy(alpha = 0.4f) else PitchBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("favorite_team_row_$teamName")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = { onToggleFavorite(teamName, league) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Star $teamName",
                                tint = if (isFavorite) TrophyGold else TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = teamName,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isFavorite) TrophyGold else TextPrimary
                            )
                            Text(
                                text = league,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    if (isFavorite) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Goal alerts",
                                tint = if (isNotifyOn) NeonGrassGreen else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isNotifyOn,
                                onCheckedChange = { notify -> onToggleNotification(teamName, notify) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NeonGrassGreen,
                                    checkedTrackColor = PitchCardElevated,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = PitchDark
                                ),
                                modifier = Modifier.testTag("toggle_notify_$teamName")
                            )
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

@Composable
fun NetworkModeOption(
    state: NetworkSimState,
    currentState: NetworkSimState,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (state == currentState) color.copy(alpha = 0.1f) else PitchDark,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (state == currentState) color else PitchBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = state == currentState,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = color, unselectedColor = TextMuted)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (state == currentState) color else TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }
        }
    }
}
