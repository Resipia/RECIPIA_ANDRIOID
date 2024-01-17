package com.recipia.aos.ui.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.jwt.JwtTokenManager
import com.recipia.aos.ui.model.RecipeAllListViewModel

class RecipeAllListViewModelFactory(
    private val jwtTokenManager: JwtTokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeAllListViewModel::class.java)) {
            return RecipeAllListViewModel(jwtTokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
