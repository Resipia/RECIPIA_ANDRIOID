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
    type: String
) {
    val serverTypeToTabMap = mapOf("following" to "팔로잉", "follower" to "팔로워")
    val validType = serverTypeToTabMap[type] ?: "팔로잉"
    var selectedTab by remember { mutableStateOf(validType) }

    // 화면이 처음 렌더링될 때 팔로잉 데이터 로딩 시작
    LaunchedEffect(key1 = true) {
        viewModel.loadFollowList(targetMemberId)
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White),
                title = { Text(text = "", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
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
            FollowTabs(selectedTab = selectedTab) { serverType ->
                selectedTab = serverTypeToTabMap[serverType] ?: "팔로잉"
                viewModel.changeType(serverType, targetMemberId)
            }
            LazyColumn {
                itemsIndexed(viewModel.followList) { index, followData ->
                    if (index >= viewModel.followList.size - 1) {
                        viewModel.loadMore(targetMemberId)
                    }
                    FollowListItem(
                        followData = followData,
                        onFollowClick = { data ->
                            // 팔로우/언팔로우 액션 처리
                            // 예: viewModel.followOrUnfollow(data.memberId)
                        }
                    )
                }
            }
        }
    }
}