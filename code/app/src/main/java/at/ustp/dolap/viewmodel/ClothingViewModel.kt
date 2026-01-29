package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.ustp.dolap.data.local.ClothingEntity
import at.ustp.dolap.data.repo.ClothingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flatMapLatest
import at.ustp.dolap.data.repo.TagRepository
import at.ustp.dolap.model.PredefinedTags

class ClothingViewModel(

    private val repository: ClothingRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    // Exposes clothes + tags as reactive StateFlows for the UI, plus all filtering/sorting state.
    // Filtering happens in-memory (search/category/color/size/season/sort), tag filtering is done via DB query.
    val clothes: StateFlow<List<ClothingEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<at.ustp.dolap.data.local.TagEntity>> =
        tagRepository.getAllTags()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getItemWithTags(id: Int) = tagRepository.getClothingWithTags(id)


    fun ensurePredefinedTags() {
        viewModelScope.launch {
            PredefinedTags.ALL.forEach { tagName ->
                tagRepository.ensureTagId(tagName)
            }
        }
    }


    private val _selectedTagIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedTagIds = _selectedTagIds.asStateFlow()

    fun setSelectedTagIds(value: Set<Int>) { _selectedTagIds.value = value }
    fun clearTagFilter() { _selectedTagIds.value = emptySet() }

    private val clothesBase =
        selectedTagIds.flatMapLatest { ids ->
            if (ids.isEmpty()) repository.getAll()
            else repository.getByAllTags(ids.toList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getItemById(id: Int) = repository.getById(id)

    // CRUD + tag linking (write clothing first, then update its tag junction rows).
    fun addItem(item: ClothingEntity, tagIds: Set<Int> = emptySet(), onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            val newId = repository.add(item)
            tagRepository.setTagsForClothing(newId, tagIds)
            onDone?.invoke()
        }
    }

    fun updateItem(item: ClothingEntity, tagIds: Set<Int> = emptySet(), onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.update(item)
            tagRepository.setTagsForClothing(item.id, tagIds)
            onDone?.invoke()
        }
    }

    fun deleteItem(item: ClothingEntity) {
        viewModelScope.launch { repository.delete(item) }
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _categoryFilter = MutableStateFlow("All")
    val categoryFilter = _categoryFilter.asStateFlow()

    private val _colorFilter = MutableStateFlow("All")
    val colorFilter = _colorFilter.asStateFlow()

    private val _sizeFilter = MutableStateFlow("All")
    val sizeFilter = _sizeFilter.asStateFlow()

    private val _seasonFilter = MutableStateFlow("All")
    val seasonFilter = _seasonFilter.asStateFlow()

    // NEW: sort option
    private val _sortOption = MutableStateFlow("Newest")
    val sortOption = _sortOption.asStateFlow()

    fun setSearchQuery(value: String) { _searchQuery.value = value }
    fun setCategoryFilter(value: String) { _categoryFilter.value = value }
    fun setColorFilter(value: String) { _colorFilter.value = value }
    fun setSizeFilter(value: String) { _sizeFilter.value = value }
    fun setSeasonFilter(value: String) { _seasonFilter.value = value }
    fun setSortOption(value: String) { _sortOption.value = value }

    fun clearSearchAndFilters() {
        _searchQuery.value = ""
        _categoryFilter.value = "All"
        _colorFilter.value = "All"
        _sizeFilter.value = "All"
        _seasonFilter.value = "All"
        _sortOption.value = "Newest"
    }

    val filteredClothes: StateFlow<List<ClothingEntity>> =
        combine(
            clothesBase,
            searchQuery,
            categoryFilter,
            colorFilter,
            sizeFilter,
            seasonFilter,
            sortOption
        ) { values: Array<Any?> ->

            val list = values[0] as List<ClothingEntity>
            val query = values[1] as String
            val category = values[2] as String
            val color = values[3] as String
            val size = values[4] as String
            val season = values[5] as String
            val sort = values[6] as String

            val q = query.trim().lowercase()

            val filtered = list.filter { item ->
                val matchesQuery = q.isEmpty() || item.name.lowercase().contains(q)

                val matchesCategory = (category == "All") || item.category == category
                val matchesColor = (color == "All") || item.color == color

                val matchesSize =
                    (size == "All") ||
                            (size == "No Size" && item.size.isNullOrBlank()) ||
                            ((item.size ?: "") == size)

                val matchesSeason =
                    (season == "All") ||
                            (season == "No Season" && item.season.isNullOrBlank()) ||
                            ((item.season ?: "") == season)

                matchesQuery && matchesCategory && matchesColor && matchesSize && matchesSeason
            }

            // Apply sorting (default = newest by id).
            when (sort) {
                "Name A-Z" -> filtered.sortedBy { it.name.lowercase() }
                "Name Z-A" -> filtered.sortedByDescending { it.name.lowercase() }
                else -> filtered.sortedByDescending { it.id } // Newest
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}