package com.recipia.aos.ui.model.category

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.recipia.aos.ui.dto.SubCategoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategorySelectionViewModel : ViewModel() {

    // CategorySelectionViewModel 내부
    var selectedCategories = mutableStateOf(setOf<SubCategoryDto>())

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()


    // 카테고리값 세팅
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

    // 처음 들어왔는지 세팅
    fun setInitialized(initialized: Boolean) {
        _isInitialized.value = initialized
    }

    // 카테고리 초기화
    fun clearSelectedCategories() {
        selectedCategories.value = emptySet()
        _isInitialized.value = false
    }

    // 초기 카테고리 초기화 메소드 (레시피 수정에서 사용)
    fun initializeCategories(
        initialCategories: Set<SubCategoryDto>
    ) {
        if (!_isInitialized.value) {
            // 초기화 로직
            selectedCategories.value = initialCategories
            _isInitialized.value = true
        }
    }

    // 카테고리 초기화
    fun clearCategories() {
        selectedCategories.value = emptySet()
    }

    // 카테고리 업데이트
    fun updateCategories(subCategoryDtos: Set<SubCategoryDto>) {
        selectedCategories.value = subCategoryDtos
    }

    // 카테고리 삭제 메소드 추가
    fun removeSelectedCategory(subCategoryDto: SubCategoryDto) {
        val updatedCategories = selectedCategories.value.toMutableSet()
        updatedCategories.remove(subCategoryDto)
        selectedCategories.value = updatedCategories
    }


}