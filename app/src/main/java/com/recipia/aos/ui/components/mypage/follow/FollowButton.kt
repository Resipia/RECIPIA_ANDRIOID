package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FollowButton(
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    val buttonText = if (isFollowing) "팔로잉" else "팔로우"
    val buttonColor = if (isFollowing) Color.Gray else MaterialTheme.colorScheme.primary

    Button(
        onClick = {
            // 클릭 시 API 요청 보내고 팔로잉 상태 변경 로직 구현
            onFollowClick()
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(206,212,218), // 버튼 배경색
            contentColor = Color.Black // 버튼 내부 글자색
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = buttonText,
            fontSize = 12.sp
        )
    }
}

