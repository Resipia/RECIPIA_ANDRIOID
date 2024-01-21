package com.recipia.aos.ui.components.recipe.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    navController: NavController
) {
    var menuExpanded by remember { mutableStateOf(false) } // 드롭다운 메뉴 상태

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "레시피 상세보기", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "더보기"
                        )
                    }
                    CustomDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        // 드롭다운 메뉴 아이템들
                        DropdownMenuItem(
                            text = { Text("수정") },
                            onClick = { /* 수정 처리 */ }
                        )
                        DropdownMenuItem(
                            text = { Text("설정") },
                            onClick = { /* 설정 처리 */ }
                        )
                        DropdownMenuItem(
                            text = { Text("피드백 보내기") },
                            onClick = { /* 피드백 처리 */ }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
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
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .height(200.dp) // 높이 설정
                    // .padding(8.dp) // 패딩 제거 또는 조정
                ) { page ->
                    Image(
                        painter = rememberAsyncImagePainter(model = recipeDetail.recipeFileUrlList[page].preUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비 채움
                            .height(200.dp) // 높이 설정
//                            .clip(RoundedCornerShape(10.dp)) // 이미지 모서리 둥글게
//                            .shadow(1.dp, RoundedCornerShape(2.dp)) // 그림자 효과
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color.Gray // 구분선의 색상 설정
                )
//                Divider(Modifier.padding(vertical = 8.dp))

                // 작성자 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 프로필 이미지
                    Image(
                        painter = rememberAsyncImagePainter(model = recipeDetail.recipeFileUrlList ?: "https://example.com/default_profile.jpg"),
                        contentDescription = "작성자 프로필",
                        modifier = Modifier
                            .size(60.dp) // 이미지 크기
                            .clip(CircleShape) // 원형 클리핑
                            .border(0.5.dp, Color.Gray, CircleShape) // 회색 테두리 추가
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 작성자 이름과 카테고리
                    Column {
                        Text(
                            text = recipeDetail.nickname,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        // 카테고리 정보
                        Row(
                            modifier = Modifier
                                .padding(top = 4.dp)
                        ) {
                            recipeDetail.subCategoryDtoList.forEach { subCategory ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(subCategory.subCategoryNm.orEmpty()) }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color.Gray // 구분선의 색상 설정
                )
//                Divider(Modifier.padding(vertical = 8.dp))

                // 제목
                Text(
                    text = "제목: ${recipeDetail.recipeName}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color.Gray // 구분선의 색상 설정
                )

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

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color.Gray // 구분선의 색상 설정
                )
//                Divider(Modifier.padding(vertical = 8.dp))

                // 레시피 설명
                Text(
                    text = recipeDetail.recipeDesc,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color.Gray // 구분선의 색상 설정
                )
//                Divider(Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
