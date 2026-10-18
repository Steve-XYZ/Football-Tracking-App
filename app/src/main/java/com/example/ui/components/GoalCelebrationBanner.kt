package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalAlert
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGrassGreen
import com.example.ui.theme.PitchCardElevated
import com.example.ui.theme.PitchDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@Composable
fun GoalCelebrationBanner(
    goalAlert: GoalAlert?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = goalAlert != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (goalAlert != null) {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(
                    PitchDark,
                    PitchCardElevated,
                    PitchDark
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(gradientBrush)
                    .border(1.5.dp, NeonGrassGreen, RoundedCornerShape(14.dp))
                    .clickable { onDismiss() }
                    .testTag("goal_celebration_banner")
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
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGrassGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "Goal",
                                tint = NeonGrassGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "⚽ GOOOAL!",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    ),
                                    color = NeonGrassGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = goalAlert.minute,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ElectricCyan
                                )
                                if (goalAlert.isFavoriteTeam) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Favorite team",
                                        tint = TrophyGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${goalAlert.scorer} • ${goalAlert.team}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Score: ${goalAlert.newHomeScore} - ${goalAlert.newAwayScore} (${goalAlert.opponent})",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
