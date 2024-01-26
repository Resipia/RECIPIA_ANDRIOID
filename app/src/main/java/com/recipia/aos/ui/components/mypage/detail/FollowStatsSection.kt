package com.recipia.aos.ui.components.mypage.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.mypage.MyPageViewModel

/**
 * 팔로잉, 팔로워, 레시피, 위글위글 목록 컴포저
 */
@Composable
fun FollowStatsSection(
    myPageViewModel: MyPageViewModel,
    navController: NavController
) {

    val textColor = Color.Black
    val myPageData = myPageViewModel.myPageData.value

    if (myPageData != null) {
        myPageViewModel.getRecipeTotalCount(myPageData.memberId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp) // 여기에 좌우 패딩 추가
            .border(
                width = 1.dp,
                color = Color(233, 236, 239),
                shape = RoundedCornerShape(8.dp) // 모서리 둥글게
            )
            .padding(vertical = 16.dp) // 여기에 패딩을 추가
    ) {
        // 팔로잉 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    // 팔로잉 리스트 화면으로 이동
                    if (myPageData != null) {
                        navController.navigate("followList/following/${myPageData.memberId}")
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "팔로잉", color = textColor)
            Spacer(modifier = Modifier.height(5.dp)) // 간격 추가
            if (myPageData != null) {
                Text(text = "${myPageData.followingCount}", color = textColor)
            }
            Spacer(modifier = Modifier.height(4.dp)) // 간격 추가
        }

        // 팔로워 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    // 팔로잉 리스트 화면으로 이동
                    if (myPageData != null) {
                        navController.navigate("followList/follower/${myPageData.memberId}")
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "팔로워", color = textColor)
            Spacer(modifier = Modifier.height(5.dp)) // 간격 추가
            if (myPageData != null) {
                Text(text = "${myPageData.followerCount}", color = textColor)
            }
            Spacer(modifier = Modifier.height(4.dp)) // 간격 추가
        }

        // 레시피 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    myPageViewModel.currentPageType.value = MyPageViewModel.PageType.TARGET_MEMBER
                    if (myPageData != null) {
                        navController.navigate("select-recipe-screen/${myPageData.memberId}")
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "레시피", color = textColor)
            Spacer(modifier = Modifier.height(5.dp)) // 간격 추가

            // 레시피 카운트 표시
            val recipeCount = myPageViewModel.recipeCount.value
            if (recipeCount != null) {
                Text(text = "$recipeCount", color = textColor)
            } else {
                // 데이터가 로드되기 전에는 "로딩 중" 또는 공백 표시
                Text(text = "0", color = textColor)
                // 또는 Text를 아예 표시하지 않음
            }
        }

        // 위글위글 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { /* 위글위글 페이지 이동 로직 */ },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "위글위글", color = textColor)
            Spacer(modifier = Modifier.height(5.dp)) // 간격 추가
            Text(text = "0", color = textColor) // 고정된 숫자
            Spacer(modifier = Modifier.height(4.dp)) // 간격 추가
        }
    }
}
