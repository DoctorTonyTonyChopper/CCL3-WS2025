package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.OutfitWithClothes
import at.ustp.dolap.viewmodel.OutfitViewModel
import coil.compose.AsyncImage

@Composable
fun OutfitListScreen(
    viewModel: OutfitViewModel,
    onOpenOutfit: (Int) -> Unit,
    onAddOutfit: () -> Unit
) {
    val outfits by viewModel.outfitsWithClothes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = outfits,
                    key = { it.outfit.id }
                ) { outfit ->
                    OutfitCard(
                        outfit = outfit,
                        onClick = { onOpenOutfit(outfit.outfit.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OutfitCard(
    outfit: OutfitWithClothes,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(outfit.outfit.name, style = MaterialTheme.typography.titleLarge)

            val meta = listOfNotNull(
                outfit.outfit.occasion?.takeIf { it.isNotBlank() }?.let { "Occasion: $it" },
                outfit.outfit.season?.takeIf { it.isNotBlank() }?.let { "Season: $it" },
                "Rating: ${outfit.outfit.rating}/5"
            ).joinToString(" • ")

            if (meta.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(meta, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(10.dp))

            val clothes = outfit.clothes

            if (clothes.isEmpty()) {
                Text("No clothes added", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        items = clothes,
                        key = { it.id }
                    ) { clothing ->
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            if (!clothing.imageUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = clothing.imageUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFEFEFEF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("—")
                                }
                            }
                        }
                    }
                }
            }

            outfit.outfit.notes?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}