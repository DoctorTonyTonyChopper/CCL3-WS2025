package at.ustp.dolap.ui.screens.edit

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.text.font.FontWeight
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: ClothingViewModel,
    itemId: Int?, // null = add mode, not null = edit mode
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var imageUriString by remember { mutableStateOf<String?>(null) }
    val isEditMode = itemId != null

    // Camera launcher (TakePicturePreview)
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

    // Gallery launcher
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
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
            listOf(noneLabel) + listOf("36", "37", "38", "39", "40", "41", "42", "43", "44", "45")
        } else {
            listOf(noneLabel) + Size.values().map { it.label }
        }
    }
    val seasonOptions = remember { listOf(noneLabel) + Season.values().map { it.label } }

    var error by remember { mutableStateOf<String?>(null) }

    fun validateAndSave() {
        error = null
        if (name.isBlank()) { error = "Please enter a name."; return }
        if (category.isBlank()) { error = "Please choose a category."; return }
        if (color.isBlank()) { error = "Please choose a color."; return }
        if (imageUriString == null) { error = "Please select a photo."; return }

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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit item" else "Add item") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { validateAndSave() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                    ) {
                        Text(
                            "Save",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    if (error != null) {
                        Text(
                            error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
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
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Photo section
            Text("Photo*", style = MaterialTheme.typography.titleMedium)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // "Frame" background so Fit looks intentional
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (imageUriString != null) {
                            AsyncImage(
                                model = imageUriString,
                                contentDescription = "Clothing photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 220.dp, max = 420.dp)
                                    .padding(12.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No image selected.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { photoPicker.launch(arrayOf("image/*")) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Pick")
                        }

                        OutlinedButton(
                            onClick = { cameraLauncher.launch(null) },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Camera")
                        }

                        if (imageUriString != null) {
                            IconButton(onClick = { imageUriString = null }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Remove photo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            DropdownField(
                label = "Category*",
                options = Category.values().map { it.label },
                selected = category,
                onSelectedChange = { category = it },
                modifier = Modifier.fillMaxWidth()
            )

            // Form fields
            val maxNameLength = 30
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    val cleaned = newValue.trimStart()
                    if (cleaned.length <= maxNameLength) name = cleaned
                },
                label = { Text("Name*") },
                supportingText = { Text("${name.length}/$maxNameLength") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            DropdownField(
                label = "Color*",
                options = ClothingColors,
                selected = color,
                onSelectedChange = { color = it },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Size (optional)",
                options = sizeOptions,
                selected = size,
                onSelectedChange = { size = it },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownField(
                label = "Season (optional)",
                options = seasonOptions,
                selected = season,
                onSelectedChange = { season = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            // Tags
            Text("Tags", style = MaterialTheme.typography.titleMedium)

            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (allTags.isEmpty()) {
                    AssistChip(
                        onClick = { /* no-op */ },
                        enabled = false,
                        label = { Text("No tags yet") }
                    )
                } else {
                    allTags.forEach { tag ->
                        val selected = selectedTagIds.contains(tag.id)

                        FilterChip(
                            selected = selected,
                            onClick = {
                                selectedTagIds =
                                    if (selected) selectedTagIds - tag.id
                                    else selectedTagIds + tag.id
                            },
                            label = { Text(tag.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            // extra padding so content isn't covered by bottom save bar
            Spacer(Modifier.height(80.dp))
        }
    }
}