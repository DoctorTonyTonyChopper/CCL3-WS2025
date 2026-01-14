package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.ustp.dolap.data.local.OutfitEntity
import at.ustp.dolap.data.local.OutfitWearEntity
import at.ustp.dolap.data.repo.OutfitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class OutfitViewModel(
    private val repository: OutfitRepository
) : ViewModel() {

    val outfits: StateFlow<List<OutfitEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outfitsWithClothes =
        repository.getAllOutfitsWithClothes()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

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

    fun getOutfitWithClothes(id: Int) = repository.getOutfitWithClothes(id)

    fun removeClothingFromOutfit(outfitId: Int, clothingId: Int) {
        viewModelScope.launch { repository.removeClothing(outfitId, clothingId) }
    }

    fun setOutfitClothes(outfitId: Int, clothingIds: List<Int>) {
        viewModelScope.launch { repository.setClothes(outfitId, clothingIds) }
    }

    fun addOutfitWithClothes(outfit: OutfitEntity, clothingIds: List<Int>, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.addWithClothes(outfit, clothingIds)
            onDone?.invoke()
        }
    }

    // Wear log-

    fun getWearLogForOutfit(outfitId: Int) = repository.getWearLogForOutfit(outfitId)

    fun getWearCountForOutfit(outfitId: Int) = repository.getWearCountForOutfit(outfitId)

    fun addWearToday(outfitId: Int) {
        val epochDay = LocalDate.now().toEpochDay()
        viewModelScope.launch { repository.addWear(outfitId, epochDay) }
    }

    fun addWearFromDatePickerMillis(outfitId: Int, selectedDateMillis: Long) {
        val localDate = Instant.ofEpochMilli(selectedDateMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        viewModelScope.launch { repository.addWear(outfitId, localDate.toEpochDay()) }
    }

    fun deleteWearById(id: Int) {
        viewModelScope.launch { repository.deleteWearById(id) }
    }

    fun deleteWear(entry: OutfitWearEntity) {
        viewModelScope.launch { repository.deleteWear(entry) }
    }
}