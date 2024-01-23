package com.recipia.aos.ui.components.navigation

import TokenManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.recipia.aos.ui.components.forgot.email.EmailVerificationForgotIdScreen
import com.recipia.aos.ui.components.forgot.email.FindEmailScreen
import com.recipia.aos.ui.components.forgot.password.PasswordResetScreen
import com.recipia.aos.ui.components.home.HomeScreen
import com.recipia.aos.ui.components.login.LoginScreen
import com.recipia.aos.ui.components.mypage.MyPageScreen
import com.recipia.aos.ui.components.mypage.follow.FollowPageScreen
import com.recipia.aos.ui.components.recipe.create.CategorySelectScreen
import com.recipia.aos.ui.components.recipe.create.CreateRecipeScreen
import com.recipia.aos.ui.components.recipe.detail.RecipeDetailScreen
import com.recipia.aos.ui.components.recipe.detail.SearchScreen
import com.recipia.aos.ui.components.signup.SignUpFirstFormScreen
import com.recipia.aos.ui.components.signup.SignUpSecondFormScreen
import com.recipia.aos.ui.components.signup.SignUpThirdFormScreen
import com.recipia.aos.ui.dto.Category
import com.recipia.aos.ui.dto.SubCategory
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.factory.BookMarkViewModelFactory
import com.recipia.aos.ui.model.factory.CategorySelectionViewModelFactory
import com.recipia.aos.ui.model.factory.FollowViewModelFactory
import com.recipia.aos.ui.model.factory.MyPageViewModelFactory
import com.recipia.aos.ui.model.factory.MyViewModelFactory
import com.recipia.aos.ui.model.factory.RecipeAllListViewModelFactory
import com.recipia.aos.ui.model.factory.RecipeCreateModelFactory
import com.recipia.aos.ui.model.factory.RecipeDetailViewModelFactory
import com.recipia.aos.ui.model.forgot.ForgotViewModel
import com.recipia.aos.ui.model.login.LoginViewModel
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import com.recipia.aos.ui.model.signup.PhoneNumberAuthViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel

@Composable
fun AppNavigation(
    tokenManager: TokenManager
) {

    /**
     * 모든 composable 루트에서 동일한 ViewModel 인스턴스를 공유하고 싶다면,
     * ViewModel 인스턴스를 NavHost 바깥에서 생성하고 이 Model 인스턴스를 공유하고 싶은 모든 composable 블록에
     * 동일한 인스턴스를 주입하는 방식을 사용하면 된다.
     */
    val navController = rememberNavController()
    val recipeAllListViewModel: RecipeAllListViewModel = viewModel(
        factory = RecipeAllListViewModelFactory(tokenManager)
    )
    val loginViewModel: LoginViewModel = viewModel(
        factory = MyViewModelFactory(tokenManager),
    )
    val categorySelectionViewModel: CategorySelectionViewModel = viewModel(
        factory = CategorySelectionViewModelFactory()
    )
    val bookmarkViewModel: BookMarkViewModel = viewModel(
        factory = BookMarkViewModelFactory(tokenManager)
    )
    val recipeCreateModel: RecipeCreateModel = viewModel(
        factory = RecipeCreateModelFactory(tokenManager)
    )
    val recipeDetailViewModel: RecipeDetailViewModel = viewModel(
        factory = RecipeDetailViewModelFactory(tokenManager)
    )
    val myPageViewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(tokenManager)
    )
    val followViewModel: FollowViewModel = viewModel(
        factory = FollowViewModelFactory(tokenManager)
    )
    val phoneNumberAuthViewModel: PhoneNumberAuthViewModel = viewModel()
    val signUpViewModel: SignUpViewModel = viewModel()
    val forgotViewModel: ForgotViewModel = viewModel()

    // jwt 존재 여부를 검증한다.
    val isUserLoggedIn = remember {
        mutableStateOf(tokenManager.hasValidAccessToken())
    }

    // 네비게이션 컨트롤
    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn.value) "home" else "login"
    ) {
        // 로그인 화면
        composable("login") {
            LoginScreen(navController, loginViewModel)
        }
        // 홈 화면(메인 레시피 목록들)
        composable("home") {
            HomeScreen(navController, recipeAllListViewModel, bookmarkViewModel)
        }
        // 검색화면
        composable("searchScreen") {
            SearchScreen(navController)
        }
        // 마이페이지
        composable("my-page") {
            MyPageScreen(navController, myPageViewModel)
        }
        // 팔로잉/팔로워 페이지
        composable(
            route = "followList/{type}/{memberId}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
                navArgument("memberId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "following"
            val memberId = backStackEntry.arguments?.getLong("memberId") ?: 0L

            FollowPageScreen(navController, followViewModel, memberId, type)
        }
        // 레시피 상세보기 화면
        composable(
            route = "recipeDetail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            RecipeDetailScreen(
                recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L,
                recipeDetailViewModel,
                navController
            )
        }
        // 레시피 생성하기
        composable("create-recipe") {
            CreateRecipeScreen(
                navController,
                categorySelectionViewModel,
                recipeCreateModel,
                tokenManager
            )
        }
        // ID찾기 화면
        composable("findId") {
            FindEmailScreen(navController)
        }
        // 이메일로 ID찾기 화면
        composable("emailVerificationScreen") {
            EmailVerificationForgotIdScreen(navController, forgotViewModel)
        }
        // PASSWORD찾기 화면
        composable("findPassword") {
            PasswordResetScreen(navController, forgotViewModel)
        }
        // 회원가입 1단계: 전화번호 인증 및 회원가입 동의 form 화면
        composable("signUpFirstForm") {
            SignUpFirstFormScreen(navController, phoneNumberAuthViewModel, signUpViewModel)
        }
        // 회원가입 2단계: 이메일, 비밀번호 form 화면
        composable("signUpSecondForm") {
            SignUpSecondFormScreen(navController, signUpViewModel, phoneNumberAuthViewModel)
        }
        // 회원가입 3단계: 프로필 세팅 form
        composable("signUpThirdForm") {
            SignUpThirdFormScreen(navController, signUpViewModel, phoneNumberAuthViewModel)
        }
        // 카테고리 선택 화면
        composable("categorySelect") {
            CategorySelectScreen(
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