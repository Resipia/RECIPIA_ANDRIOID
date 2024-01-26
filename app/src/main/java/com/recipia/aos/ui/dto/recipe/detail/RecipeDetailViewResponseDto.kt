package com.recipia.aos.ui.dto.recipe.detail

import com.recipia.aos.ui.dto.SubCategoryDto
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import java.time.LocalDateTime

data class RecipeDetailViewResponseDto(
    val id: Long,
    val memberId: Long,
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
    val recipeLikeId: Long?,
    val createDate:  String?,        // 레시피 생성시간 (년/월/일)
)