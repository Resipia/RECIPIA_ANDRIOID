package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.recipia.aos.R
import com.recipia.aos.ui.dto.mypage.follow.FollowListResponseDto

/**
 * 팔로잉/팔로워 페이지 메인 호출 컴포저
 */
@Composable
fun FollowListItem(
    followData: FollowListResponseDto,
    onFollowClick: (FollowListResponseDto) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp) // 항목 사이의 간격 추가
    ) {

        // Coil 라이브러리를 사용하여 이미지 표시
        Image(
            painter = followData.preUrl?.let {
                rememberAsyncImagePainter(model = it)
            } ?: painterResource(id = R.drawable.ic_launcher_foreground), // 기본 이미지 리소스
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(56.dp) // 이미지 크기를 더 크게 조절
                .clip(CircleShape)
                .border(0.5.dp, Color.LightGray, CircleShape) // 연한 테두리 추가
        )

        Spacer(modifier = Modifier.width(16.dp)) // 이미지와 닉네임 사이 간격 추가

        Column(
            modifier = Modifier
                .weight(1f) // 이름과 닉네임 영역이 남은 공간을 모두 차지하도록 설정
        ) {
            // 닉네임
            Text(
                text = followData.nickname,
                fontSize = 14.sp, // 폰트 크기를 12.sp로 설정
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 16.dp) // 닉네임과 팔로우 버튼 사이의 간격 추가
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // 중간 공간을 채우기 위한 Spacer 추가

        // 팔로우 버튼
        if (!followData.isMe) {
            FollowButton(
                isFollowing = followData.followId != null,
                onFollowClick = { onFollowClick(followData) }
            )
        }
    }
}

