package at.ustp.dolap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.ustp.dolap.data.repo.InsightsRepository

class InsightsViewModelFactory(
    private val repository: InsightsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsightsViewModel::class.java)) {
            return InsightsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}