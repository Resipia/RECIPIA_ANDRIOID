package com.recipia.aos.ui.components.mypage.function

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.components.HorizontalDivider

/**
 * 내가 보는 마이페이지 하단에 표시될 사용자 편의 기능 컴포저
 * (내가 북마크한 레시피, 내가 좋아요한 레시피, 작성한 댓글/대댓글 보기 등등)
 */
@Composable
fun MyPageFeatureItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column {
        FeatureListItem(
            title = title,
            icon = icon,
            onClick = onClick
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth() // 전체 너비를 채우도록 설정
                .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
            thickness = 0.5.dp, // 구분선의 두께 설정
            color = Color.Gray // 구분선의 색상 설정
        )
    }
}