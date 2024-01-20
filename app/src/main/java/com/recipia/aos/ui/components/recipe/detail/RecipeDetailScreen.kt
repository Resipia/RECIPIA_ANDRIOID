package com.recipia.aos.ui.components.recipe.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel

@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel
) {
    // 레시피 상세 정보 로드
    LaunchedEffect(key1 = recipeId) {
        recipeDetailViewModel.loadRecipeDetail(recipeId)
    }

    // LiveData를 Compose에서 관찰하기 위해 observeAsState() 사용
    val recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()
    val isLoading = recipeDetailViewModel.isLoading.observeAsState()


    if (isLoading.value == true) {
        // 로딩 인디케이터 표시
        CircularProgressIndicator()
    } else {
        recipeDetailState.value?.let { recipeDetail ->
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                recipeDetail.recipeFileUrlList.forEach { file ->
                    val imagePainter = rememberAsyncImagePainter(model = file.preUrl)
                    Image(
                        painter = imagePainter,
                        contentDescription = "Recipe Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // 이미지 크기는 적절히 조절
                    )
                }

                Text(text = recipeDetail.recipeName, style = MaterialTheme.typography.bodySmall)
                Text(text = "작성자: ${recipeDetail.nickname}", style = MaterialTheme.typography.bodyMedium)
                Text(text = recipeDetail.recipeDesc, style = MaterialTheme.typography.titleLarge)
                Text(text = "재료: ${recipeDetail.ingredient}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "시간: ${recipeDetail.timeTaken}분", style = MaterialTheme.typography.bodyLarge)
                Text(text = "해시태그: ${recipeDetail.hashtag}", style = MaterialTheme.typography.bodyLarge)

                // 영양 정보
                recipeDetail.nutritionalInfoDto?.let { info ->
                    Text(text = "영양 정보", style = MaterialTheme.typography.titleSmall)
                    Text(text = "탄수화물: ${info.carbohydrates}")
                    Text(text = "단백질: ${info.protein}")
                    Text(text = "지방: ${info.fat}")
                }

                // 서브 카테고리
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    recipeDetail.subCategoryDtoList.take(3).forEach { subCategory ->
                        AssistChip(
                            onClick = {  },
                            label = {
                                subCategory.subCategoryNm?.let {
                                    Text(it, fontSize = 10.sp)
                                }
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

