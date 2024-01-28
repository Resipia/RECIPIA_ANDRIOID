package com.recipia.aos.ui.components.recipe.detail

import TokenManager
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

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
                            // 삭제 성공 시 처리, 예를 들어 홈 화면으로 이동
                            Toast.makeText(context, "레시피가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                            navController.popBackStack()
                            navController.navigate("home")
                        },
                        onError = { errorMessage ->
                            // 오류 발생 시 처리
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {}
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