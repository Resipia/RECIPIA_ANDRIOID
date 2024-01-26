package com.recipia.aos.ui.components.recipe.detail.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.dto.comment.CommentListResponseDto

/**
 * 댓글 콘텐츠 정보
 */
@Composable
fun CommentItem(
    comment: CommentListResponseDto
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = comment.nickname,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = " · ${comment.createDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            if (comment.updated) {
                Text(
                    text = " (수정됨)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = comment.commentValue,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)

        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}