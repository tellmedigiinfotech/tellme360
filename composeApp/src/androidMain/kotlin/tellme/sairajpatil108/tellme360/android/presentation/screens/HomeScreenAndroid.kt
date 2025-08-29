package tellme.sairajpatil108.tellme360.android.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tellme.sairajpatil108.tellme360.data.model.VideoContent
import tellme.sairajpatil108.tellme360.data.model.Series
import tellme.sairajpatil108.tellme360.data.model.ContentCategory
import tellme.sairajpatil108.tellme360.platform.rememberVRVideoPlayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import compose.icons.FeatherIcons
import compose.icons.feathericons.Grid
import compose.icons.feathericons.Play
import compose.icons.feathericons.Tv
import compose.icons.feathericons.AlertCircle
import tellme.sairajpatil108.tellme360.android.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenAndroid(
    onNavigateToSeries: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    contentViewModel: ContentViewModel = viewModel()
) {
    val vrVideoPlayer = rememberVRVideoPlayer()
    
    // Observe data from ViewModel
    val isLoading by contentViewModel.isLoading.observeAsState(initial = false)
    val error by contentViewModel.error.observeAsState()
    val categories by contentViewModel.categories.observeAsState(initial = emptyList())
    val allVideos by contentViewModel.allVideos.observeAsState(initial = emptyList())
    val allSeries by contentViewModel.allSeries.observeAsState(initial = emptyList())
    
    // Derived state for featured content
    val featuredVideos = remember(allVideos) {
        allVideos.sortedByDescending { it.rating }.take(5)
    }
    
    val recentVideos = remember(allVideos) {
        allVideos.sortedByDescending { it.views }.take(5)
    }
    
    val featuredSeries = remember(allSeries) {
        allSeries.sortedByDescending { it.rating }.take(5)
    }
    


    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Welcome to TellMe360", 
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis, 
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        
        // Error handling
        error?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                // You could show a snackbar here if needed
            }
        }
        
        if (isLoading && allVideos.isEmpty()) {
            // Show loading state for initial load
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading content...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else if (error != null && allVideos.isEmpty()) {
            // Show error state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = FeatherIcons.AlertCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Failed to load content",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { contentViewModel.refreshAllData() }
                    ) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    // Header
                    Spacer(modifier = Modifier.height(4.dp))
                }
                

                
                // Categories section
                if (categories.isNotEmpty()) {
                    item {
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
                                CategoryCard(
                                    category = category,
                                    onClick = {
                                        contentViewModel.loadVideosByCategory(category.name)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Featured Videos section
                if (featuredVideos.isNotEmpty()) {
                    item {
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
                                        vrVideoPlayer.playVideo(video.videoUrl, video.title)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Recent Videos section
                if (recentVideos.isNotEmpty()) {
                    item {
                        Text(
                            text = "Popular Videos",
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
                                        vrVideoPlayer.playVideo(video.videoUrl, video.title)
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Featured Series section
                if (featuredSeries.isNotEmpty()) {
                    item {
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
        }
    }
}

@Composable
fun CategoryCard(category: ContentCategory, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp)
            .clickable { onClick() },
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
                    text = category.icon,
                    style = MaterialTheme.typography.titleLarge
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
            .height(140.dp)
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
                Icon(
                    imageVector = FeatherIcons.Play, 
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Video info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = video.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "★ ${String.format("%.1f", video.rating)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
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
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
            .height(140.dp)
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
                Icon(
                    imageVector = FeatherIcons.Tv, 
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Series info overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${series.episodeCount} episodes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "★ ${String.format("%.1f", series.rating)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
