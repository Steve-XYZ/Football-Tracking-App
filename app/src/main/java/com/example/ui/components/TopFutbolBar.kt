package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NetworkSimState
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LiveAlertRed
import com.example.ui.theme.NeonGrassGreen
import com.example.ui.theme.PitchBorder
import com.example.ui.theme.PitchCard
import com.example.ui.theme.PitchSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@Composable
fun TopFutbolBar(
    networkSimState: NetworkSimState,
    isSyncing: Boolean,
    notificationsEnabled: Boolean,
    onNetworkStateChanged: (NetworkSimState) -> Unit,
    onRefreshClicked: () -> Unit,
    onRequestNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    var networkMenuExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Surface(
        color = PitchSurface,
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_app_bar")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title & Live Pulse Dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("app_branding_row")
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(pulseAlpha)
                            .background(
                                color = if (networkSimState == NetworkSimState.OFFLINE_CACHE) TrophyGold else NeonGrassGreen,
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FUTBOL",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        text = "PULSE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = NeonGrassGreen
                    )
                }

                // Actions: Network State Chip, Notifications Bell, Sync
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Network state selector chip
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when (networkSimState) {
                                NetworkSimState.ONLINE_FAST -> PitchCard
                                NetworkSimState.POOR_CONNECTION -> PitchCard
                                NetworkSimState.OFFLINE_CACHE -> PitchCard
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (networkSimState) {
                                    NetworkSimState.ONLINE_FAST -> NeonGrassGreen.copy(alpha = 0.5f)
                                    NetworkSimState.POOR_CONNECTION -> TrophyGold.copy(alpha = 0.5f)
                                    NetworkSimState.OFFLINE_CACHE -> LiveAlertRed.copy(alpha = 0.5f)
                                }
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { networkMenuExpanded = true }
                                .testTag("network_mode_chip")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (networkSimState) {
                                        NetworkSimState.ONLINE_FAST -> Icons.Default.Speed
                                        NetworkSimState.POOR_CONNECTION -> Icons.Default.CellTower
                                        NetworkSimState.OFFLINE_CACHE -> Icons.Default.Flight
                                    },
                                    contentDescription = networkSimState.label,
                                    tint = when (networkSimState) {
                                        NetworkSimState.ONLINE_FAST -> NeonGrassGreen
                                        NetworkSimState.POOR_CONNECTION -> TrophyGold
                                        NetworkSimState.OFFLINE_CACHE -> LiveAlertRed
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (networkSimState) {
                                        NetworkSimState.ONLINE_FAST -> "Fast"
                                        NetworkSimState.POOR_CONNECTION -> "Weak 2G"
                                        NetworkSimState.OFFLINE_CACHE -> "Offline"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = networkMenuExpanded,
                            onDismissRequest = { networkMenuExpanded = false },
                            modifier = Modifier.background(PitchCard)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("⚡ Online (Fast)", color = NeonGrassGreen, fontWeight = FontWeight.Bold)
                                        Text("Instant real-time updates", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                },
                                onClick = {
                                    networkMenuExpanded = false
                                    onNetworkStateChanged(NetworkSimState.ONLINE_FAST)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("📶 Weak 2G (High Latency)", color = TrophyGold, fontWeight = FontWeight.Bold)
                                        Text("Simulates bad connection / lag", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                },
                                onClick = {
                                    networkMenuExpanded = false
                                    onNetworkStateChanged(NetworkSimState.POOR_CONNECTION)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text("✈️ Offline Cache Mode", color = LiveAlertRed, fontWeight = FontWeight.Bold)
                                        Text("Tests pure Room offline storage", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    }
                                },
                                onClick = {
                                    networkMenuExpanded = false
                                    onNetworkStateChanged(NetworkSimState.OFFLINE_CACHE)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Notification toggle indicator
                    IconButton(
                        onClick = onRequestNotifications,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("notification_permission_button")
                    ) {
                        Icon(
                            imageVector = if (notificationsEnabled) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = "Goal Notifications",
                            tint = if (notificationsEnabled) NeonGrassGreen else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Refresh Button
                    IconButton(
                        onClick = onRefreshClicked,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("refresh_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ElectricCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
