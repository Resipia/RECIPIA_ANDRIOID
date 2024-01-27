package com.recipia.aos.ui.components.recipe.detail.comment

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.recipia.aos.ui.model.comment.CommentViewModel

/**
 * 댓글창을 누르면 열리는 Bottom의 sheet 영역 컴포저
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    commentViewModel: CommentViewModel,
    recipeId: Long
) {

    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        containerColor = Color.White, // 하단 시트의 배경색을 하얗게 설정
        content = {
            BoxWithConstraints {
                // 'maxHeight'를 여기에서 계산합니다.
                val maxHeight = this.maxHeight * 0.48f  // 화면 높이의 80%

                CommentsList(
                    commentViewModel = commentViewModel,
                    recipeId = recipeId,
                    maxHeight = maxHeight
                )
            }
        }
    )
}