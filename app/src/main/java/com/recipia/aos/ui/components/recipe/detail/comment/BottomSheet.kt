package com.recipia.aos.ui.components.recipe.detail.comment

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.model.comment.CommentViewModel
import kotlinx.coroutines.launch

/**
 * 댓글창을 누르면 열리는 Bottom의 sheet 영역 컴포저
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    commentViewModel: CommentViewModel,
    recipeId: Long
) {
    val coroutineScope = rememberCoroutineScope()
    var commentText by remember { mutableStateOf("") }
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        windowInsets = WindowInsets(0.dp),
        sheetState = modalBottomSheetState,
        onDismissRequest = {
            coroutineScope.launch {
                modalBottomSheetState.hide()
                onDismiss()
            }
        },
        containerColor = Color.White,
        content = {
            BoxWithConstraints {
                // 댓글 목록이 차지할 수 있는 최대 높이를 줄여서 댓글 입력창이 더 위로 올라오게 조정
                val maxHeight = this.maxHeight * 0.3f  // 예를 들어, 전체 화면 높이의 40%로 조정

                Column(
                    modifier = Modifier.imeNestedScroll()
                ) {
                    CommentsList(
                        commentViewModel = commentViewModel,
                        recipeId = recipeId,
                        maxHeight = maxHeight
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )

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

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    // 댓글 추가 로직
                                    // commentViewModel.addComment(recipeId, commentText)
                                    commentText = ""  // 입력 필드 초기화
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(bottom = 6.dp)
                                    .imePadding(),
                                imageVector = Icons.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    )

}
