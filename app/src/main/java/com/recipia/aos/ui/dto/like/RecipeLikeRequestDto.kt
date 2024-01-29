package com.recipia.aos.ui.dto.like

/**
 * 좋아요 요청 dto
 */
data class RecipeLikeRequestDto(
    val recipeLikeId: Long?,
    val recipeId: Long,
    val memberId: Long
)