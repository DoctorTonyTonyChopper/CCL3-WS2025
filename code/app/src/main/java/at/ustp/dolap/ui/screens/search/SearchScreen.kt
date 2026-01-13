package at.ustp.dolap.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import at.ustp.dolap.model.Category
import at.ustp.dolap.model.ClothingColors
import at.ustp.dolap.model.Season
import at.ustp.dolap.model.Size
import at.ustp.dolap.ui.components.DropdownField
import at.ustp.dolap.viewmodel.ClothingViewModel
import at.ustp.dolap.data.local.ClothingEntity
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                sort != "Newest"

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
        ) {
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

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(12.dp))

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

            Spacer(Modifier.height(12.dp))

            // NEW: Sort dropdown
            DropdownField(
                label = "Sort",
                options = sortOptions,
                selected = sort,
                onSelectedChange = { viewModel.setSortOption(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.clearSearchAndFilters() },
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

            Spacer(Modifier.height(16.dp))

            if (filtered.isEmpty()) {
                Text("No results.")
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filtered) { item ->
                        SearchGridCard(
                            item = item,
                            onClick = { onItemClick(item.id) }
                        )
                    }
                }
            }
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
            if (item.imageUri != null) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
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