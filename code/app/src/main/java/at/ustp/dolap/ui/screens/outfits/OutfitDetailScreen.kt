package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

    var confirmDelete by remember { mutableStateOf(false) }

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

    val outfit = data?.outfit
    val clothes = data?.clothes.orEmpty()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
            OutlinedButton(onClick = { confirmDelete = true }) { Text("Delete") }
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

            // Image
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
