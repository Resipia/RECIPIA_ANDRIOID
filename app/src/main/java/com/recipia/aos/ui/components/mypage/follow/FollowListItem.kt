package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.dto.mypage.follow.FollowListResponseDto

/**
 * 팔로잉/팔로워 페이지 메인 호출 컴포저
 */
@Composable
fun FollowListItem(
    followData: FollowListResponseDto,
    onFollowClick: (FollowListResponseDto) -> Unit
) {

    Row(verticalAlignment = Alignment.CenterVertically) {
        // 프로필 이미지
        Image(
            painter = rememberAsyncImagePainter(followData.preUrl),
            contentDescription = "Profile Picture",
            modifier = Modifier.size(40.dp).clip(CircleShape)
        )

        // 닉네임
        Text(text = followData.nickname, modifier = Modifier.padding(start = 8.dp))

        // 팔로우 버튼
        if (!followData.isMe) {
            FollowButton(
                isFollowing = true,
                onFollowClick = { onFollowClick(followData) }
            )
        }
    }
}