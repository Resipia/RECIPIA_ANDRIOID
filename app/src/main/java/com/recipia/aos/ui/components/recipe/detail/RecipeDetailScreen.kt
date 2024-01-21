package com.recipia.aos.ui.components.recipe.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "레시피 상세보기", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        RecipeDetailContent(
            recipeId = recipeId,
            recipeDetailViewModel = recipeDetailViewModel,
            paddingValues = innerPadding
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeDetailContent(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    paddingValues: PaddingValues
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
                modifier = Modifier
                    .padding(paddingValues) // Apply PaddingValues here
                    .padding(horizontal = 16.dp)
            ) {

                // 이미지 슬라이더 구현
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f
                ) {
                    recipeDetail.recipeFileUrlList.size // Provide pageCount here
                }

                HorizontalPager(
                    state = pagerState, modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) { page ->
                    Image(
                        painter = rememberAsyncImagePainter(model = recipeDetail.recipeFileUrlList[page].preUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp)) // 이미지 모서리 둥글게
                            .shadow(4.dp, RoundedCornerShape(10.dp)) // 그림자 효과
                    )
                }

                Divider(Modifier.padding(vertical = 8.dp))

                // 작성자 정보
                Row {
                    Text(
                        text = "작성자: ${recipeDetail.nickname}",
                        style = MaterialTheme.typography.titleSmall
                    )

                    // 카테고리 정보
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        recipeDetail.subCategoryDtoList.forEach { subCategory ->
                            AssistChip(
                                onClick = { },
                                label = { Text(subCategory.subCategoryNm.orEmpty()) }
                            )
                        }
                    }
                }

                Divider(Modifier.padding(vertical = 8.dp))

                // 제목
                Text(
                    text = "제목: ${recipeDetail.recipeName}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 재료 목록
                Text(
                    text = "재료: ${recipeDetail.ingredient}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 영양 정보
                recipeDetail.nutritionalInfoDto?.let { info ->
                    Text(
                        text = "영양 정보: 탄수화물 ${info.carbohydrates}, 단백질 ${info.protein}, 지방 ${info.fat}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Divider(Modifier.padding(vertical = 8.dp))

                // 레시피 설명
                Text(
                    text = recipeDetail.recipeDesc,
                    style = MaterialTheme.typography.bodyLarge
                )

                Divider(Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
