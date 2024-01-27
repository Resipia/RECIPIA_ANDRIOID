package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FollowButton(
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    val buttonText = if (isFollowing) "팔로잉" else "팔로우"

    Button(
        onClick = onFollowClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isFollowing) Color(222,226,230) else Color.White, // 팔로잉이면 회색, 아니면 흰색
            contentColor = if (isFollowing) Color.Black else Color(56, 142, 60) // 팔로잉이면 검은색, 아니면 초록색
        ),
        border = BorderStroke(1.dp, if (isFollowing) Color(222,226,230) else Color(56, 142, 60)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = buttonText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isFollowing) Color.Black else Color(56, 142, 60),
        )
    }

}

