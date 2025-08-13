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
import tellme.sairajpatil108.tellme360.data.model.Series
import androidx.compose.ui.input.nestedscroll.nestedScroll
import compose.icons.FeatherIcons
import compose.icons.feathericons.Star
import compose.icons.feathericons.Tv

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesScreen(
    onNavigateToSeriesDetail: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    val categories = remember {
        listOf("All", "Adventure", "Education", "Entertainment", "Gaming", "Travel")
    }
    
    val seriesList = remember {
        listOf(
            Series(
                id = "1",
                title = "VR Adventure Series",
                description = "Epic adventures in virtual reality with stunning visuals",
                thumbnailUrl = "",
                episodeCount = 12,
                category = "Adventure",
                rating = 4.6f
            ),
            Series(
                id = "2",
                title = "Virtual Education",
                description = "Learn various subjects through immersive VR experiences",
                thumbnailUrl = "",
                episodeCount = 8,
                category = "Education",
                rating = 4.8f
            ),
            Series(
                id = "3",
                title = "VR Gaming Masters",
                description = "Master the art of VR gaming with expert tips and tricks",
                thumbnailUrl = "",
                episodeCount = 15,
                category = "Gaming",
                rating = 4.7f
            ),
            Series(
                id = "4",
                title = "Virtual Travel Guide",
                description = "Explore the world's most beautiful destinations in VR",
                thumbnailUrl = "",
                episodeCount = 20,
                category = "Travel",
                rating = 4.5f
            ),
            Series(
                id = "5",
                title = "VR Entertainment Hub",
                description = "The best in virtual reality entertainment and shows",
                thumbnailUrl = "",
                episodeCount = 10,
                category = "Entertainment",
                rating = 4.4f
            )
        )
    }
    
    val filteredSeries = seriesList.filter { series ->
        val matchesSearch = series.title.contains(searchQuery, ignoreCase = true) ||
                series.description.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || series.category == selectedCategory
        matchesSearch && matchesCategory
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Series", style = MaterialTheme.typography.titleLarge) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search series...") },
                leadingIcon = { Text("🔍") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Category filters
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }

            // Series list
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredSeries) { series ->
                    SeriesListItem(
                        series = series,
                        onClick = { onNavigateToSeriesDetail(series.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SeriesListItem(series: Series, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Series thumbnail
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = FeatherIcons.Tv, contentDescription = null)
            }
            
            // Series info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = series.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = series.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        Icon(imageVector = FeatherIcons.Star, contentDescription = null)
                        Text(
                            text = series.rating.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = FeatherIcons.Tv, contentDescription = null)
                        Text(
                            text = "${series.episodeCount} episodes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = series.category,
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
