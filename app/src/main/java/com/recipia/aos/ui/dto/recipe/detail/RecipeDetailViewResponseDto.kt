package com.recipia.aos.ui.dto.recipe.detail

import com.recipia.aos.ui.dto.SubCategoryDto
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto

data class RecipeDetailViewResponseDto(
    val id: Long,
    val recipeName: String,
    val recipeDesc: String,
    val timeTaken: Int,
    val ingredient: String,
    val hashtag: String,
    val nutritionalInfoDto: NutritionalInfoDto,
    val subCategoryDtoList: List<SubCategoryDto>,
    val nickname: String,
    val recipeFileUrlList: List<RecipeFileResponseDto>,
    val bookmarkId: Long?,
    val recipeLikeId: Long?
)