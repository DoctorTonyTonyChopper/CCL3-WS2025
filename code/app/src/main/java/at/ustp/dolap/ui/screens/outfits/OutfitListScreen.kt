package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.OutfitEntity
import at.ustp.dolap.viewmodel.OutfitViewModel

@Composable
fun OutfitListScreen(
    viewModel: OutfitViewModel,
    onOpenOutfit: (Int) -> Unit,
    onAddOutfit: () -> Unit
) {
    val outfits by viewModel.outfits.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Outfits", style = MaterialTheme.typography.headlineMedium)
            IconButton(onClick = onAddOutfit) {
                Icon(Icons.Filled.Add, contentDescription = "Add outfit")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (outfits.isEmpty()) {
            Text(
                "No outfits yet. Tap + to create one.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(outfits) { outfit ->
                    OutfitCard(outfit = outfit, onClick = { onOpenOutfit(outfit.id) })
                }
            }
        }
    }
}

@Composable
private fun OutfitCard(
    outfit: OutfitEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(outfit.name, style = MaterialTheme.typography.titleLarge)
            val meta = listOfNotNull(
                outfit.occasion?.takeIf { it.isNotBlank() }?.let { "Occasion: $it" },
                outfit.season?.takeIf { it.isNotBlank() }?.let { "Season: $it" },
                "Rating: ${outfit.rating}/5"
            ).joinToString(" • ")

            if (meta.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(meta, style = MaterialTheme.typography.bodyMedium)
            }

            outfit.notes?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}