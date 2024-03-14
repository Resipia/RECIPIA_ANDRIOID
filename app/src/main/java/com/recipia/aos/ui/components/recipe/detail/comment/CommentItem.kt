package com.recipia.aos.ui.components.recipe.detail.comment

import TokenManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.api.dto.comment.CommentListResponseDto
import com.recipia.aos.ui.model.comment.CommentViewModel

/**
 * 댓글 콘텐츠 정보
 */
@Composable
fun CommentItem(
    commentDto: com.recipia.aos.ui.api.dto.comment.CommentListResponseDto,
    commentViewModel: CommentViewModel,
    tokenManager: TokenManager,
    recipeId: Long
) {
    val loadMemberId = tokenManager.loadMemberId()
    var showDialog by remember { mutableStateOf(false) }  // 다이얼로그 표시 상태

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            // 닉네임
            Text(
                text = commentDto.nickname,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f) // Expand the space to the right
            )
            // 생성일자
            Text(
                text = " · ${commentDto.createDate}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 12.sp
            )
            // 수정내역
            if (commentDto.updated) {
                Text(
                    text = " (수정됨)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
        // 댓글 내용
        Text(
            text = commentDto.commentValue,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 4.dp)
        )
    }

    Row {
        // todo: 대댓글은 주석 (기능 완성후 해제하기)
//        Button(
//            onClick = {
//                // 대댓글 보기
//            },
//            modifier = Modifier
//                .height(40.dp)
//                .padding(
//                    start = 16.dp,
//                    top = 4.dp,
//                    bottom = 8.dp
//                )
//                .width(48.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.White,
//                contentColor = Color.Black
//            ),
//            shape = RectangleShape,
//            border = BorderStroke(1.dp, Color(222, 226, 230)),
//            contentPadding = PaddingValues(0.dp) // This removes padding around the text inside
//        ) {
//            Text(
//                "대댓글",
//                style = MaterialTheme.typography.labelLarge,
//                color = Color.Black,
//                fontWeight = FontWeight.Bold,
//                fontSize = 8.sp,
//                modifier = Modifier.padding(0.dp) // 텍스트의 padding을 제거하여 경계에 딱 붙어 있게 만듭니다.
//            )
//        }

        // 만약 본인 댓글이면 수정 및 삭제 버튼 추가
        if (commentDto.memberId == loadMemberId) {

            // 댓글 수정버튼
            Button(
                onClick = {
                    commentViewModel.startEditingComment(commentDto.id, commentDto.commentValue)
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(
//                        start = 4.dp, // todo: 위에 대댓글 살리면 이걸로 복구
                        start = 16.dp,
                        top = 12.dp,
                        bottom = 4.dp
                    )
                    .width(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color(222, 226, 230)),
                contentPadding = PaddingValues(0.dp) // This removes padding around the text inside
            ) {
                Text(
                    "수정",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(0.dp) // 텍스트의 padding을 제거하여 경계에 딱 붙어 있게 만듭니다.
                )
            }

            // todo: 삭제는 dialog를 한번 띄운다. (삭제하시겠습니까?)
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .height(40.dp)
                    .padding(
                        start = 4.dp,
                        top = 12.dp,
                        end = 16.dp,
                        bottom = 4.dp
                    )
                    .width(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color(222, 226, 230)),
                contentPadding = PaddingValues(0.dp) // This removes padding around the text inside
            ) {
                Text(
                    "삭제",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    modifier = Modifier.padding(0.dp) // 텍스트의 padding을 제거하여 경계에 딱 붙어 있게 만듭니다.
                )
            }

            // 삭제 확인 다이얼로그
            if (showDialog) {
                AlertDialog(
                    containerColor = Color.White, // AlertDialog 배경색을 하얀색으로 설정
                    textContentColor = Color.Black, // 글자색을 검정색으로 설정
                    onDismissRequest = { showDialog = false },
                    title = { Text("삭제 확인", color = Color.Black) },
                    text = { Text("정말 작성하신 댓글을 삭제하시겠습니까?", color = Color.Black) },
                    confirmButton = {
                        Button(
                            onClick = {
                                commentViewModel.requestDeleteComment(commentDto.id, recipeId)
                                showDialog = false  // 다이얼로그 닫기
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(27, 94, 32),
                                contentColor = Color.White
                            )
                        ) {
                            Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(27, 94, 32),
                                contentColor = Color.White
                            )
                        ) {
                            Text("취소", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }


    Spacer(modifier = Modifier.height(16.dp))

    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth() // 전체 너비를 채우도록 설정
            .padding(horizontal = 8.dp), // 양쪽에 패딩 적용
        thickness = 0.5.dp, // 구분선의 두께 설정
        color = Color(222, 226, 230) // 구분선의 색상 설정
    )
}