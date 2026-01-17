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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

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

            // Tags section
            Text("Tags", style = MaterialTheme.typography.titleMedium)

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (allTags.isEmpty()) {
                    AssistChip(
                        onClick = { /* no-op */ },
                        enabled = false,
                        label = { Text("No tags yet") }
                    )
                } else {
                    allTags.forEach { tag ->
                        val selected = selectedTagIds.contains(tag.id)

                        FilterChip(
                            selected = selected,
                            onClick = {
                                val next =
                                    if (selected) selectedTagIds - tag.id
                                    else selectedTagIds + tag.id
                                viewModel.setSelectedTagIds(next)
                            },
                            label = { Text(tag.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                // Playful but consistent: selected uses secondaryContainer
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MaterialTheme.colorScheme.outlineVariant,
                                selectedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                }
            }

            // Clear filters button
            Button(
                onClick = {
                    viewModel.clearSearchAndFilters()
                    viewModel.clearTagFilter()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                enabled = hasActiveFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasActiveFilters)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (hasActiveFilters)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(if (hasActiveFilters) "Clear filters" else "No filters applied")
            }

            Spacer(Modifier.height(4.dp))

            // Results
            if (filtered.isEmpty()) {
                SearchEmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    hasActiveFilters = hasActiveFilters
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 760.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        SearchGridCard(
                            item = item,
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SearchEmptyState(
    modifier: Modifier = Modifier,
    hasActiveFilters: Boolean
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (hasActiveFilters) "No matches found" else "Nothing to show yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Text(
                text = if (hasActiveFilters)
                    "Try removing a filter or selecting fewer tags."
                else
                    "Add clothing items from Home to start searching.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!item.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}