package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.viewmodel.ClothingViewModel
import at.ustp.dolap.viewmodel.OutfitViewModel
import coil.compose.AsyncImage


@Composable
fun OutfitPickClothesScreen(
    outfitId: Int,
    outfitViewModel: OutfitViewModel,
    clothingViewModel: ClothingViewModel,
    onBack: () -> Unit
) {
    val allClothes by clothingViewModel.clothes.collectAsState()

    val flow = remember(outfitId) { outfitViewModel.getOutfitWithClothes(outfitId) }
    val outfitWithClothes by flow.collectAsState(initial = null)

    var selected by remember { mutableStateOf(setOf<Int>()) }
    var initialized by remember { mutableStateOf(false) }

    // Initialize selection ONCE when outfit data arrives
    LaunchedEffect(outfitWithClothes) {
        val ids = outfitWithClothes?.clothes?.map { it.id }?.toSet()
        if (!initialized && ids != null) {
            selected = ids
            initialized = true
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pick clothes", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Select the clothing items to include in this outfit.")
        Spacer(Modifier.height(12.dp))

        if (allClothes.isEmpty()) {
            Text("No clothing items yet. Add clothes first in the Wardrobe tab.")
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onBack) { Text("Back") }
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allClothes) { item ->
                SelectableClothingRow(
                    clothing = item,
                    checked = selected.contains(item.id),
                    onToggle = {
                        selected = if (selected.contains(item.id)) selected - item.id else selected + item.id
                    }
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(onClick = onBack) { Text("Cancel") }
            Button(
                onClick = {
                    outfitViewModel.setOutfitClothes(outfitId, selected.toList())
                    onBack()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save (${selected.size})")
            }
        }
    }
}


@Composable
private fun SelectableClothingRow(
    clothing: ClothingEntity,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onToggle() },
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

            // Thumbnail
            if (!clothing.imageUri.isNullOrBlank()) {
                AsyncImage(
                    model = clothing.imageUri,
                    contentDescription = "Clothing thumbnail",
                    modifier = Modifier
                        .size(56.dp),
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