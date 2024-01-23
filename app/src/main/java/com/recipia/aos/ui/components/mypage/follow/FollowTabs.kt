package com.recipia.aos.ui.components.mypage.follow

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FollowTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("팔로잉", "팔로워")
    val tabToServerTypeMap = mapOf("팔로잉" to "following", "팔로워" to "follower")

    val selectedTabIndex = tabs.indexOf(selectedTab).coerceIn(0, tabs.size - 1)

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.White
    ) {
        tabs.forEachIndexed { index, text ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(tabToServerTypeMap[tabs[index]] ?: "following") },
                text = { Text(text) }
            )
        }
    }
}
