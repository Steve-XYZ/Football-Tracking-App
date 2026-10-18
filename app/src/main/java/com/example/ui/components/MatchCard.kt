package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@Composable
fun MatchCard(
    match: MatchEntity,
    isHomeFavorite: Boolean,
    isAwayFavorite: Boolean,
    onToggleFavorite: (String, String) -> Unit,
    onClickMatch: (MatchEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = match.status == "LIVE"

    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val liveAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "livePulseAlpha"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = PitchCard),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLive) NeonGrassGreen.copy(alpha = 0.4f) else PitchBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClickMatch(match) }
            .testTag("match_card_${match.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: League & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${match.league} • ${match.matchDate}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (match.status) {
                        "LIVE" -> LiveAlertRed.copy(alpha = 0.15f)
                        "FINISHED" -> PitchCardElevated
                        else -> ElectricCyan.copy(alpha = 0.15f)
                    },
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (match.status) {
                            "LIVE" -> LiveAlertRed.copy(alpha = 0.6f)
                            "FINISHED" -> PitchBorder
                            else -> ElectricCyan.copy(alpha = 0.4f)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLive) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .alpha(liveAlpha)
                                    .background(LiveAlertRed, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (isLive) "${match.minute} LIVE" else match.minute,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (match.status) {
                                "LIVE" -> LiveAlertRed
                                "FINISHED" -> TextSecondary
                                else -> ElectricCyan
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scoreboard Area: Home Team vs Away Team
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = { onToggleFavorite(match.homeTeam, match.league) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isHomeFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite ${match.homeTeam}",
                            tint = if (isHomeFavorite) TrophyGold else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.homeTeam,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Scores
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PitchDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (match.status == "UPCOMING") {
                            Text(
                                text = "vs",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary
                            )
                        } else {
                            Text(
                                text = "${match.homeScore}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                ),
                                color = if (isLive) NeonGrassGreen else TextPrimary
                            )
                            Text(
                                text = " : ",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextMuted
                            )
                            Text(
                                text = "${match.awayScore}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                ),
                                color = if (isLive) NeonGrassGreen else TextPrimary
                            )
                        }
                    }
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = match.awayTeam,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { onToggleFavorite(match.awayTeam, match.league) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isAwayFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite ${match.awayTeam}",
                            tint = if (isAwayFavorite) TrophyGold else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Events Summary / Scorers
            if (match.eventsSummary.isNotBlank() && match.status != "UPCOMING") {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PitchCardElevated.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = match.eventsSummary,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer link
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.stadium,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onClickMatch(match) }
                ) {
                    Text(
                        text = "Stats & Timeline",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open stats",
                        tint = ElectricCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
