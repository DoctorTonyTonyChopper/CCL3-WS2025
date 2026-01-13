package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.ustp.dolap.data.repo.OutfitRepository

class OutfitViewModelFactory(
    private val repository: OutfitRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutfitViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OutfitViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}