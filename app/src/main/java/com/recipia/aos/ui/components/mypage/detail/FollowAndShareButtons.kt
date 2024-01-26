package com.recipia.aos.ui.components.mypage.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel

/**
 * 팔로우, 공유하기 버튼 컴포저
 */
@Composable
fun FollowAndShareButtons(
    myPageViewModel: MyPageViewModel,
    followViewModel: FollowViewModel,
    targetId: Long,
    context: Context
) {

    val myPageData = myPageViewModel.myPageData.observeAsState().value
    val isFollowing = myPageViewModel.isFollowing.observeAsState(false).value

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.weight(1f))

        // isMe가 true이면 버튼을 렌더링하지 않음
        if (myPageData != null && !myPageData.me) {

            val followButtonText = if (isFollowing) "팔로잉" else "팔로우"
            val textColor = if (isFollowing) Color.Black else Color(56, 142, 60)
            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = if (isFollowing) Color(222, 226, 230) else Color.White,
                contentColor = textColor // 버튼 내용의 색상을 textColor로 설정
            )

            Button(
                onClick = {
                    // 팔로우/언팔로우 처리
                    followViewModel.followOrUnfollow(

                        targetMemberId = myPageData.memberId ?: 0,
                        onResult = { success, newFollowId ->
                            if (success) {
                                // 팔로우 상태 업데이트
                                val isNowFollowing = newFollowId != null
                                myPageViewModel.updateFollowingStatus(isNowFollowing)

                                // 데이터 재로딩 전에 상태 업데이트
                                if (isNowFollowing) {
                                    if (newFollowId != null) {
                                        myPageData.followId = newFollowId
                                    }
                                } else {
                                    myPageData.followId = 0L
                                }

                                myPageViewModel.resetMyPageData()
                                // 마이페이지 데이터 리로드
                                myPageViewModel.loadMyPageData(targetId)

                            } else {
                                Toast.makeText(context, "작업 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(32.dp),
                colors = buttonColors,
                border = BorderStroke(1.dp, if (isFollowing) Color(222,226,230) else Color(56, 142, 60)),
                shape = RoundedCornerShape(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "팔로우",
                    modifier = Modifier.size(12.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    followButtonText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}