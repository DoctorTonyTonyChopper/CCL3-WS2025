package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.ustp.dolap.data.repo.ClothingRepository

class ClothingViewModelFactory(
    private val repository: ClothingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClothingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClothingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}