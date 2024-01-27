package com.recipia.aos.ui.components.recipe.detail.comment

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.recipia.aos.ui.dto.comment.CommentListResponseDto

@Composable
fun CommentsSection(
    comments: List<CommentListResponseDto>,
    loadMoreComments: () -> Unit
) {
    LazyColumn {
        itemsIndexed(comments) { index, comment ->
            CommentItem(comment)

            // 마지막 항목이 렌더링되면 추가 데이터 로드
            if (index == comments.size - 1) {
                loadMoreComments()
            }
        }
    }
}
