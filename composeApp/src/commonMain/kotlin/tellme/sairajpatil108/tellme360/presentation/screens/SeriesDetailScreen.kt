package tellme.sairajpatil108.tellme360.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tellme.sairajpatil108.tellme360.data.model.Series
import tellme.sairajpatil108.tellme360.data.model.VideoContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    seriesId: String,
    onBackPressed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Mock series data based on seriesId
    val series = remember(seriesId) {
        Series(
            id = seriesId,
            title = "VR Adventure Series",
            description = "Embark on epic adventures in virtual reality with stunning visuals and immersive experiences. This series takes you to the most breathtaking locations and thrilling scenarios, all from the comfort of your VR headset.",
            thumbnailUrl = "",
            episodeCount = 12,
            category = "Adventure",
            rating = 4.6f
        )
    }
    
    val episodes = remember {
        listOf(
            VideoContent(
                id = "ep1",
                title = "Episode 1: The Beginning",
                description = "Start your VR adventure with an introduction to virtual reality",
                thumbnailUrl = "",
                videoUrl = "",
                duration = "15:30",
                category = "Adventure",
                rating = 4.5f,
                views = 12000
            ),
            VideoContent(
                id = "ep2",
                title = "Episode 2: Mountain Climb",
                description = "Scale the highest peaks in virtual reality",
                thumbnailUrl = "",
                videoUrl = "",
                duration = "22:15",
                category = "Adventure",
                rating = 4.7f,
                views = 15000
            ),
            VideoContent(
                id = "ep3",
                title = "Episode 3: Ocean Depths",
                description = "Explore the mysterious depths of the ocean",
                thumbnailUrl = "",
                videoUrl = "",
                duration = "18:45",
                category = "Adventure",
                rating = 4.6f,
                views = 13500
            ),
            VideoContent(
                id = "ep4",
                title = "Episode 4: Space Journey",
                description = "Travel through the cosmos in this epic space adventure",
                thumbnailUrl = "",
                videoUrl = "",
                duration = "25:20",
                category = "Adventure",
                rating = 4.8f,
                views = 18000
            )
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            // Series header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Placeholder for series thumbnail
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📺",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                
                // Back button
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "←",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
                
                // Play button
                FloatingActionButton(
                    onClick = { /* Handle play series */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "▶️",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        
        item {
            // Series info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = series.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "📺",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${series.episodeCount} episodes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = series.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = series.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Handle download */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📥")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download")
                    }
                    
                    OutlinedButton(
                        onClick = { /* Handle share */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📤")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share")
                    }
                }
            }
        }
        
        item {
            // Episodes header
            Text(
                text = "Episodes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        
        // Episodes list
        items(episodes) { episode ->
            EpisodeCard(
                episode = episode,
                onClick = { /* Handle episode click */ },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun EpisodeCard(
    episode: VideoContent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Episode thumbnail
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶️",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            // Episode info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = episode.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = episode.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = episode.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "⭐",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = episode.rating.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
