package com.recipia.aos.ui.api.dto.recipe.detail

import com.recipia.aos.ui.api.dto.SubCategoryDto
import com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto
import java.time.LocalDateTime

data class RecipeDetailViewResponseDto(
    val id: Long,
    val memberId: Long,
    val recipeName: String,
    val recipeDesc: String,
    val timeTaken: Int,
    val ingredient: String,
    val hashtag: String,
    val nutritionalInfoDto: com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto,
    val subCategoryDtoList: List<com.recipia.aos.ui.api.dto.SubCategoryDto>,
    val nickname: String,
    val recipeFileUrlList: List<com.recipia.aos.ui.api.dto.recipe.detail.RecipeFileResponseDto>,
    val bookmarkId: Long?,
    val recipeLikeId: Long?,
    val createDate:  String?,        // 레시피 생성시간 (년/월/일)
)