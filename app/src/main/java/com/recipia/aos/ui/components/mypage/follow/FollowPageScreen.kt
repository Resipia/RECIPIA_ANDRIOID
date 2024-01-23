package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowPageScreen(
    navController: NavController,
    viewModel: FollowViewModel,
    targetMemberId: Long,
    initialType: String // "following" 또는 "follower"
) {
    val serverTypeToTabMap = mapOf("following" to "팔로잉", "follower" to "팔로워")
    var selectedTab by remember { mutableStateOf(serverTypeToTabMap[initialType] ?: "팔로잉") }

    LaunchedEffect(key1 = initialType) {
        viewModel.loadFollowList(targetMemberId, initialType)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White),
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // 리스트를 초기화
                            viewModel.clearFollowList()
                            // 뒤로 가기
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold로부터 제공된 패딩 적용
                .padding(16.dp),
        ) {
            // 탭 구성
            FollowTabs(selectedTab) { newType ->
                val serverType = serverTypeToTabMap.entries.firstOrNull { it.value == newType }?.key ?: "following"
                viewModel.loadFollowList(targetMemberId, serverType)
                selectedTab = newType
            }
            LazyColumn {
                itemsIndexed(viewModel.followList) { index, followData ->
                    if (index >= viewModel.followList.size - 1) {
                        viewModel.loadMore(targetMemberId)
                    }
                    FollowListItem(
                        followData = followData,
                        onFollowClick = { data ->
                            // 팔로우/언팔로우 버튼 클릭 시 실행되는 로직
                            viewModel.followOrUnfollow(data.memberId) { isSuccess, followId ->
                                // 요청이 성공적이면 (isSuccess == true)
                                if (isSuccess) {
                                    // 현재 리스트에서 클릭된 아이템의 인덱스를 찾음
                                    val index = viewModel.followList.indexOf(data)
                                    if (index != -1) {
                                        // 클릭된 아이템의 팔로우 상태를 업데이트
                                        val updatedData = data.copy(followId = followId)
                                        viewModel.followList[index] = updatedData
                                    }
                                } else {
                                    // 요청 실패 시, 에러 메시지 처리 등을 할 수 있음
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}