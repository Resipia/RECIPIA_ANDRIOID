package com.recipia.aos.ui.dto.recipecreate

data class NutritionalInfoDto(
    val id: Long?, // pk
    val carbohydrates: Int?,  // 탄수화물 함량
    val protein: Int?, // 단백질 함량
    val fat: Int?, // 지방 함량
    val vitamins: Int?, // 비타민 함량
    val minerals: Int? // 미네랄 함량
)