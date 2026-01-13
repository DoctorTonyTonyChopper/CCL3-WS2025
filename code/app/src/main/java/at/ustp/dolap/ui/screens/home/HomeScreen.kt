package at.ustp.dolap.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.ui.theme.LogoTextStyle
import at.ustp.dolap.viewmodel.ClothingViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ClothingViewModel,
    onItemClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    val clothes by viewModel.clothes.collectAsState()

    var itemToDelete by remember { mutableStateOf<ClothingEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dolap",
                        style = LogoTextStyle
                    )
                }
            )
        }
    ) { padding ->
        if (clothes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your wardrobe is empty.",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Add your first clothing item with a photo.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onAddClick) {
                    Text("Add item")
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = padding.calculateTopPadding() + 12.dp,
                    bottom = padding.calculateBottomPadding() + 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(clothes) { item ->
                    ClothingGridCard(
                        item = item,
                        onClick = { onItemClick(item.id) },
                        onDelete = { itemToDelete = item }
                    )
                }
            }
        }
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete item?") },
            text = { Text("Are you sure you want to delete \"${itemToDelete!!.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val deletedItem = itemToDelete!!
                        itemToDelete = null

                        viewModel.deleteItem(deletedItem)

                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Deleted \"${deletedItem.name}\"",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.addItem(deletedItem.copy(id = 0))
                            }
                        }
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun ClothingGridCard(
    item: ClothingEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            if (item.imageUri != null) {
                AsyncImage(
                    model = item.imageUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                Spacer(Modifier.height(8.dp))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            Text(item.name, style = MaterialTheme.typography.titleMedium)
            Text(item.category, style = MaterialTheme.typography.bodySmall)

            Spacer(Modifier.height(6.dp))


            }
        }
    }
