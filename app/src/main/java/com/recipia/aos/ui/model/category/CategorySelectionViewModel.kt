package com.recipia.aos.ui.model.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CategorySelectionViewModel : ViewModel() {
    var selectedCategories = mutableStateOf(setOf<Int>())

    fun setSelectedCategories(categories: Set<Int>) {
        selectedCategories.value = categories
    }
}