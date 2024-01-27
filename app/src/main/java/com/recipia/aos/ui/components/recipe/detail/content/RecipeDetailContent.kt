package com.recipia.aos.ui.components.recipe.detail.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.common.AnimatedPreloader
import com.recipia.aos.ui.model.comment.CommentViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel

/**
 * 레시피 상세보기 콘텐츠 컴포저
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeDetailContent(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
    setShowSheet: (Boolean) -> Unit
) {

    val recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()
    val isLoading = recipeDetailViewModel.isLoading.observeAsState()

    // 레시피 상세 정보 로드
    LaunchedEffect(key1 = recipeId) {
        recipeDetailViewModel.loadRecipeDetail(recipeId)
        commentViewModel.loadInitialComments(recipeId) // 수정된 함수 호출
    }

    // 로딩중이면 인디케이터 표시
    if (isLoading.value == true) {
        AnimatedPreloader(modifier = Modifier.size(100.dp)) // 로딩 바의 크기 조절 가능
    } else {
        recipeDetailState.value?.let { recipeDetail ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                item {
                    // 이미지 슬라이더 구현
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        initialPageOffsetFraction = 0f
                    ) {
                        recipeDetail.recipeFileUrlList.size
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .height(300.dp) // 높이 설정
                    ) { page ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = recipeDetail.recipeFileUrlList[page].preUrl
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .aspectRatio(1.0f / 2.0f) // 이미지의 세로 길이를 가로 길이의 절반으로 설정
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }

                item {
                    // 날짜, 좋아요, 북마크 정보
                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 날짜
                        val dateOnly = recipeDetail.createDate?.substringBefore("T") ?: ""
                        recipeDetail.createDate?.let {
                            Text(
                                text = dateOnly,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        // todo: 좋아요

                        // todo: 북마크
                    }

//                    HorizontalDivider(
//                        modifier = Modifier
//                            .fillMaxWidth() // 전체 너비를 채우도록 설정
//                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
//                        thickness = 0.5.dp, // 구분선의 두께 설정
//                        color = Color(222, 226, 230) // 구분선의 색상 설정
//                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // 작성자 정보
                    Row(
                        modifier = Modifier
                            .clickable {
                                // 여기서 navController를 사용하여 MyPageScreen으로 이동
                                navController.navigate("other-user-page/${recipeDetail.memberId}")
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 프로필 이미지
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = recipeDetail.recipeFileUrlList
                                    ?: "https://example.com/default_profile.jpg"
                            ),
                            contentDescription = "작성자 프로필",
                            modifier = Modifier
                                .size(40.dp) // 이미지 크기
                                .clip(CircleShape) // 원형 클리핑
                                .border(0.5.dp, Color.Gray, CircleShape) // 회색 테두리 추가
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 닉네임
                        Text(
                            text = recipeDetail.nickname,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }

                item {
                    // 레시피명
                    Text(
                        text = recipeDetail.recipeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 카테고리 정보
                    Row(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "카테고리: ",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        recipeDetail.subCategoryDtoList.forEach { subCategory ->
                            Text(
                                subCategory.subCategoryNm.orEmpty(),
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 레시피 내용
                    Text(
                        text = recipeDetail.recipeDesc,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }

                item {
                    // todo: 소요 시간
                    Text(
                        text = "소요 시간: ${recipeDetail.timeTaken}분",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // todo: 재료 목록
                    Text(
                        text = "재료: ${recipeDetail.ingredient}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // todo: 해시태그 정보
                    Text(
                        text = "해시태그: ${recipeDetail.hashtag}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // todo: 영양 정보
                    recipeDetail.nutritionalInfoDto?.let { info ->
                        Text(
                            text = "영양 정보: 탄수화물 ${info.carbohydrates}(g)," +
                                    " 단백질 ${info.protein}(g)," +
                                    " 지방 ${info.fat}(g)," +
                                    " 비타민 ${info.vitamins}(g)," +
                                    " 미네랄 ${info.minerals}(g)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }


                item {

                    Spacer(modifier = Modifier.height(8.dp))

                    // 댓글보기
                    Button(
                        onClick = {
                            navController.navigate("comment/${recipeId}")
                        },
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .height(60.dp) // 버튼의 높이를 더 크게 설정
                            .padding(horizontal = 12.dp, vertical = 8.dp), // 주변 여백 설정
                        shape = RoundedCornerShape(12.dp), // 모서리를 둥글게 설정
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(241,243,245), // 버튼 배경색
                            contentColor = MaterialTheme.colorScheme.onPrimary // 텍스트 및 아이콘 색상
                        )
                    ) {
                        Text(
                            text = "댓글보기",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}