package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.OutfitEntity
import at.ustp.dolap.viewmodel.OutfitViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OutfitAddEditScreen(
    viewModel: OutfitViewModel,
    outfitId: Int?,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var occasion by remember { mutableStateOf("") }
    var season by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(3) }

    // If editing, load existing
    LaunchedEffect(outfitId) {
        if (outfitId != null) {
            viewModel.getOutfitById(outfitId).collectLatest { outfit ->
                if (outfit != null) {
                    name = outfit.name
                    occasion = outfit.occasion.orEmpty()
                    season = outfit.season.orEmpty()
                    notes = outfit.notes.orEmpty()
                    rating = outfit.rating
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (outfitId == null) "Create Outfit" else "Edit Outfit",
            style = MaterialTheme.typography.headlineMedium
        )

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

        OutlinedTextField(
            value = season,
            onValueChange = { season = it },
            label = { Text("Season") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Text("Rating: $rating/5", style = MaterialTheme.typography.titleMedium)
        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toInt().coerceIn(1, 5) },
            valueRange = 1f..5f,
            steps = 3
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBack) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    val trimmed = name.trim()
                    if (trimmed.isNotEmpty()) {
                        val outfit = OutfitEntity(
                            id = outfitId ?: 0,
                            name = trimmed,
                            occasion = occasion.trim().ifBlank { null },
                            season = season.trim().ifBlank { null },
                            notes = notes.trim().ifBlank { null },
                            rating = rating.coerceIn(1, 5),
                            createdAt = System.currentTimeMillis()
                        )

                        if (outfitId == null) viewModel.addOutfit(outfit)
                        else viewModel.updateOutfit(outfit)

                        onBack()
                    }
                }
            ) {
                Text("Save")
            }
        }
    }
}