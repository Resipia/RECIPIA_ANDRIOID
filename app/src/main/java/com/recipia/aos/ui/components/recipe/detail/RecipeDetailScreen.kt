package com.recipia.aos.ui.components.recipe.detail

import TokenManager
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.components.recipe.detail.comment.BottomSheet
import com.recipia.aos.ui.components.recipe.detail.content.RecipeDetailContent
import com.recipia.aos.ui.model.comment.CommentViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import kotlinx.coroutines.launch

/**
 * 레시피 상세보기 화면 컴포저
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    tokenManager: TokenManager
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val currentUserMemberId = tokenManager.loadMemberId() // 현재 사용자의 memberId 불러오기
    val recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    val setShowSheet = { value: Boolean -> showSheet = value }

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
        topBar = {
            TopAppBar(
                title = { Text(text = "", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = {
                        // 댓글 목록 초기화
                        commentViewModel.clearComments()
                        // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                        navController.popBackStack()
                        navController.navigate("home")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
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
                        // 레시피 작성자가 현재 로그인한 사용자와 같은 경우에만 수정 및 삭제 옵션을 보여줌
                        if (recipeDetailState.value?.memberId == currentUserMemberId) {
                            // 레시피 수정하기
                            DropdownMenuItem(
                                text = { Text("레시피 수정") },
                                onClick = {
                                    navController.navigate("update-recipe")
                                }
                            )
                            // 레시피 삭제 버튼
                            DropdownMenuItem(
                                text = { Text("레시피 삭제") },
                                onClick = {
                                    showDialog = true
                                }
                            )
                        }
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
            commentViewModel = commentViewModel,
            navController = navController,
            paddingValues = innerPadding
        )
    }
}