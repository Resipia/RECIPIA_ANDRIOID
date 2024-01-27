package com.recipia.aos.ui.components.recipe.detail.comment

import TokenManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.comment.CommentViewModel
import kotlinx.coroutines.launch

/**
 * 댓글 화면을 보여주는 컴포저
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CommentPageScreen(
    commentViewModel: CommentViewModel,
    navController: NavController,
    recipeId: Long,
    tokenManager: TokenManager
) {

    val coroutineScope = rememberCoroutineScope()
    var commentText by remember { mutableStateOf("") }
    // 키보드 컨트롤러 (터치시 키보드 닫히게 하기)
    val keyboardController = LocalSoftwareKeyboardController.current
    val errorMessage by commentViewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val editingComment by commentViewModel.editingComment.collectAsState()

    // 에러 메시지가 있을 경우 토스트 메시지로 표시
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            commentViewModel.clearErrorMessage() // 메시지 표시 후 초기화
        }
    }

    // 수정하려는 댓글을 OutlinedTextField에 로드
    LaunchedEffect(editingComment) {
        editingComment?.let { (_, commentValue) ->
            commentText = commentValue
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // 이벤트를 감지하여 키보드를 숨깁니다.
                detectTapGestures(
                    onPress = { /* 터치 감지 시 수행할 동작 */ },
                    onTap = { keyboardController?.hide() }
                )
            },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White), // 여기에 배경색을 하얀색으로 설정,
                title = {
                    Text(
                        text = "댓글",
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        },
        bottomBar = {
            // 댓글 입력 영역
            Row(
                modifier = Modifier
                    .padding(
                        top = 8.dp,
                        start = 12.dp,
                        end = 12.dp, // Row의 끝에도 패딩 추가
                        bottom = 24.dp
                    )
                    .imePadding()  // 키보드에 의해 가려지지 않도록 패딩 추가
            ) {

                // 가변적인 높이를 가진 댓글 입력창
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.weight(1f),
                    maxLines = 5,
                    placeholder = { Text("댓글을 입력하세요") },
                )

                Spacer(modifier = Modifier.width(4.dp))

                // "전송" 버튼 클릭 로직
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            editingComment?.let { (id, _) ->
                                commentViewModel.updateComment(id, commentText)
                                commentViewModel.clearEditingComment()
                                commentText = ""
                            } ?: run {
                                commentViewModel.addComment(recipeId, commentText)
                                commentText = ""
                            }
                        }
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .imePadding()
                            .size(40.dp),
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.Black
                    )
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            // 댓글 목록이 차지할 수 있는 최대 높이를 줄여서 댓글 입력창이 더 위로 올라오게 조정
            val maxHeight = this.maxHeight * 1f  // 예를 들어, 전체 화면 높이의 40%로 조정

            Column {
                CommentsList(
                    commentViewModel = commentViewModel,
                    recipeId = recipeId,
                    maxHeight = maxHeight,
                    tokenManager = tokenManager
                )
            }
        }
    }
}