package com.recipia.aos.ui.components.navigation

import SignUpViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.TopAppBar
import com.recipia.aos.ui.components.category.CategoriesScreen
import com.recipia.aos.ui.components.infinitescroll.ItemListWithInfiniteScroll
import com.recipia.aos.ui.components.mypage.MyPageScreen
import com.recipia.aos.ui.components.signup.SignUpScreen
import com.recipia.aos.ui.dto.Category
import com.recipia.aos.ui.dto.SubCategory
import com.recipia.aos.ui.jwt.JwtTokenManager
import com.recipia.aos.ui.model.RecipeAllListViewModel
import com.recipia.aos.ui.model.factory.MyViewModelFactory
import com.recipia.aos.ui.model.factory.RecipeAllListViewModelFactory

@Composable
fun AppNavigation(
    jwtTokenManager: JwtTokenManager
) {

    val navController = rememberNavController()
    val viewModel: RecipeAllListViewModel = viewModel(
        factory = RecipeAllListViewModelFactory(jwtTokenManager)
    )
    val signUpViewModel: SignUpViewModel = viewModel(
        factory = MyViewModelFactory(LocalContext.current),
        modelClass = SignUpViewModel::class.java
    )
    // jwt가 존재하는지 검증한다.
    val isUserLoggedIn = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isUserLoggedIn.value = jwtTokenManager.hasValidToken()
    }

    // 네비게이션 컨트롤
    NavHost(navController = navController, startDestination = if (isUserLoggedIn.value) "home" else "login") {
        composable("login") {
            // 로그인 화면 구성
            // SignUpViewModel을 네비게이션 구성의 일부로서 주입
            SignUpScreen(navController, signUpViewModel)
        }
        composable("home") {
            Scaffold(
                topBar = { TopAppBar(navController, viewModel) },
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ItemListWithInfiniteScroll(viewModel)
                }
            }
        }
        composable("mypage") {
            MyPageScreen(navController)
        }
        composable("categories") {
            CategoriesScreen(
                navController = navController,
                subCategories = listOf(
                    SubCategory(1, "김치찌개", 1),
                    SubCategory(2, "된장찌개", 1),
                    SubCategory(3, "비빔밥", 1),
                    SubCategory(4, "불고기", 1),
                    SubCategory(5, "짜장면", 2),
                    SubCategory(6, "짬뽕", 2),
                    SubCategory(7, "마파두부", 2),
                    SubCategory(8, "꿔바로우", 2),
                    SubCategory(9, "초밥", 3),
                    SubCategory(10, "라멘", 3),
                    SubCategory(11, "돈부리", 3),
                    SubCategory(12, "우동", 3),
                    SubCategory(13, "파스타", 4),
                    SubCategory(14, "스테이크", 4),
                    SubCategory(15, "피자", 4),
                    SubCategory(16, "샐러드", 4)
                ),
                categories = listOf(
                    Category(1, "한식"),
                    Category(2, "중식"),
                    Category(3, "일식"),
                    Category(4, "양식")
                ),
                onSelectedCategories = { selectedIds ->
                    println("선택된 서브 카테고리 ID: $selectedIds")
                    // 여기에서 선택된 ID들을 처리하는 로직을 추가할 수 있음
                }
            )
        }
    }
}