package com.recipia.aos.ui.dto.recipecreate

data class NutritionalInfoDto(
    val carbohydrates: Int? = 0,  // 탄수화물 함량
    val protein: Int? = 0, // 단백질 함량
    val fat: Int? = 0, // 지방 함량
    val vitamins: Int? = 0, // 비타민 함량
    val minerals: Int? = 0 // 미네랄 함량
)