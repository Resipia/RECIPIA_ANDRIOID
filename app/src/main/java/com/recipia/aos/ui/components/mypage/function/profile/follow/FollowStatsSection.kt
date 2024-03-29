package com.recipia.aos.ui.components.mypage.function.profile.follow

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import kotlinx.coroutines.launch

/**
 * 팔로잉, 팔로워, 레시피, 위글위글 목록 컴포저
 */
@Composable
fun FollowStatsSection(
    myPageViewModel: MyPageViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {

    val textColor = Color.Black
    val myPageData = myPageViewModel.myPageData.value
    // 레시피 총 개수 관찰
    val recipeCount by myPageViewModel.recipeCount.observeAsState()
    val coroutineScope = rememberCoroutineScope()

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
            Text(text = "${recipeCount ?: 0}", color = textColor) // recipeCount가 null일 경우 0으로 대체
        }

        // 위글위글 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "준비중인 서비스입니다."
                        )
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "위글위글", color = textColor)
            Spacer(modifier = Modifier.height(5.dp)) // 간격 추가
            Text(text = "0", color = textColor) // 고정된 숫자
            Spacer(modifier = Modifier.height(4.dp)) // 간격 추가
        }
    }
}
