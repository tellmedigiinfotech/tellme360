package tellme.sairajpatil108.tellme360.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Download
import compose.icons.feathericons.Play
import compose.icons.feathericons.Share2
import compose.icons.feathericons.Star
import compose.icons.feathericons.Tv
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tellme.sairajpatil108.tellme360.data.model.Series
import tellme.sairajpatil108.tellme360.data.model.VideoContent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.StarOutline

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
                id = "1",
                title = "Sample VR Test Video",
                description = "This is a sample 360-degree VR video for testing purposes. Experience immersive virtual reality content.",
                thumbnailUrl = "",
                videoUrl = "https://tellme360.media/videos/2025-07-30/14-29-48-615460/mp4_files/1080p.mp4",
                duration = "10:00",
                category = "Test",
                rating = 5.0f,
                views = 1000
            )
        )
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(series.title, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = FeatherIcons.ArrowLeft, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                    Icon(imageVector = FeatherIcons.Tv, contentDescription = null)
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xB3000000)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
                
                // Back button
                // Moved to top bar navigation icon
                
                // Play button
                FloatingActionButton(
                    onClick = { /* Handle play series */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(imageVector = FeatherIcons.Play, contentDescription = "Play")
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
                        Icon(imageVector = FeatherIcons.Star, contentDescription = null)
                        Text(
                            text = series.rating.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = FeatherIcons.Tv, contentDescription = null)
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
                        Icon(imageVector = FeatherIcons.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download")
                    }
                    
                    OutlinedButton(
                        onClick = { /* Handle share */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = FeatherIcons.Share2, contentDescription = null)
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
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
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
