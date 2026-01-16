package at.ustp.dolap.ui.screens.outfits

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.viewmodel.OutfitViewModel
import coil.compose.AsyncImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailScreen(
    viewModel: OutfitViewModel,
    outfitId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onPickClothes: (Int) -> Unit
) {
    val flow = remember(outfitId) { viewModel.getOutfitWithClothes(outfitId) }
    val data by flow.collectAsState(initial = null)

    val wearLogFlow = remember(outfitId) { viewModel.getWearLogForOutfit(outfitId) }
    val wearLog by wearLogFlow.collectAsState(initial = emptyList())

    var confirmDelete by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showAllWear by remember { mutableStateOf(false) }
    var wearExpanded by remember { mutableStateOf(false) }

    val wearDateFormatter = remember { DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault()) }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete outfit?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    data?.outfit?.let { viewModel.deleteOutfit(it) }
                    confirmDelete = false
                    onBack()
                }) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            viewModel.addWearFromDatePickerMillis(outfitId, millis)
                            // Optionally auto-expand so user can delete if mis-tapped
                            wearExpanded = true
                        }
                        showDatePicker = false
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val outfit = data?.outfit
    val clothes = data?.clothes.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Outfit Details", style = MaterialTheme.typography.headlineMedium)

        if (outfit == null) {
            Text("Outfit not found.")
            OutlinedButton(onClick = onBack) { Text("Back") }
            return
        }

        Text(outfit.name, style = MaterialTheme.typography.titleLarge)
        Text("Occasion: ${outfit.occasion?.takeIf { it.isNotBlank() } ?: "—"}")
        Text("Season: ${outfit.season?.takeIf { it.isNotBlank() } ?: "—"}")
        Text("Rating: ${outfit.rating}/5")
        Text("Notes: ${outfit.notes?.takeIf { it.isNotBlank() } ?: "—"}")

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBack) { Text("Back") }
            Button(onClick = { onEdit(outfitId) }) { Text("Edit") }
            Button(
                onClick = { confirmDelete = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Delete")
            }

        }

        Spacer(Modifier.height(6.dp))

        // --- Wear history (Checkpoint 4, compact + expandable) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Wear history", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Worn ${wearLog.size} time${if (wearLog.size == 1) "" else "s"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    TextButton(onClick = { wearExpanded = !wearExpanded }) {
                        Text("Manage entries")
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = if (wearExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = if (wearExpanded) "Collapse" else "Expand"
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = {
                        viewModel.addWearToday(outfitId)
                        wearExpanded = true
                    }) { Text("Worn today") }
                    OutlinedButton(onClick = { showDatePicker = true }) { Text("Pick date") }
                }

                AnimatedVisibility(visible = wearExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                        if (wearLog.isEmpty()) {
                            Text(
                                "No wear entries yet. Use \"Worn today\" or \"Pick date\".",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            val sorted = remember(wearLog) { wearLog.sortedByDescending { it.wornDate } }
                            val shown = if (showAllWear) sorted else sorted.take(5)

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                shown.forEach { entry ->
                                    val dateText = remember(entry.wornDate) {
                                        // wornDate is stored as epochDay
                                        LocalDate.ofEpochDay(entry.wornDate).format(wearDateFormatter)
                                    }

                                    WearLogRow(
                                        label = dateText,
                                        onDelete = { viewModel.deleteWearById(entry.id) }
                                    )
                                }
                            }

                            if (wearLog.size > 5) {
                                TextButton(onClick = { showAllWear = !showAllWear }) {
                                    Text(if (showAllWear) "Show less" else "Show all (${wearLog.size})")
                                }
                            }
                        }

                        // Intentionally do NOT show worn date in UI (but it's saved for Checkpoint 6).
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Clothes in this outfit", style = MaterialTheme.typography.titleMedium)
            Button(onClick = { onPickClothes(outfitId) }) { Text("Add clothes") }
        }

        if (clothes.isEmpty()) {
            Text("No clothes added yet. Tap “Add clothes”.")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(clothes) { item ->
                    OutfitClothingCard(
                        clothing = item,
                        onRemove = { viewModel.removeClothingFromOutfit(outfitId, item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WearLogRow(
    label: String,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        OutlinedButton(onClick = onDelete) { Text("Delete") }
    }
}

@Composable
private fun OutfitClothingCard(
    clothing: ClothingEntity,
    onRemove: () -> Unit
) {
    Card(
        border = BorderStroke(1.dp, Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            if (!clothing.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = clothing.imageUri,
                    contentDescription = "Clothing image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image", fontSize = 12.sp)
                }
            }

            Text(clothing.name, style = MaterialTheme.typography.titleMedium)

            val meta = listOfNotNull(clothing.category, clothing.color).joinToString(" • ")
            if (meta.isNotBlank()) {
                Text(meta, style = MaterialTheme.typography.bodySmall)
            }

            OutlinedButton(
                onClick = onRemove,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Remove")
            }
        }
    }
}