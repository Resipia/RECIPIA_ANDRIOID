package com.recipia.aos.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.ai.client.generativeai.Chat
import java.util.Locale

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaults.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit
) {
    // Material Design의 NavigationBar 컴포넌트 사용
    androidx.compose.material3.NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        windowInsets = windowInsets,
    ) {
        content()
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedItem = remember { mutableStateOf(0) }
    val context = LocalContext.current

    // 아이콘 색상 정의
    val selectedIconColor = Color.Black // 선택된 아이템의 아이콘 색상
    val unselectedIconColor = Color.Gray // 선택되지 않은 아이템의 아이콘 색상

    // navController의 상태가 변경될 때마다 selectedItem을 업데이트
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LaunchedEffect(currentRoute) {
        selectedItem.value = when (currentRoute) {
            "home" -> 0
            "my-page" -> 3
            else -> 0
        }
    }

    val items = listOf(
        "홈" to Icons.Filled.Home,
        "위글위글" to Icons.Filled.AddReaction,
        "채팅" to Icons.Filled.Chat,
        "마이페이지" to Icons.Filled.AccountCircle
    )

    Column {
        // 상단 경계선 추가
        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )

        // NavigationBar 스타일을 수정
        NavigationBar(
            containerColor = Color.White, // 배경색을 하얀색으로 설정
            contentColor = unselectedIconColor, // 기본 아이콘 색상 설정
            tonalElevation = 0.dp // 그림자 없음
        ) {
            items.forEachIndexed { index, pair ->
                val (label, icon) = pair
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label) },
                    selected = selectedItem.value == index,
                    onClick = {
                        when (label) {
                            "채팅" -> Toast.makeText(context, "준비중인 서비스입니다.", Toast.LENGTH_SHORT).show()
                            "위글위글" -> Toast.makeText(context, "준비중인 서비스입니다.", Toast.LENGTH_SHORT).show()
                            else -> {
                                navController.navigate(when (label) {
                                    "홈" -> "home"
                                    "마이페이지" -> "my-page"
                                    else -> "home"
                                }) {
                                    launchSingleTop = true
                                }
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedIconColor, // 선택된 아이템의 아이콘 색상
                        unselectedIconColor = unselectedIconColor // 선택되지 않은 아이템의 아이콘 색상
                    )
                )
            }
        }
    }
}
