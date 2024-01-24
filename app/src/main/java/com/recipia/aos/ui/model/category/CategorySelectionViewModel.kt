package com.recipia.aos.ui.model.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.dto.SubCategoryDto

class CategorySelectionViewModel : ViewModel() {

    // CategorySelectionViewModel 내부
    var selectedCategories = mutableStateOf(setOf<SubCategoryDto>())

    fun setSelectedCategories(subCategoryDtos: Set<SubCategoryDto>) {
        selectedCategories.value = subCategoryDtos
    }

    // selectedCategories가 Set<Int> 타입이므로, 이를 List<SubCategoryDto>로 변환하는 로직
    fun createSubCategoryDtoList(
        selectedCategoryDtos: Set<SubCategoryDto>
    ): List<SubCategoryDto> {

        return selectedCategoryDtos.map { dto ->
            SubCategoryDto(id = dto.id, subCategoryNm = "")
        }
    }
}