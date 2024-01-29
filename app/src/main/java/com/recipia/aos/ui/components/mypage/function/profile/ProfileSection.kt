package com.recipia.aos.ui.components.mypage.function.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.recipia.aos.R
import com.recipia.aos.ui.components.mypage.function.FollowAndShareButtons
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel

/**
 * 프로필 이미지, 닉네임, 한줄소개, 팔로우 버튼
 */
@Composable
fun ProfileSection(
    myPageViewModel: MyPageViewModel,
    followViewModel: FollowViewModel,
    targetMemberId: Long? = null
) {

    val myPageData = myPageViewModel.myPageData.value
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 프로필 이미지
        val painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context)
                .data(myPageData?.profileImageUrl ?: R.drawable.ic_launcher_foreground)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .transformations(CircleCropTransformation())
                .build()
        )

        Image(
            painter = painter,
            contentDescription = "프로필 이미지",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(0.5.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 닉네임과 팔로워 버튼과 소개
        Column {
            Row {
                Text(
                    text = myPageData?.nickname ?: "익명 사용자",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 2.dp)
                )

                // 팔로우 버튼
                FollowAndShareButtons(
                    myPageViewModel = myPageViewModel,
                    followViewModel = followViewModel,
                    targetId = targetMemberId ?: 0L
                )

            }

            Spacer(modifier = Modifier.height(8.dp))

            myPageData?.introduction?.let {
                Text(text = it, color = Color.Black)
            }
        }
    }
}