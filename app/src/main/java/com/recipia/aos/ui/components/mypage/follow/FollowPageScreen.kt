package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel

/**
 * 'FollowPageScreen' 컴포저블 함수는 팔로잉/팔로워 페이지를 표시하는데 사용된다..
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowPageScreen(
    navController: NavController,
    viewModel: FollowViewModel,
    recipeAllListViewModel: RecipeAllListViewModel,
    targetMemberId: Long,
    initialType: String //  "following" 또는 "follower" 중 하나
) {

    // 탭 이름과 해당하는 서버 타입을 매핑
    val serverTypeToTabMap = mapOf("following" to "팔로잉", "follower" to "팔로워")

    // 초기 선택된 탭 상태 (기본값은 "팔로잉")
    var selectedTab by remember { mutableStateOf(serverTypeToTabMap[initialType] ?: "팔로잉") }

    val lazyListState = rememberLazyListState() // LazyListState 인스턴스 생성

    // 이 함수는 initialType이 변경될 때마다 실행된다. 이는 사용자가 처음 페이지에 들어왔을 때 초기 데이터를 로드하는 데 사용된다. (초기 한 번만 실행됨)
    LaunchedEffect(key1 = initialType) {
        viewModel.loadFollowList(targetMemberId, initialType)
    }

    LaunchedEffect(key1 = viewModel.followResult) {
        viewModel.followResult.collect { result ->
            result?.let { (isSuccess, followId) ->

                val memberIdToUpdate = viewModel.lastRequestedMemberId // 저장된 memberId 사용
                if (isSuccess && memberIdToUpdate != null) {
                    viewModel.followList.forEachIndexed { index, followData ->
                        if (followData.memberId == memberIdToUpdate) { // memberId 비교
                            val updatedItem = followData.copy(followId = followId)
                            viewModel.updateFollowListItem(index, updatedItem)
                        }
                    }
                } else {
                    // 실패 처리 로직
                }
            }
        }
    }

    // 스낵바 설정
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
        bottomBar = {
            BottomNavigationBar(
                navController,
                snackbarHostState,
                recipeAllListViewModel,
                lazyListState
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold로부터 제공된 패딩 적용
                .padding(16.dp),
        ) {
            // 탭 구성
            FollowTabs(selectedTab) { newType ->
                // 새로운 타입에 맞는 데이터를 로드하고 선택된 탭 상태를 업데이트
                val serverType = serverTypeToTabMap.entries.firstOrNull { it.value == newType }?.key
                    ?: "following"
                viewModel.loadFollowList(targetMemberId, serverType) // 탭 클릭하면 이 함수가 호출되어 데이터를 로드한다.
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
                            // 팔로우/언팔로우 요청만 하고 결과 처리는 LaunchedEffect 내에서 함
                            viewModel.followOrUnfollow(data.memberId)
                        }
                    )
                }
            }
        }
    }
}