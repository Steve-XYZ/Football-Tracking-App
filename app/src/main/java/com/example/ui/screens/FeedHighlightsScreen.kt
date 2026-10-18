package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.NewsHighlightEntity
import com.example.ui.theme.ElectricCyan
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

@Composable
fun FeedHighlightsScreen(
    newsList: List<NewsHighlightEntity>,
    favoriteTeams: List<FavoriteTeamEntity>,
    onToggleBookmark: (NewsHighlightEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }
    var activePreviewHighlight by remember { mutableStateOf<NewsHighlightEntity?>(null) }

    val categories = listOf("All", "Goal Highlight", "Tactics", "Match Recap", "Breaking News")
    val favNames = favoriteTeams.map { it.teamName.lowercase() }

    val filteredNews = newsList.filter { item ->
        val categoryMatch = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
        val favoriteMatch = !showOnlyFavorites || favNames.contains(item.relatedTeam.lowercase())
        categoryMatch && favoriteMatch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDark)
            .padding(top = 10.dp)
            .testTag("feed_highlights_screen")
    ) {
        // Toggle: All vs My Favorite Teams
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (!showOnlyFavorites) NeonGrassGreen else PitchCard,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (!showOnlyFavorites) NeonGrassGreen else PitchBorder
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showOnlyFavorites = false }
            ) {
                Text(
                    text = "All Highlights",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (!showOnlyFavorites) PitchDark else TextSecondary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (showOnlyFavorites) TrophyGold else PitchCard,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (showOnlyFavorites) TrophyGold else PitchBorder
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showOnlyFavorites = true }
                    .testTag("filter_favorite_feed_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "My Teams",
                        tint = if (showOnlyFavorites) PitchDark else TrophyGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "My Teams Feed",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (showOnlyFavorites) PitchDark else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) PitchDark else TextSecondary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        containerColor = PitchCard
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

        // Highlights List
        if (filteredNews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "No highlights",
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No highlights match your filter",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try switching back to 'All Highlights' or star more teams in Favorites.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(filteredNews) { item ->
                    HighlightCard(
                        item = item,
                        isFavoriteTeam = favNames.contains(item.relatedTeam.lowercase()),
                        onPlayClick = { activePreviewHighlight = item },
                        onBookmarkClick = { onToggleBookmark(item) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Video Highlight Player Preview Modal
    if (activePreviewHighlight != null) {
        val item = activePreviewHighlight!!
        AlertDialog(
            onDismissRequest = { activePreviewHighlight = null },
            confirmButton = {
                Button(
                    onClick = { activePreviewHighlight = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGrassGreen)
                ) {
                    Text("Close Replay", color = PitchDark, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = PitchCard,
            title = {
                Text(
                    text = "🎥 Match Highlight Replay",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NeonGrassGreen
                )
            },
            text = {
                Column {
                    // Simulated Pitch / Video Player Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PitchDark)
                            .border(1.dp, PitchBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SportsSoccer,
                                contentDescription = "Replay",
                                tint = NeonGrassGreen,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "HD Video Replay (${item.videoDuration ?: "1:00"})",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.keyTimestamp ?: "Key Moment"} • ${item.relatedTeam}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { 0.65f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = NeonGrassGreen,
                        trackColor = PitchCardElevated,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        )
    }
}

@Composable
fun HighlightCard(
    item: NewsHighlightEntity,
    isFavoriteTeam: Boolean,
    onPlayClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PitchCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, PitchBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onPlayClick() }
            .testTag("highlight_card_${item.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge & Team
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (item.category) {
                            "Goal Highlight" -> NeonGrassGreen.copy(alpha = 0.15f)
                            "Tactics" -> ElectricCyan.copy(alpha = 0.15f)
                            else -> TrophyGold.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = item.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                            color = when (item.category) {
                                "Goal Highlight" -> NeonGrassGreen
                                "Tactics" -> ElectricCyan
                                else -> TrophyGold
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (item.keyTimestamp != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.keyTimestamp,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isFavoriteTeam) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite team",
                            tint = TrophyGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = item.relatedTeam,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isFavoriteTeam) TrophyGold else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Replay Thumbnail banner (if duration exists)
            if (item.videoDuration != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PitchDark)
                        .border(1.dp, PitchBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonGrassGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play highlight",
                                tint = PitchDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Play Clip (${item.videoDuration})",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Headline
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.publishedAgo} • ${item.views} views",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (item.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (item.isBookmarked) NeonGrassGreen else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
