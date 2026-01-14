package at.ustp.dolap.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.model.Category
import at.ustp.dolap.model.ClothingColors
import at.ustp.dolap.model.Season
import at.ustp.dolap.model.Size
import at.ustp.dolap.ui.components.DropdownField
import at.ustp.dolap.viewmodel.ClothingViewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import at.ustp.dolap.data.local.TagEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: ClothingViewModel,
    onBack: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val query by viewModel.searchQuery.collectAsState()
    val category by viewModel.categoryFilter.collectAsState()
    val color by viewModel.colorFilter.collectAsState()
    val size by viewModel.sizeFilter.collectAsState()
    val season by viewModel.seasonFilter.collectAsState()
    val sort by viewModel.sortOption.collectAsState()
    val selectedTagIds by viewModel.selectedTagIds.collectAsState()

    val filtered by viewModel.filteredClothes.collectAsState(initial = emptyList())

    val categoryOptions = remember { listOf("All") + Category.values().map { it.label } }
    val colorOptions = remember { listOf("All") + ClothingColors }
    val sizeOptions = remember { listOf("All", "No Size") + Size.values().map { it.label } }
    val seasonOptions = remember { listOf("All", "No Season") + Season.values().map { it.label } }
    val sortOptions = remember { listOf("Newest", "Name A-Z", "Name Z-A") }

    val hasActiveFilters =
        query.isNotBlank() ||
                category != "All" ||
                color != "All" ||
                size != "All" ||
                season != "All" ||
                sort != "Newest" ||
                selectedTagIds.isNotEmpty()

    val scrollState = rememberScrollState()

    val allTags by viewModel.allTags.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                label = { Text("Search by name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )

            // Filters row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownField(
                    label = "Category",
                    options = categoryOptions,
                    selected = category,
                    onSelectedChange = { viewModel.setCategoryFilter(it) },
                    modifier = Modifier.weight(1f)
                )

                DropdownField(
                    label = "Color",
                    options = colorOptions,
                    selected = color,
                    onSelectedChange = { viewModel.setColorFilter(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Filters row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DropdownField(
                    label = "Size",
                    options = sizeOptions,
                    selected = size,
                    onSelectedChange = { viewModel.setSizeFilter(it) },
                    modifier = Modifier.weight(1f)
                )

                DropdownField(
                    label = "Season",
                    options = seasonOptions,
                    selected = season,
                    onSelectedChange = { viewModel.setSeasonFilter(it) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Sort dropdown
            DropdownField(
                label = "Sort",
                options = sortOptions,
                selected = sort,
                onSelectedChange = { viewModel.setSortOption(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Tags filter chips
            Text("Tags", style = MaterialTheme.typography.titleMedium)

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (allTags.isEmpty()) {
                    Text(
                        text = "No tags yet. Add tags when editing/creating clothing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    allTags.forEach { tag ->
                        FilterChip(
                            selected = selectedTagIds.contains(tag.id),
                            onClick = {
                                val next =
                                    if (selectedTagIds.contains(tag.id)) selectedTagIds - tag.id
                                    else selectedTagIds + tag.id
                                viewModel.setSelectedTagIds(next)
                            },
                            label = { Text(tag.name) }
                        )
                    }
                }
            }

            if (selectedTagIds.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearTagFilter() }) {
                    Text("Clear tags")
                }
            }

            // Clear filters button
            Button(
                onClick = {
                    viewModel.clearSearchAndFilters()
                    viewModel.clearTagFilter()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = hasActiveFilters,
                colors = if (hasActiveFilters) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            ) {
                Text(if (hasActiveFilters) "Clear filters" else "No filters applied")
            }

            Spacer(Modifier.height(4.dp))

            // Results
            if (filtered.isEmpty()) {
                Text("No results.")
            } else {
                // Like your Outfit screen: constrain grid height so parent scroll works
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 700.dp) // adjust if you want more/less
                ) {
                    items(filtered, key = { it.id }) { item ->
                        SearchGridCard(
                            item = item,
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }

            // Extra space so last grid items don't feel cramped
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SearchGridCard(
    item: ClothingEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            if (!item.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(8.dp))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(item.category, style = MaterialTheme.typography.bodySmall)
        }
    }
}