package com.recipia.aos.ui.model.factory

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel

class RecipeAllListViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeAllListViewModel::class.java)) {
            return RecipeAllListViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
