package com.recipia.aos.ui.components.recipe.detail

import TokenManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.recipe.detail.comment.BottomSheet
import com.recipia.aos.ui.components.recipe.detail.content.RecipeDetailContent
import com.recipia.aos.ui.model.comment.CommentViewModel
import com.recipia.aos.ui.model.recipe.like.LikeViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import kotlinx.coroutines.launch

/**
 * 레시피 상세보기 화면 컴포저
 */
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    likeViewModel: LikeViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    tokenManager: TokenManager
) {
    var recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()
    val currentUserMemberId = tokenManager.loadMemberId() // 현재 사용자의 memberId 불러오기
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

    // BottomSheet(댓글창) 호출
    if (showSheet) {
        BottomSheet(
            onDismiss = {  // 여기서 상태를 변경하는 람다 함수를 전달함
                coroutineScope.launch {
                    showSheet = false
                }
            },
            commentViewModel = commentViewModel,
            recipeId = recipeId,
            tokenManager = tokenManager
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {},
        bottomBar = {
            // 하단 bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp), // 아이콘 주변의 여백을 설정하고 박스의 너비를 최대로 설정
            ) {

                // 최상단에 경계선 추가
                Divider(
                    color = Color(222, 226, 230),
                    thickness = 1.dp,
                    modifier = Modifier.align(Alignment.TopStart) // Divider를 Box의 최상단에 정렬
                )

                Row(
                    modifier = Modifier.align(Alignment.BottomEnd) // Row를 Box의 오른쪽 하단에 정렬
                ) {
                    // 좋아요 아이콘
                    val isLiked =
                        recipeDetailState.value?.recipeLikeId != null && recipeDetailState.value?.recipeLikeId != 0L

                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "좋아요",
                        modifier = Modifier
                            .size(38.dp)
                            .padding(top=12.dp)
                            .clickable {
                                // 현재 좋아요 상태에 따라 좋아요 추가 또는 삭제 처리
                                val recipeLikeId =
                                    if (isLiked) recipeDetailState.value?.recipeLikeId else null
                                // 좋아요 아이콘 클릭 이벤트 내부
                                likeViewModel.toggleRecipeLike(
                                    recipeLikeId,
                                    recipeId,
                                    currentUserMemberId,
                                    onSuccess = { updatedLikeId ->
                                        // 성공 시 (레시피 상세보기 모델의) 좋아요 상태 업데이트
                                        recipeDetailViewModel.updateRecipeLikeId(updatedLikeId)
                                    },
                                    onError = { error ->
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("좋아요 실패")
                                        }
                                        // 에러 처리, 예를 들어 토스트 메시지 표시
                                    })
                            },
                        tint = if (isLiked) Color.Red else Color.Gray
                    )

                    // 댓글 보기 아이콘
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription = "댓글 보기",
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                navController.navigate("comment/$recipeId")
                            }
                            .padding(start = 4.dp, top=13.dp, bottom = 6.dp) // 좋아요 아이콘과 댓글 아이콘 사이의 간격 조정
                    )
                }
            }
        }
    ) { innerPadding ->
        RecipeDetailContent(
            recipeId = recipeId,
            recipeDetailViewModel = recipeDetailViewModel,
            commentViewModel = commentViewModel,
            navController = navController,
            paddingValues = innerPadding,
            tokenManager = tokenManager
        )
    }
}