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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tellme.sairajpatil108.tellme360.data.model.VideoContent

import androidx.compose.ui.input.nestedscroll.nestedScroll
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import compose.icons.feathericons.Star
import compose.icons.feathericons.Eye
import compose.icons.feathericons.Search
import compose.icons.feathericons.X
import compose.icons.feathericons.Download
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import tellme.sairajpatil108.tellme360.presentation.components.CommonAppBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VRVideosScreen(
    onNavigateToVideoDetail: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {

    var selectedFilter by remember { mutableStateOf("All") }
    
     val filters = remember {
        listOf("All", "Test")
    }
    
    val vrVideos = remember {
        listOf(
            VideoContent(
                id = "1",
                title = "Sample VR Test Video",
                description = "This is a sample 360-degree VR video for testing purposes. Experience immersive virtual reality content.",
                thumbnailUrl = "",
                videoUrl = "https://tellme360.media/videos/2025-07-30/14-29-48-615460/mp4_files/1080p.mp4",
                duration = "10:00",
                category = "Test",
                isVR = true,
                rating = 5.0f,
                views = 1000
            )
        )
    }
    
    val filteredVideos = if (selectedFilter == "All") {
        vrVideos
    } else {
        vrVideos.filter { it.category == selectedFilter }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CommonAppBar(title = "360° Stories")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Search bar

                    var searchQuery by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search 360° stories...") },
                        leadingIcon = { Icon(FeatherIcons.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(FeatherIcons.X, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

            item {
                // Categories row
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
            }
            item {
                // Results count placeholder to mirror old UI
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filteredVideos.size} stories found",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                // 2-column grid like old app; disable inner scroll and provide height
                val rows = (filteredVideos.size + 1) / 2
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((rows * 200).dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = false
                ) {
                    items(filteredVideos) { video ->
                        VideoGridCard(video) {
                            onNavigateToVideoDetail(video.id)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun VRVideoCard(video: VideoContent, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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
                Icon(
                    imageVector = FeatherIcons.Play,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            // Gradient overlay for legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xCC000000)),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

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
                    .padding(16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleLarge,
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
                        Icon(
                            imageVector = FeatherIcons.Star,
                            contentDescription = null,
                            tint = Color.White
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
                        Icon(
                            imageVector = FeatherIcons.Eye,
                            contentDescription = null,
                            tint = Color.White
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

@Composable
fun VideoGridCard(video: VideoContent, onClick: () -> Unit) {
    var showDownloadDialog by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Placeholder for thumbnail (remove Coil to keep commonMain dependency-free)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Download button overlay
            IconButton(
                onClick = { showDownloadDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = FeatherIcons.Download,
                    contentDescription = "Download",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = video.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = video.duration,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
    
    // Download confirmation dialog
    if (showDownloadDialog) {
        AlertDialog(
            onDismissRequest = { 
                if (!isDownloading) showDownloadDialog = false 
            },
            title = { Text("Download Video") },
            text = { 
                if (isDownloading) {
                    Column {
                        Text("Downloading 360° video...")
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = downloadProgress,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "${(downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (downloadStatus.isNotEmpty()) {
                            Text(
                                text = downloadStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Text("Would you like to download this 360° video for offline viewing?")
                }
            },
            confirmButton = {
                if (isDownloading) {
                    TextButton(
                        onClick = { /* Download in progress, can't cancel for now */ }
                    ) {
                        Text("Downloading...")
                    }
                } else {
                    TextButton(
                        onClick = {
                            isDownloading = true
                            downloadProgress = 0f
                            downloadStatus = "Starting download..."
                            
                            // Simulate download progress
                            // In a real app, this would be implemented using platform-specific download managers
                            coroutineScope.launch {
                                repeat(100) { progress ->
                                    delay(50)
                                    downloadProgress = progress / 100f
                                    downloadStatus = when {
                                        progress < 30 -> "Connecting to server..."
                                        progress < 70 -> "Downloading video data..."
                                        progress < 90 -> "Processing video..."
                                        else -> "Finalizing download..."
                                    }
                                }
                                downloadStatus = "Download completed!"
                                delay(1000)
                                isDownloading = false
                                showDownloadDialog = false
                            }
                        }
                    ) {
                        Text("Download")
                    }
                }
            },
            dismissButton = {
                if (!isDownloading) {
                    TextButton(onClick = { showDownloadDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
