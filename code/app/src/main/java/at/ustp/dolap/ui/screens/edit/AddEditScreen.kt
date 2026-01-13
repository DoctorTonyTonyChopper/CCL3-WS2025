package at.ustp.dolap.ui.screens.edit

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.model.Category
import at.ustp.dolap.model.ClothingColors
import at.ustp.dolap.model.Season
import at.ustp.dolap.model.Size
import at.ustp.dolap.ui.components.DropdownField
import at.ustp.dolap.viewmodel.ClothingViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: ClothingViewModel,
    itemId: Int?,          // null = add mode, not null = edit mode
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isEditMode = itemId != null

    val existingItem by if (isEditMode) {
        viewModel.getItemById(itemId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf<ClothingEntity?>(null) }
    }

    // UI label for "no value"
    val noneLabel = "None"

    // Defaults
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.TSHIRT.label) }
    var color by remember { mutableStateOf(ClothingColors.first()) }

    // Default is None so it can be saved as null
    var size by remember { mutableStateOf(noneLabel) }
    var season by remember { mutableStateOf(noneLabel) }

    var imageUriString by remember { mutableStateOf<String?>(null) }

    // Load existing item into fields when editing
    LaunchedEffect(existingItem) {
        if (isEditMode && existingItem != null) {
            name = existingItem!!.name
            category = existingItem!!.category.ifBlank { Category.TSHIRT.label }
            color = existingItem!!.color.ifBlank { ClothingColors.first() }

            // Convert null -> "None" for UI
            size = existingItem!!.size ?: noneLabel
            season = existingItem!!.season ?: noneLabel

            imageUriString = existingItem!!.imageUri
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some providers don't allow persist permission, which is okay
            }
            imageUriString = uri.toString()
        }
    }

    var error by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    // Dropdown options with "None"
    val sizeOptions = remember { listOf(noneLabel) + Size.values().map { it.label } }
    val seasonOptions = remember { listOf(noneLabel) + Season.values().map { it.label } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Item" else "Add Item") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 2.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            error = null

                            if (name.isBlank()) {
                                error = "Please enter a name."
                                return@Button
                            }
                            if (category.isBlank()) {
                                error = "Please choose a category."
                                return@Button
                            }
                            if (color.isBlank()) {
                                error = "Please choose a color."
                                return@Button
                            }

                            // Photo required
                            if (imageUriString == null) {
                                error = "Please select a photo."
                                return@Button
                            }

                            // Convert UI "None" -> null for DB
                            val sizeToSave = if (size == noneLabel) null else size.trim()
                            val seasonToSave = if (season == noneLabel) null else season.trim()

                            if (isEditMode && existingItem != null) {
                                val updated = existingItem!!.copy(
                                    name = name.trim(),
                                    category = category.trim(),
                                    color = color.trim(),
                                    size = sizeToSave,
                                    season = seasonToSave,
                                    imageUri = imageUriString
                                )
                                viewModel.updateItem(updated)
                            } else {
                                val newItem = ClothingEntity(
                                    name = name.trim(),
                                    category = category.trim(),
                                    color = color.trim(),
                                    size = sizeToSave,
                                    season = seasonToSave,
                                    imageUri = imageUriString

                                )
                                viewModel.addItem(newItem)
                            }

                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save")
                    }

                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Photo Section
            Text("Photo*", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (imageUriString != null) {
                AsyncImage(
                    model = imageUriString,
                    contentDescription = "Clothing photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            } else {
                Text("No image selected.")
            }

            Spacer(Modifier.height(8.dp))


            Row {
                OutlinedButton(
                    onClick = { photoPicker.launch(arrayOf("image/*")) }
                ) {
                    Text("Pick Image")

                }

                Spacer(Modifier.width(12.dp))

                if (imageUriString != null) {
                    TextButton(
                        onClick = { imageUriString = null }
                    ) {
                        Text("Remove")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Form Section
            val maxNameLength = 30

            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    val cleaned = newValue.trimStart()
                    if (cleaned.length <= maxNameLength) {
                        name = cleaned
                    }
                },
                label = { Text("Name*") },
                supportingText = {
                    Text("${name.length}/$maxNameLength")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Category*",
                options = Category.values().map { it.label },
                selected = category,
                onSelectedChange = { category = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Color*",
                options = ClothingColors,
                selected = color,
                onSelectedChange = { color = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Size (optional)",
                options = sizeOptions,
                selected = size,
                onSelectedChange = { size = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            DropdownField(
                label = "Season (optional)",
                options = seasonOptions,
                selected = season,
                onSelectedChange = { season = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(80.dp))
        }
    }
}