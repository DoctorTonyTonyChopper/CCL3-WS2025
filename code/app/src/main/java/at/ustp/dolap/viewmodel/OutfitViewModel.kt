package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.ustp.dolap.data.local.OutfitEntity
import at.ustp.dolap.data.repo.OutfitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OutfitViewModel(
    private val repository: OutfitRepository
) : ViewModel() {

    val outfits: StateFlow<List<OutfitEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getOutfitById(id: Int) = repository.getById(id)

    fun addOutfit(outfit: OutfitEntity) {
        viewModelScope.launch { repository.add(outfit) }
    }

    fun updateOutfit(outfit: OutfitEntity) {
        viewModelScope.launch { repository.update(outfit) }
    }

    fun deleteOutfit(outfit: OutfitEntity) {
        viewModelScope.launch { repository.delete(outfit) }
    }
}