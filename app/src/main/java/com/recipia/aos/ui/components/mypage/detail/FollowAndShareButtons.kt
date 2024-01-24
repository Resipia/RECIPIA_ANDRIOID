package com.recipia.aos.ui.components.mypage.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    val myPageData = myPageViewModel.myPageData.value

    // 팔로우 버튼 상태 관리
    var followButtonText by remember { mutableStateOf("팔로우") }
    var followButtonColor by remember { mutableStateOf(Color(149, 117, 205)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp) // 여기에 좌우 패딩 추가
    ) {
        // isMe가 true이면 버튼을 렌더링하지 않음
        if (myPageData != null) {
            if (!myPageData.me) {
                Button(
                    onClick = {
                        // 팔로우/언팔로우 처리
                        followViewModel.followOrUnfollow(
                            targetMemberId = myPageData.memberId ?: 0,
                            onResult = { success, newFollowId ->
                                if (success) {
                                    // 팔로우 상태에 따라 버튼 상태 변경
                                    val isNowFollowing = newFollowId != null
                                    followButtonText = if (isNowFollowing) "팔로잉" else "팔로우"
                                    followButtonColor =
                                        if (isNowFollowing) Color(206, 212, 218) else Color(
                                            149,
                                            117,
                                            205
                                        )

                                    // 데이터 재로딩 전에 상태 업데이트
                                    if (isNowFollowing) {
                                        if (newFollowId != null) {
                                            myPageData.followId = newFollowId
                                        }
                                    } else {
                                        myPageData.followId = 0L
                                    }

                                    // 데이터 재로딩
                                    myPageViewModel.loadMyPageData(targetId)
                                } else {
                                    Toast.makeText(context, "작업 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = followButtonColor),
                    shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "팔로우",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(followButtonText, fontSize = 14.sp, color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp)) // 버튼 사이 간격

        // 공유하기 버튼
        Button(
            onClick = { /* 공유하기 처리 */ },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(233, 236, 239)),
            shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)) // 버튼 모양 변경
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "공유하기",
                modifier = Modifier.size(14.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
            Text("공유하기", fontSize = 14.sp, color = Color.Black) // 텍스트 색상 변경
        }
    }
}