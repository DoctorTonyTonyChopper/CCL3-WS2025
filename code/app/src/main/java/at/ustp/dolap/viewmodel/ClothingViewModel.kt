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

class ClothingViewModel(
    private val repository: ClothingRepository
) : ViewModel() {

    val clothes: StateFlow<List<ClothingEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getItemById(id: Int) = repository.getById(id)

    fun addItem(item: ClothingEntity) {
        viewModelScope.launch { repository.add(item) }
    }

    fun updateItem(item: ClothingEntity) {
        viewModelScope.launch { repository.update(item) }
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
            clothes,
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

            // Apply sorting
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