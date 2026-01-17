package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.data.local.OutfitEntity
import at.ustp.dolap.model.Season
import at.ustp.dolap.ui.components.DropdownField
import at.ustp.dolap.viewmodel.ClothingViewModel
import at.ustp.dolap.viewmodel.OutfitViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitAddEditScreen(
    viewModel: OutfitViewModel,
    clothingViewModel: ClothingViewModel,
    outfitId: Int?,
    onBack: () -> Unit
) {
    val noneLabel = "None"
    val isEditMode = outfitId != null

    var name by remember { mutableStateOf("") }
    var occasion by remember { mutableStateOf("") }
    var season by remember { mutableStateOf(noneLabel) }
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(3) }

    val seasonOptions = remember { listOf(noneLabel) + Season.values().map { it.label } }

    val allClothes by clothingViewModel.clothes.collectAsState()
    var selectedClothes by remember { mutableStateOf(setOf<Int>()) }

    var loadedExisting by remember { mutableStateOf(false) }
    var originalCreatedAt by remember { mutableLongStateOf(0L) }

    var error by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Load outfit + selected clothes if editing
    LaunchedEffect(outfitId) {
        if (outfitId != null) {
            viewModel.getOutfitWithClothes(outfitId).collectLatest { data ->
                val outfit = data?.outfit ?: return@collectLatest
                if (!loadedExisting) {
                    name = outfit.name
                    occasion = outfit.occasion.orEmpty()
                    season = outfit.season ?: noneLabel
                    notes = outfit.notes.orEmpty()
                    rating = outfit.rating
                    originalCreatedAt = outfit.createdAt
                    selectedClothes = data.clothes.map { it.id }.toSet()
                    loadedExisting = true
                }
            }
        } else {
            loadedExisting = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit outfit" else "Create outfit") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) { Text("Cancel") }

                        Button(
                            onClick = {
                                error = null
                                val trimmed = name.trim()
                                if (trimmed.isEmpty()) {
                                    error = "Please enter a name."
                                    return@Button
                                }

                                val seasonToSave = if (season == noneLabel) null else season.trim()

                                val outfit = OutfitEntity(
                                    id = outfitId ?: 0,
                                    name = trimmed,
                                    occasion = occasion.trim().ifBlank { null },
                                    season = seasonToSave,
                                    notes = notes.trim().ifBlank { null },
                                    rating = rating.coerceIn(1, 5),
                                    createdAt = if (!isEditMode) System.currentTimeMillis() else originalCreatedAt
                                )

                                if (!isEditMode) {
                                    viewModel.addOutfitWithClothes(outfit, selectedClothes.toList()) {
                                        onBack()
                                    }
                                } else {
                                    viewModel.updateOutfit(outfit)
                                    viewModel.setOutfitClothes(outfitId!!, selectedClothes.toList())
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                        ) { Text("Save") }
                    }

                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
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

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name*") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = occasion,
                onValueChange = { occasion = it },
                label = { Text("Occasion") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            DropdownField(
                label = "Season (optional)",
                options = seasonOptions,
                selected = season,
                onSelectedChange = { season = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Rating section in a card (feels more structured)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Rating: $rating/5",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Slider(
                        value = rating.toFloat(),
                        onValueChange = { rating = it.toInt().coerceIn(1, 5) },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text("Select clothes", style = MaterialTheme.typography.titleMedium)

            if (allClothes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "No clothing items yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "Add clothes first in your wardrobe, then build outfits.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allClothes, key = { it.id }) { item ->
                            SelectableClothingRowWithThumb(
                                clothing = item,
                                checked = selectedClothes.contains(item.id),
                                onToggle = {
                                    selectedClothes =
                                        if (selectedClothes.contains(item.id)) selectedClothes - item.id
                                        else selectedClothes + item.id
                                }
                            )
                        }
                    }
                }
            }

            // Extra space so last content doesn't hide behind bottomBar
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SelectableClothingRowWithThumb(
    clothing: ClothingEntity,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onToggle() }
            )
            Spacer(Modifier.width(10.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (!clothing.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = clothing.imageUri,
                        contentDescription = "Clothing thumbnail",
                        modifier = Modifier.size(56.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "—",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    clothing.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val meta = listOfNotNull(clothing.category, clothing.color).joinToString(" • ")
                if (meta.isNotBlank()) {
                    Text(
                        meta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}