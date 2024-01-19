package com.recipia.aos.ui.dto

data class RecipeMainListResponseDto(
    val id: Long?,
    val recipeName: String,
    val nickname: String,
    val subCategoryList: List<String>,
    val bookmarkId: Long?
)
