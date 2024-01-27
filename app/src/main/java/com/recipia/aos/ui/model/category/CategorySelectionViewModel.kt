package com.recipia.aos.ui.model.category

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
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

    // 초기화 상태 플래그
    var isInitialized = false

    // 업데이트 페이지에서 뒤로가면 초기 상태 초기화하기
    fun changeInitialized() {
        isInitialized = false
    }

    // 재료와 해시태그 초기화 메소드 (레시피 수정에서 사용)
    fun initializeCategories(
        initialCategories: Set<SubCategoryDto>
    ) {
        if (!isInitialized) {
            // 초기화 로직
            selectedCategories.value = initialCategories
            isInitialized = true
        }
    }
}