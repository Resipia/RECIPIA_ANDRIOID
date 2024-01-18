package com.recipia.aos.ui.dto.recipecreate


data class RecipeCreateUpdateRequestDto(
    val id: Long?,
    val recipeName: String,   // 레시피명
    val recipeDesc: String,   // 레시피 설명
    val timeTaken: Int?,   // 레시피 따라하는데 필요한 시간
    val ingredient: String,   // 재료
    val hashtag: String,      // 해시태그
    val nutritionalInfo: NutritionalInfoDto,   // 영양소 dto
    val subCategoryDtoList: List<SubCategoryDto>,   // 카테고리
//    val fileList: List<MultipartFile>?, // 이미지 파일들 (null로 초기화)
    val deleteFileOrder: List<Int>? // 삭제할 이미지의 order (null로 초기화)
)
