package com.recipia.aos.ui.components.recipe.detail.comment

import TokenManager
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.model.comment.CommentViewModel
import kotlinx.coroutines.launch

/**
 * 댓글 리스트를 불러오는 컴포저
 */
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CommentsList(
    commentViewModel: CommentViewModel,
    recipeId: Long,
    maxHeight: Dp,
    tokenManager: TokenManager
) {
    val commentsResponse by commentViewModel.comments.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val comments = commentsResponse?.content ?: emptyList()

    Column {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(), // 전체 너비를 채우도록 설정
//                .padding(horizontal = 16.dp), // 양쪽에 패딩 적용
            thickness = 0.5.dp, // 구분선의 두께 설정
            color = Color(222, 226, 230) // 구분선의 색상 설정
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (commentsResponse?.content.isNullOrEmpty()) {
                // 데이터가 없을 때의 메시지를 보여줌
                Text(
                    text = "댓글이 존재하지 않습니다.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(maxHeight)  // 여기에서 최대 높이를 적용합니다.
                ) {
                    itemsIndexed(comments) { index, commentDto ->
                        CommentItem(commentDto, commentViewModel, tokenManager, recipeId)

                        // 마지막 항목이 렌더링되면 추가 데이터 로드
                        if (index == comments.size - 1) {
                            coroutineScope.launch {
                                commentViewModel.loadMoreComments(recipeId)
                            }
                        }
                    }
                }
            }
        }
    }
}
