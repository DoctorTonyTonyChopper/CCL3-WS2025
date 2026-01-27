package at.ustp.dolap.ui.screens.outfits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import at.ustp.dolap.data.local.OutfitWithClothes
import at.ustp.dolap.viewmodel.OutfitViewModel
import coil.compose.AsyncImage
import java.util.Locale

val MintPrimary = Color(0xFF2EC4B6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitListScreen(
    viewModel: OutfitViewModel,
    onOpenOutfit: (Int) -> Unit,
    onAddOutfit: () -> Unit
) {
    val outfits by viewModel.outfitsWithClothes.collectAsState()

    var searchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchOpen) {
        if (searchOpen) focusRequester.requestFocus()
    }

    val filteredOutfits = remember(outfits, searchQuery) {
        if (searchQuery.isBlank()) outfits
        else {
            val q = searchQuery.trim().lowercase(Locale.getDefault())
            outfits.filter { it.outfit.name.lowercase(Locale.getDefault()).contains(q) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Outfits") },
                actions = {
                    IconButton(
                        onClick = {
                            if (searchOpen) {
                                keyboardController?.hide()
                                searchQuery = ""
                                searchOpen = false
                            } else {
                                searchOpen = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (searchOpen) Icons.Filled.Close else Icons.Filled.Search,
                            contentDescription = if (searchOpen) "Close search" else "Search"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddOutfit,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add outfit",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        if (outfits.isEmpty()) {
            OutfitEmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                onAddClick = onAddOutfit
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                if (searchOpen) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        placeholder = { Text("Search outfits…") },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                searchOpen = false
                            }
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                } else {
                    Spacer(Modifier.height(12.dp))
                }

                if (filteredOutfits.isEmpty()) {
                    Text(
                        text = "No outfits match \"$searchQuery\".",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(
                            items = filteredOutfits,
                            key = { it.outfit.id }
                        ) { outfit: OutfitWithClothes ->  // <-- Typ annotiert
                            OutfitCard(
                                outfit = outfit,
                                viewModel = viewModel,
                                onClick = { onOpenOutfit(outfit.outfit.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OutfitEmptyState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Checkroom,
                    contentDescription = null
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Start your outfits",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "No outfits yet.",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Add your first outfit.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap + to create one.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun OutfitCard(
    outfit: OutfitWithClothes,
    viewModel: OutfitViewModel,
    onClick: () -> Unit
) {
    // Holen des Worn-Status aus ViewModel als State
    val wornToday by viewModel.isOutfitWornToday(outfit.outfit.id).collectAsState(initial = false)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    outfit.outfit.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                val meta = listOfNotNull(
                    outfit.outfit.occasion?.takeIf { it.isNotBlank() }?.let { "Occasion: $it" },
                    outfit.outfit.season?.takeIf { it.isNotBlank() }?.let { "Season: $it" },
                ).joinToString(" • ")

                if (meta.isNotBlank()) {
                    Text(
                        meta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(10.dp))

                val clothes = outfit.clothes
                if (clothes.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(clothes, key = { it.id }) { clothing ->
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
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
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
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.height(4.dp))
            }

            RatingStars(
                rating = outfit.outfit.rating,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            )


            TextButton(
                onClick = {
                    if (!wornToday) {
                        viewModel.addWearToday(outfit.outfit.id)
                    } else {
                        viewModel.removeWearToday(outfit.outfit.id)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = if (wornToday) Icons.Filled.Check else Icons.Filled.Today,
                    contentDescription = if (wornToday) "Marked as worn" else "Mark as worn today",
                    tint = MintPrimary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (wornToday) "Worn today" else "Wear today",
                    color = MintPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }
}
@Composable
private fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating)
                    Icons.Filled.Star
                else
                    Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = MintPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

