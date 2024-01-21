package com.recipia.aos.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
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

    // navController의 상태가 변경될 때마다 selectedItem을 업데이트
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    LaunchedEffect(currentRoute) {
        selectedItem.value = when (currentRoute) {
            "home" -> 0
            "my-page" -> 3
            // 여기에 다른 경로에 대한 처리를 추가
            else -> 0
        }
    }

    val items = listOf(
        "홈" to Icons.Filled.Home,
        "위글위글" to Icons.Filled.Settings, // 여기에 해당하는 아이콘을 선택하세요
        "채팅" to Icons.Filled.Email,
        "마이페이지" to Icons.Filled.Person
    )

    NavigationBar {
        items.forEachIndexed { index, pair ->
            val (label, icon) = pair
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = selectedItem.value == index,
                onClick = {
                    when (label) {
                        "채팅" -> Toast.makeText(context, "준비중인 서비스입니다.", Toast.LENGTH_SHORT).show()
                        else -> {
                            navController.navigate(when (label) {
                                "홈" -> "home"
                                "마이페이지" -> "my-page"
                                // 다른 네비게이션 경로들
                                else -> "home"
                            }) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )
        }
    }
}
