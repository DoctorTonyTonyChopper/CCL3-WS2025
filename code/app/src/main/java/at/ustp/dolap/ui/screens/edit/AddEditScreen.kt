package at.ustp.dolap.ui.screens.edit

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: ClothingViewModel,
    itemId: Int?,          // null = add mode, not null = edit mode
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var imageUriString by remember { mutableStateOf<String?>(null) }
    val isEditMode = itemId != null


// Kamera Launcher (TakePicturePreview)
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val file = File(context.filesDir, "camera_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
            }
            val uri = Uri.fromFile(file)
            imageUriString = uri.toString()
        }
    }

// Galerie Launcher
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
                // manche Provider erlauben persist nicht, ist okay
            }
            imageUriString = uri.toString()
        }
    }

    val existingItem by if (isEditMode) {
        viewModel.getItemById(itemId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf<ClothingEntity?>(null) }
    }

    val noneLabel = "None"

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.TSHIRT.label) }
    var color by remember { mutableStateOf(ClothingColors.first()) }
    var size by remember { mutableStateOf(noneLabel) }
    var season by remember { mutableStateOf(noneLabel) }


    LaunchedEffect(existingItem) {
        if (isEditMode && existingItem != null) {
            name = existingItem!!.name
            category = existingItem!!.category.ifBlank { Category.TSHIRT.label }
            color = existingItem!!.color.ifBlank { ClothingColors.first() }
            size = existingItem!!.size ?: noneLabel
            season = existingItem!!.season ?: noneLabel
            imageUriString = existingItem!!.imageUri
        }
    }

    var selectedTagIds by remember { mutableStateOf<Set<Int>>(emptySet()) }

    val allTags by viewModel.allTags.collectAsState()
    val existingWithTags by if (isEditMode) {
        viewModel.getItemWithTags(itemId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    LaunchedEffect(existingWithTags) {
        if (isEditMode) {
            selectedTagIds = existingWithTags?.tags?.map { it.id }?.toSet() ?: emptySet()
        }
    }

    val scrollState = rememberScrollState()
    val sizeOptions = remember(category) {
        if (category == "Shoes") {

            listOf(noneLabel) + listOf("36","37","38","39","40","41","42","43","44","45")
        } else {

            listOf(noneLabel) + Size.values().map { it.label }
        }
    }

    val seasonOptions = remember { listOf(noneLabel) + Season.values().map { it.label } }

    var error by remember { mutableStateOf<String?>(null) }

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
                            if (name.isBlank()) { error = "Please enter a name."; return@Button }
                            if (category.isBlank()) { error = "Please choose a category."; return@Button }
                            if (color.isBlank()) { error = "Please choose a color."; return@Button }
                            if (imageUriString == null) { error = "Please select a photo."; return@Button }

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
                                viewModel.updateItem(updated, tagIds = selectedTagIds)
                            } else {
                                val newItem = ClothingEntity(
                                    name = name.trim(),
                                    category = category.trim(),
                                    color = color.trim(),
                                    size = sizeToSave,
                                    season = seasonToSave,
                                    imageUri = imageUriString
                                )
                                viewModel.addItem(newItem, tagIds = selectedTagIds)
                            }

                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Save") }

                    if (error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(error!!, color = MaterialTheme.colorScheme.error)
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
                OutlinedButton(onClick = { photoPicker.launch(arrayOf("image/*")) }) { Text("Pick Image") }
                Spacer(Modifier.width(12.dp))

                OutlinedButton(onClick = { cameraLauncher.launch(null) }) {
                    Text("Take Photo")
                }
                Spacer(Modifier.width(12.dp))
                if (imageUriString != null) {
                    TextButton(onClick = { imageUriString = null }) { Text("Remove") }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --------- Form fields ---------
            val maxNameLength = 30
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    val cleaned = newValue.trimStart()
                    if (cleaned.length <= maxNameLength) name = cleaned
                },
                label = { Text("Name*") },
                supportingText = { Text("${name.length}/$maxNameLength") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            DropdownField("Category*", Category.values().map { it.label }, category, { category = it }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            DropdownField("Color*", ClothingColors, color, { color = it }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            DropdownField("Size (optional)", sizeOptions, size, { size = it }, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            DropdownField("Season (optional)", seasonOptions, season, { season = it }, Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))
            Text("Tags", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allTags.forEach { tag ->
                    FilterChip(
                        selected = selectedTagIds.contains(tag.id),
                        onClick = {
                            selectedTagIds =
                                if (selectedTagIds.contains(tag.id)) selectedTagIds - tag.id
                                else selectedTagIds + tag.id
                        },
                        label = { Text(tag.name) }
                    )
                }
            }
            Spacer(Modifier.height(80.dp))
        }
    }
}
