package com.recipia.aos.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
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
        windowInsets = windowInsets
    ) {
        content()
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val selectedItem = remember { mutableStateOf(0) }
    val context = LocalContext.current
    val items = listOf("홈", "위글위글", "채팅", "마이페이지")

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = item) },
                label = { Text(item) },
                selected = selectedItem.value == index,
                onClick = {
                    selectedItem.value = index
                    if (item == "채팅") {
                        Toast.makeText(context, "개발중인 기능입니다", Toast.LENGTH_SHORT).show()
                    } else {
                        // 네비게이션 로직
                        navController.navigate(when (item) {
                            "홈" -> "home"
                            "마이페이지" -> "mypage"
                            // 다른 네비게이션 경로들
                            else -> "home"
                        })
                    }
                }
            )
        }
    }
}