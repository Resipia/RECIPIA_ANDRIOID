package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FollowButton(
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    val buttonText = if (isFollowing) "팔로잉" else "팔로우"
    val buttonColor = if (isFollowing) Color(206,212,218) else Color(149,117,205) // 팔로잉이면 회색, 아니면 파란색

    Button(
        onClick = onFollowClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
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

