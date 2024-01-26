package com.recipia.aos.ui.dto

import java.time.LocalDateTime

data class RecipeListResponseDto(
    val id: Long?,
    val recipeName: String,
    val nickname: String,
    val subCategoryList: List<String>,
    val bookmarkId: Long?,
    val createDate: LocalDateTime?, // 레시피 생성시간 (년/월/일)
    val thumbnailFullPath: String?, // 썸네일 이미지 저장경로
    val thumbnailPreUrl: String?    // 썸네일 이미지 pre-signed-url
)
