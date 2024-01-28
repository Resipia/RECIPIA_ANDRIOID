package com.recipia.aos.ui.components.recipe.detail

import TokenManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import kotlinx.coroutines.launch

/**
 * 레시피 상세보기 화면 컴포저
 */
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    tokenManager: TokenManager
) {
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

    // dialog(알림창) 호출
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("레시피 삭제") },
            text = { Text("정말 작성한 레시피를 삭제하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    recipeDetailViewModel.deleteRecipe(
                        recipeId = recipeId,
                        onSuccess = {
                            // 성공 시 스낵바 알림
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "레시피가 삭제되었습니다.",
                                    duration = SnackbarDuration.Short
                                )
                            }

                            // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                            navController.popBackStack()
                            navController.navigate("home")
                        },
                        onError = { errorMessage ->
                            // 오류 발생 시 스낵바 알림
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "레시피 삭제에 실패하였습니다.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                    showDialog = false
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {},
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp), // 아이콘 주변의 여백을 설정하고 박스의 너비를 최대로 설정
            ) {

                // 최상단에 경계선 추가
                Divider(
                    color = Color(222, 226, 230),
                    thickness = 1.dp,
                    modifier = Modifier.align(Alignment.TopStart) // Divider를 Box의 최상단에 정렬
                )

                IconButton(
                    onClick = {
                        navController.navigate("comment/$recipeId")
                    },
                    modifier = Modifier.align(Alignment.BottomEnd) // 아이콘을 Box의 오른쪽 하단에 정렬
                ) {
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription = "댓글 보기",
                        modifier = Modifier.size(28.dp)
                            .padding()

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