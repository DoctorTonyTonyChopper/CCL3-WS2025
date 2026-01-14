package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
                title = { Text(if (isEditMode) "Edit Outfit" else "Create Outfit") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.weight(1f)
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
                            modifier = Modifier.weight(1f)
                        ) { Text("Save") }
                    }

                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name*") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = occasion,
                onValueChange = { occasion = it },
                label = { Text("Occasion") },
                modifier = Modifier.fillMaxWidth()
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

            Text("Rating: $rating/5", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt().coerceIn(1, 5) },
                valueRange = 1f..5f,
                steps = 3
            )

            Spacer(Modifier.height(6.dp))
            Text("Select clothes", style = MaterialTheme.typography.titleMedium)

            if (allClothes.isEmpty()) {
                Text("No clothing items yet. Add clothes first in Wardrobe.")
            } else {
                // Wichtig: begrenzen, damit nicht ALLES unendlich hoch wird.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp),
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
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = { onToggle() })
            Spacer(Modifier.width(10.dp))

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
                    Text("—")
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(clothing.name, style = MaterialTheme.typography.titleMedium)
                val meta = listOfNotNull(clothing.category, clothing.color).joinToString(" • ")
                if (meta.isNotBlank()) Text(meta, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}