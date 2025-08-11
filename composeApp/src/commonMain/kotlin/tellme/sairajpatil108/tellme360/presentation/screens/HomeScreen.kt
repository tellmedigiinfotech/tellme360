package tellme.sairajpatil108.tellme360.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tellme.sairajpatil108.tellme360.data.model.VideoContent
import tellme.sairajpatil108.tellme360.data.model.Series
import tellme.sairajpatil108.tellme360.data.model.ContentCategory
import tellme.sairajpatil108.tellme360.platform.rememberVRVideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSeries: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val vrVideoPlayer = rememberVRVideoPlayer()
    
    // Mock data for demonstration
    val featuredVideos = remember {
        listOf(
            VideoContent(
                id = "1",
                title = "Amazing VR Experience",
                description = "Experience the future of entertainment",
                thumbnailUrl = "",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                duration = "15:30",
                category = "VR",
                isVR = true,
                rating = 4.5f,
                views = 15000
            ),
            VideoContent(
                id = "2",
                title = "Virtual Reality Gaming",
                description = "Immerse yourself in virtual worlds",
                thumbnailUrl = "",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                duration = "22:15",
                category = "Gaming",
                isVR = true,
                rating = 4.8f,
                views = 25000
            )
        )
    }
    
    val categories = remember {
        listOf(
            ContentCategory("1", "VR Videos", "vr"),
            ContentCategory("2", "Gaming", "game"),
            ContentCategory("3", "Education", "edu"),
            ContentCategory("4", "Entertainment", "ent")
        )
    }
    
    val recentVideos = remember {
        listOf(
            VideoContent(
                id = "3",
                title = "VR Tutorial for Beginners",
                description = "Learn the basics of VR",
                thumbnailUrl = "",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                duration = "8:45",
                category = "Tutorial",
                rating = 4.2f,
                views = 8500
            )
        )
    }
    
    val featuredSeries = remember {
        listOf(
            Series(
                id = "1",
                title = "VR Adventure Series",
                description = "Epic adventures in virtual reality",
                thumbnailUrl = "",
                episodeCount = 12,
                category = "Adventure",
                rating = 4.6f
            )
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            // Header
            Text(
                text = "Welcome to TellMe360",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        item {
            // Categories
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category = category)
                }
            }
        }
        
        item {
            // Featured Videos
            Text(
                text = "Featured Videos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(featuredVideos) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            // Launch VR video player for VR videos
                            if (video.isVR) {
                                vrVideoPlayer.playVideo(video.videoUrl, video.title)
                            }
                        }
                    )
                }
            }
        }
        
        item {
            // Recent Videos
            Text(
                text = "Recently Added",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(recentVideos) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            // Launch VR video player for VR videos
                            if (video.isVR) {
                                vrVideoPlayer.playVideo(video.videoUrl, video.title)
                            }
                        }
                    )
                }
            }
        }
        
        item {
            // Featured Series
            Text(
                text = "Featured Series",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(featuredSeries) { series ->
                    SeriesCard(
                        series = series,
                        onClick = { onNavigateToSeries(series.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: ContentCategory) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clickable { /* Handle category click */ },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📁",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun VideoCard(video: VideoContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            // Placeholder for video thumbnail
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶️",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            // Video info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            // VR badge
            if (video.isVR) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "VR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SeriesCard(series: Series, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            // Placeholder for series thumbnail
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📺",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            
            // Series info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${series.episodeCount} episodes",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
