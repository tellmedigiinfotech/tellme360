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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tellme.sairajpatil108.tellme360.data.model.VideoContent
import tellme.sairajpatil108.tellme360.platform.rememberVRVideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VRVideosScreen(
    modifier: Modifier = Modifier
) {
    val vrVideoPlayer = rememberVRVideoPlayer()
    var selectedFilter by remember { mutableStateOf("All") }
    
    val filters = remember {
        listOf("All", "Gaming", "Education", "Entertainment", "Travel", "Sports")
    }
    
    val vrVideos = remember {
        listOf(
            VideoContent(
                id = "1",
                title = "VR Gaming Experience",
                description = "Immerse yourself in the world of virtual reality gaming",
                thumbnailUrl = "",
                videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                duration = "25:30",
                category = "Gaming",
                isVR = true,
                rating = 4.8f,
                views = 35000
            ),
            VideoContent(
                id = "2",
                title = "Virtual Travel: Paris",
                description = "Explore the beautiful city of Paris in VR",
                thumbnailUrl = "",
                videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                duration = "18:45",
                category = "Travel",
                isVR = true,
                rating = 4.6f,
                views = 22000
            ),
            VideoContent(
                id = "3",
                title = "VR Education: Solar System",
                description = "Learn about planets in an immersive VR experience",
                thumbnailUrl = "",
                videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                duration = "32:15",
                category = "Education",
                isVR = true,
                rating = 4.9f,
                views = 28000
            ),
            VideoContent(
                id = "4",
                title = "VR Sports: Basketball",
                description = "Experience basketball like never before in VR",
                thumbnailUrl = "",
                videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                duration = "15:20",
                category = "Sports",
                isVR = true,
                rating = 4.4f,
                views = 18000
            ),
            VideoContent(
                id = "5",
                title = "VR Entertainment: Concert",
                description = "Attend a virtual concert from the front row",
                thumbnailUrl = "",
                videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg",
                duration = "45:30",
                category = "Entertainment",
                isVR = true,
                rating = 4.7f,
                views = 42000
            )
        )
    }
    
    val filteredVideos = if (selectedFilter == "All") {
        vrVideos
    } else {
        vrVideos.filter { it.category == selectedFilter }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Header
        TopAppBar(
            title = { Text("VR Videos") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        
        // Filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) }
                )
            }
        }
        
        // Videos grid
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredVideos) { video ->
                VRVideoCard(
                    video = video,
                    onClick = {
                        // Launch VR video player activity
                        vrVideoPlayer.playVideo(video.videoUrl, video.title)
                    }
                )
            }
        }
    }
}

@Composable
fun VRVideoCard(video: VideoContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Video thumbnail placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▶️",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            
            // VR badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "VR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            
            // Duration badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = video.duration,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
            
            // Video info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = video.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
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
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = video.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "👁️",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${video.views / 1000}k",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = video.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
