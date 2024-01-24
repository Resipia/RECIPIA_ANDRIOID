package com.recipia.aos.ui.components.mypage.detail

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.model.mypage.MyPageViewModel

/**
 * 프로필 이미지, 닉네임, 한줄소개
 */
@Composable
fun ProfileSection(
    myPageViewModel: MyPageViewModel,
) {

    val myPageData = myPageViewModel.myPageData.value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        Image(
            painter = rememberAsyncImagePainter(model = myPageData?.profileImageUrl),
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(0.5.dp, Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 닉네임과 소개
        Column {
            Text(
                text = myPageData?.nickname ?: "익명 사용자",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            myPageData?.introduction?.let {
                Text(text = it, color = Color.Gray)
            }
        }
    }
}