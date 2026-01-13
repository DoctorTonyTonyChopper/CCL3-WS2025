package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.ustp.dolap.viewmodel.OutfitViewModel

@Composable
fun OutfitDetailScreen(
    viewModel: OutfitViewModel,
    outfitId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val outfitFlow = remember(outfitId) { viewModel.getOutfitById(outfitId) }
    val outfit by outfitFlow.collectAsState(initial = null)

    var confirmDelete by remember { mutableStateOf(false) }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete outfit?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                Button(onClick = {
                    outfit?.let { viewModel.deleteOutfit(it) }
                    confirmDelete = false
                    onBack()
                }) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Outfit Details", style = MaterialTheme.typography.headlineMedium)

        if (outfit == null) {
            Text("Outfit not found.", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBack) { Text("Back") }
            return
        }

        Text(outfit!!.name, style = MaterialTheme.typography.titleLarge)

        val occasion = outfit!!.occasion?.takeIf { it.isNotBlank() } ?: "—"
        val season = outfit!!.season?.takeIf { it.isNotBlank() } ?: "—"
        val notes = outfit!!.notes?.takeIf { it.isNotBlank() } ?: "—"

        Text("Occasion: $occasion")
        Text("Season: $season")
        Text("Rating: ${outfit!!.rating}/5")
        Text("Notes: $notes")

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onBack) { Text("Back") }
            Button(onClick = { onEdit(outfitId) }) { Text("Edit") }
            OutlinedButton(onClick = { confirmDelete = true }) { Text("Delete") }
        }
    }
}