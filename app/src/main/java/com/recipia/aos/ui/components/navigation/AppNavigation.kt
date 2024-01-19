package com.recipia.aos.ui.components.navigation

import FindIdScreen
import TokenManager
import com.recipia.aos.ui.model.login.LoginViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.recipia.aos.ui.components.login.LoginScreen
import com.recipia.aos.ui.components.login.forgot.EmailVerificationScreen
import com.recipia.aos.ui.components.mypage.MyPageScreen
import com.recipia.aos.ui.components.recipe.create.CreateRecipeScreen
import com.recipia.aos.ui.dto.Category
import com.recipia.aos.ui.dto.SubCategory
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.factory.CategorySelectionViewModelFactory
import com.recipia.aos.ui.model.factory.MyViewModelFactory
import com.recipia.aos.ui.model.factory.RecipeAllListViewModelFactory
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel

@Composable
fun AppNavigation(
    tokenManager: TokenManager
) {

    // 네비게이션 컨트롤러, 레시피 상세 목록, 로그인 관련 모델 세팅
    val navController = rememberNavController()
    val recipeAllListViewModel: RecipeAllListViewModel = viewModel(
        factory = RecipeAllListViewModelFactory(tokenManager)
    )
    val loginViewModel: LoginViewModel = viewModel(
        factory = MyViewModelFactory(LocalContext.current),
        modelClass = LoginViewModel::class.java
    )
    val categorySelectionViewModel: CategorySelectionViewModel = viewModel(
        factory = CategorySelectionViewModelFactory()
    )
    val bookmarkViewModel: BookMarkViewModel = viewModel()

    // jwt 존재 여부를 검증한다.
    val isUserLoggedIn = remember {
        mutableStateOf(tokenManager.hasValidAccessToken())
    }

    // 네비게이션 컨트롤
    NavHost(
        navController = navController, startDestination = if (isUserLoggedIn.value) "home" else "login"
    ) {
        // 로그인 화면
        composable("login") {
            LoginScreen(navController, loginViewModel)
        }
        // 홈 화면(메인 레시피 목록들)
        composable("home") {
            Scaffold(
                topBar = { TopAppBar(navController, recipeAllListViewModel, bookmarkViewModel) },
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                Surface(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    HomeScreen(navController, recipeAllListViewModel, bookmarkViewModel, innerPadding)
                }
            }
        }
        // 마이페이지
        composable("mypage") {
            MyPageScreen(navController)
        }
        // 레시피 생성하기
        composable("create-recipe") {
            CreateRecipeScreen(
                navController,
                categorySelectionViewModel,
                tokenManager
            )
        }
        // ID찾기 화면
        composable("findId") {
            FindIdScreen(navController)
        }
        // 이메일로 ID찾기 화면
        composable("emailVerificationScreen") {
            EmailVerificationScreen(navController)
        }
        // 카테고리 선택 화면
        composable("categories") {
            CategoriesScreen(
                navController = navController,
                viewModel = categorySelectionViewModel,
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