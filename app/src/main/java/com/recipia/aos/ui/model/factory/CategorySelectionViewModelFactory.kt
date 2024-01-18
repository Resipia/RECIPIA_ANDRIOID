package com.recipia.aos.ui.model.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.model.category.CategorySelectionViewModel

class CategorySelectionViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategorySelectionViewModel::class.java)) {
            return CategorySelectionViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}