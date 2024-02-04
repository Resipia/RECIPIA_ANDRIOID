package com.recipia.aos.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.recipia.aos.ui.components.home.ElevatedDivider
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import kotlinx.coroutines.launch

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
fun BottomNavigationBar(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    recipeAllListViewModel: RecipeAllListViewModel,
    lazyListState: LazyListState // LazyListState를 인자로 받음
) {
    val selectedItem = remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope() // 코루틴 스코프 생성

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
        ElevatedDivider(
            color = Color(206,212,218),
            thickness = 0.5.dp
        )

        // NavigationBar 스타일을 수정 (높이 추가)
        NavigationBar(
            modifier = Modifier.height(78.dp), // 여기서 높이를 조정합니다.
            containerColor = Color.White,
            contentColor = unselectedIconColor,
            tonalElevation = 1.dp
        ) {
            items.forEachIndexed { index, pair ->
                val (label, icon) = pair
                NavigationBarItem(
                    icon = { Icon(icon, contentDescription = label) },
                    label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black) },
                    selected = selectedItem.value == index,
                    onClick = {
                        when (label) {
                            "홈" -> {
                                if (navController.currentDestination?.route == "home") {
                                    recipeAllListViewModel.makeEmptyListSubCategoryData()
                                    recipeAllListViewModel.refreshItems(recipeAllListViewModel.selectedSubCategories.value)

                                    scope.launch {
                                        lazyListState.scrollToItem(0) // 스크롤을 맨 위로 이동 (코루틴 내에서 호출)
                                    }
                                } else {
                                    navController.navigate("home") {
                                        launchSingleTop = true
                                    }
                                }
                            }
                            "마이페이지" -> {
                                navController.navigate("my-page") {
                                    launchSingleTop = true // 이렇게하면 데이터를 변경 안함(쿼리호출x) 그래서 수정해야할지 고민이다.
                                }
                            }
                            "채팅", "위글위글" -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar("준비중인 서비스입니다.")
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
