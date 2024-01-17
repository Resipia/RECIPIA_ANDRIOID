package com.recipia.aos.ui.components.recipe.create

import NutritionalInfoForm
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.recipia.aos.ui.dto.recipecreate.NutritionalInfoDto
import com.recipia.aos.ui.dto.recipecreate.RecipeCreateUpdateRequestDto
import com.recipia.aos.ui.theme.RecipiaaosTheme

@Composable
fun RecipeForm(
    recipeDto: RecipeCreateUpdateRequestDto,
    onRecipeInfoChanged: (RecipeCreateUpdateRequestDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            // 레시피 이름 입력 필드
            OutlinedTextField(
                value = recipeDto.recipeName,
                onValueChange = {
                    onRecipeInfoChanged(recipeDto.copy(recipeName = it))
                },
                label = { Text("레시피 이름") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // 레시피 설명 입력 필드
            OutlinedTextField(
                value = recipeDto.recipeDesc,
                onValueChange = {
                    onRecipeInfoChanged(recipeDto.copy(recipeDesc = it))
                },
                label = { Text("레시피 설명") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // 시간 입력 필드
            OutlinedTextField(
                value = recipeDto.timeTaken?.toString() ?: "",
                onValueChange = {
                    onRecipeInfoChanged(recipeDto.copy(timeTaken = it.toIntOrNull()))
                },
                label = { Text("레시피 소요 시간 (분)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // 재료 입력 필드
            OutlinedTextField(
                value = recipeDto.ingredient,
                onValueChange = {
                    onRecipeInfoChanged(recipeDto.copy(ingredient = it))
                },
                label = { Text("재료") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // 해시태그 입력 필드
            OutlinedTextField(
                value = recipeDto.hashtag,
                onValueChange = {
                    onRecipeInfoChanged(recipeDto.copy(hashtag = it))
                },
                label = { Text("해시태그") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // 영양소 입력 폼
            NutritionalInfoForm(
                nutritionalInfoDto = recipeDto.nutritionalInfo,
                onRecipeInfoChanged = { updatedNutritionalInfo ->
                    val updatedRecipeDto = updatedNutritionalInfo?.let { recipeDto.copy(nutritionalInfo = it) }
                    if (updatedRecipeDto != null) {
                        onRecipeInfoChanged(updatedRecipeDto)
                    }
                }
            )
        }

        // 카테고리 선택 폼
        // SubCategory 선택 폼
        // 이미지 업로드 폼 등 추가 가능
    }
}


@Preview
@Composable
fun RecipeFormPreview() {
    val initialRecipeDto = RecipeCreateUpdateRequestDto(
        id = null,
        recipeName = "",
        recipeDesc = "",
        timeTaken = null,
        ingredient = "",
        hashtag = "",
        nutritionalInfo = NutritionalInfoDto(1,10,10,10,10,10),
        deleteFileOrder = null,
        subCategoryDtoList = emptyList()
    )

    RecipiaaosTheme {
        RecipeForm(
            recipeDto = initialRecipeDto,
            onRecipeInfoChanged = {}
        )
    }
}
