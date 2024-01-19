package com.recipia.aos.ui.model.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.dto.SubCategoryDto

class CategorySelectionViewModel : ViewModel() {
    var selectedCategories = mutableStateOf(setOf<Int>())

    fun setSelectedCategories(categories: Set<Int>) {
        selectedCategories.value = categories
    }

    // selectedCategories가 Set<Int> 타입이므로, 이를 List<SubCategoryDto>로 변환하는 로직
    fun createSubCategoryDtoList(
        selectedCategoryIds: Set<Int>
    ): List<SubCategoryDto> {

        return selectedCategoryIds.map { categoryId ->
            SubCategoryDto(id = categoryId.toLong(), subCategoryNm = "")
        }
    }
}