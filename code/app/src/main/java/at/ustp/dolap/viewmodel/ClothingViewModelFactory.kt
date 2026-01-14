package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.ustp.dolap.data.repo.ClothingRepository
import at.ustp.dolap.data.repo.TagRepository

class ClothingViewModelFactory(
    private val repository: ClothingRepository,
    private val tagRepository: TagRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClothingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClothingViewModel(repository, tagRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}