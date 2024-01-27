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
import androidx.compose.ui.unit.sp
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
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            // 닉네임
            Text(
                text = comment.nickname,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            // 생성일자
            Text(
                text = " · ${comment.createDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 10.sp
            )
            // 수정내역
            if (comment.updated) {
                Text(
                    text = " (수정됨)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        // 댓글 내용
        Text(
            text = comment.commentValue,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 16.dp)

        )
    }

    Spacer(modifier = Modifier.height(8.dp))
}