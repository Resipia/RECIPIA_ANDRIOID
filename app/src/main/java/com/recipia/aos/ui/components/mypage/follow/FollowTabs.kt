package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * 'FollowTabs' 컴포저블 함수는 팔로잉/팔로워 탭을 표시하는데 사용된다.
 */
@Composable
fun FollowTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {

    // 탭 이름 목록
    val tabs = listOf("팔로잉", "팔로워")

    // 현재 선택된 탭의 인덱스
    val selectedTabIndex = tabs.indexOf(selectedTab).coerceIn(0, tabs.size - 1)

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White
    ) {
        // 각 탭에 대해 반복
        tabs.forEach { text ->
            Tab(
                selected = text == selectedTab, // 현재 탭이 선택된 탭인지 여부
                onClick = { onTabSelected(text) }, // 탭 클릭 시 onTabSelected 호출 (이 함수는 selectedTab 상태를 업데이트하고 viewModel.loadFollowList 함수를 호출하여 새로운 탭 타입에 해당하는 데이터를 로드)
                text = { Text(text) }
            )
        }
    }
}
