package com.recipia.aos.ui.components.navigation

import TokenManager
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.recipia.aos.ui.components.forgot.email.EmailVerificationForgotIdScreen
import com.recipia.aos.ui.components.forgot.email.FindEmailScreen
import com.recipia.aos.ui.components.forgot.password.PasswordFindSuccessScreen
import com.recipia.aos.ui.components.forgot.password.PasswordResetScreen
import com.recipia.aos.ui.components.home.CategorySelectRecipeScreen
import com.recipia.aos.ui.components.home.HomeScreen
import com.recipia.aos.ui.components.home.SplashScreen
import com.recipia.aos.ui.components.home.search.RecipeSearchScreen
import com.recipia.aos.ui.components.login.LoginScreen
import com.recipia.aos.ui.components.mypage.MyPageRecipeListScreen
import com.recipia.aos.ui.components.mypage.MyPageScreen
import com.recipia.aos.ui.components.mypage.follow.FollowPageScreen
import com.recipia.aos.ui.components.mypage.function.PasswordChangeSuccessScreen
import com.recipia.aos.ui.components.mypage.function.accoount.AccountSettingsScreen
import com.recipia.aos.ui.components.mypage.function.accoount.PasswordChangeScreen
import com.recipia.aos.ui.components.mypage.function.ask.AskCreateScreen
import com.recipia.aos.ui.components.mypage.function.ask.AskDetailScreen
import com.recipia.aos.ui.components.mypage.function.ask.AskListPageScreen
import com.recipia.aos.ui.components.mypage.function.ask.AskSuccessScreen
import com.recipia.aos.ui.components.mypage.function.profile.ProfileEditScreen
import com.recipia.aos.ui.components.recipe.create.CategorySelectScreen
import com.recipia.aos.ui.components.recipe.create.RecipeCreateScreen
import com.recipia.aos.ui.components.recipe.detail.RecipeDetailScreen
import com.recipia.aos.ui.components.recipe.detail.comment.CommentPageScreen
import com.recipia.aos.ui.components.recipe.search.MongoIngredientAndHashTagSearchScreen
import com.recipia.aos.ui.components.recipe.update.RecipeUpdateScreen
import com.recipia.aos.ui.components.signup.SignUpFirstPhoneValidScreen
import com.recipia.aos.ui.components.signup.SignUpSecondFormScreen
import com.recipia.aos.ui.components.signup.SignUpSuccessScreen
import com.recipia.aos.ui.components.signup.SignUpThirdFormScreen
import com.recipia.aos.ui.components.signup.agree.DataRetentionConsent
import com.recipia.aos.ui.components.signup.agree.PersonalInfoConsentScreen
import com.recipia.aos.ui.api.dto.search.SearchType
import com.recipia.aos.ui.components.chat.ChatRoomListScreen
import com.recipia.aos.ui.components.chat.ChatRoomScaffoldScreen
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.chat.ChatViewModel
import com.recipia.aos.ui.model.comment.CommentViewModel
import com.recipia.aos.ui.model.factory.AskViewModelFactory
import com.recipia.aos.ui.model.factory.BookMarkViewModelFactory
import com.recipia.aos.ui.model.factory.CategorySelectionViewModelFactory
import com.recipia.aos.ui.model.factory.ChatViewModelFactory
import com.recipia.aos.ui.model.factory.CommentViewModelFactory
import com.recipia.aos.ui.model.factory.FollowViewModelFactory
import com.recipia.aos.ui.model.factory.LikeViewModelFactory
import com.recipia.aos.ui.model.factory.MongoSearchViewModelFactory
import com.recipia.aos.ui.model.factory.MyPageViewModelFactory
import com.recipia.aos.ui.model.factory.MyViewModelFactory
import com.recipia.aos.ui.model.factory.RecipeAllListViewModelFactory
import com.recipia.aos.ui.model.factory.RecipeCreateModelFactory
import com.recipia.aos.ui.model.factory.RecipeDetailViewModelFactory
import com.recipia.aos.ui.model.forgot.ForgotViewModel
import com.recipia.aos.ui.model.login.LoginViewModel
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.mypage.ask.AskViewModel
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel
import com.recipia.aos.ui.model.recipe.like.LikeViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import com.recipia.aos.ui.model.search.MongoSearchViewModel
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
    val mongoSearchViewModel: MongoSearchViewModel = viewModel(
        factory = MongoSearchViewModelFactory(tokenManager)
    )
    val commentViewModel: CommentViewModel = viewModel(
        factory = CommentViewModelFactory(tokenManager)
    )
    val likeViewModel: LikeViewModel = viewModel(
        factory = LikeViewModelFactory(tokenManager)
    )
    val askViewModel: AskViewModel = viewModel(
        factory = AskViewModelFactory(tokenManager)
    )
    val chatViewModel: ChatViewModel = viewModel(
        factory = ChatViewModelFactory(tokenManager)
    )
    val phoneNumberAuthViewModel: PhoneNumberAuthViewModel = viewModel()
    val signUpViewModel: SignUpViewModel = viewModel()
    val forgotViewModel: ForgotViewModel = viewModel()

    // 네비게이션 컨트롤 시작
    NavHost(
        navController = navController,
        startDestination = "splash-screen"
    ) {
        // 로딩화면
        composable("splash-screen") {
            SplashScreen(navController, tokenManager)
        }
        // 로그인 화면
        composable("login") {
            LoginScreen(navController, loginViewModel)
        }
        // 홈 화면(메인 레시피 목록들)
        composable("home") {
            // 홈 화면 호출할때는 카테고리 정보 초기화하기
//            recipeAllListViewModel.makeEmptyListSubCategoryData()
            HomeScreen(navController, recipeAllListViewModel, bookmarkViewModel)
        }
        // 채팅방 이동
        composable("chat-list") {
            ChatRoomScaffoldScreen(chatViewModel, navController, recipeAllListViewModel)
        }
        // 카테고리 조건 조회
        composable("category-recipe-search") {
            CategorySelectRecipeScreen(
                navController,
                categorySelectionViewModel,
                onSelectedCategories = { selectedIds ->
                    println("선택된 서브 카테고리 ID: $selectedIds")
                    // 여기에서 선택된 ID들을 처리하는 로직을 추가할 수 있음
                },
                recipeAllListViewModel = recipeAllListViewModel
            )
        }
        // 홈 화면 제목으로 레시피 검색하는 화면
        composable("recipe-search") {
            RecipeSearchScreen(
                navController,
                recipeAllListViewModel,
                bookmarkViewModel
            )
        }
        // 재료, 해시태그 검색/추가 화면
        composable(
            route = "search-Screen/{type}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val typeString =
                backStackEntry.arguments?.getString("type") ?: com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG.toString()
            val type = com.recipia.aos.ui.api.dto.search.SearchType.valueOf(typeString)
            MongoIngredientAndHashTagSearchScreen(navController, mongoSearchViewModel, type)
        }
        // 내가보는 마이페이지
        composable("my-page") {
            MyPageScreen(
                navController,
                myPageViewModel,
                recipeAllListViewModel,
                followViewModel,
                bookmarkViewModel,
                tokenManager
            )
        }
        // 남의 마이페이지 (RecipeDetailViewModel에서 memberId 사용)
        composable("other-user-page/{memberId}") { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId")?.toLongOrNull()
            MyPageScreen(
                navController,
                myPageViewModel,
                recipeAllListViewModel,
                followViewModel,
                bookmarkViewModel,
                tokenManager,
                memberId
            )
        }
        // 마이페이지에서 북마크/좋아요한 레시피 보기
        composable("select-recipe-screen/{memberId}") { backStackEntry ->
            val memberId = backStackEntry.arguments?.getString("memberId")?.toLongOrNull()
            MyPageRecipeListScreen(
                navController = navController,
                bookmarkViewModel = bookmarkViewModel,
                myPageViewModel = myPageViewModel,
                targetMemberId = memberId, // 여기서 전달된 데이터를 사용
                tokenManager = tokenManager
            )
        }
        // 계정 정보 수정 목록 페이지
        composable("account-settings") {
            AccountSettingsScreen(
                navController = navController
            )
        }
        // 비밀번호 변경 페이지
        composable("password-change") {
            PasswordChangeScreen(
                navController = navController,
                myPageViewModel = myPageViewModel
            )
        }
        // 비밀번호 변경 성공 페이지
        composable("password-change-success") {
            PasswordChangeSuccessScreen(navController)
        }
        // 프로필 수정 화면
        composable("profile-edit") {
            ProfileEditScreen(
                navController = navController,
                myPageViewModel = myPageViewModel,
                signUpViewModel = signUpViewModel
            )
        }
        // 문의하기 목록 페이지
        composable("ask-list") {
            AskListPageScreen(
                navController = navController,
                askViewModel = askViewModel
            )
        }
        // 문의하기 작성 페이지
        composable("ask-create") {
            AskCreateScreen(
                navController = navController,
                askViewModel = askViewModel
            )
        }
        // 문의하기 상세보기 페이지
        composable("askDetail/{askId}") { backStackEntry ->
            AskDetailScreen(
                askId = backStackEntry.arguments?.getString("askId")?.toLong() ?: 0,
                navController = navController,
                askViewModel = askViewModel
            )
        }
        // 문의하기 성공 페이지
        composable("ask-success") {
            AskSuccessScreen(navController = navController)
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

            FollowPageScreen(
                navController,
                followViewModel,
                recipeAllListViewModel,
                memberId,
                type
            )
        }
        // 레시피 상세보기 화면
        composable(
            route = "recipeDetail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            RecipeDetailScreen(
                recipeId = recipeId,
                recipeDetailViewModel = recipeDetailViewModel,
                likeViewModel = likeViewModel,
                commentViewModel = commentViewModel,
                myPageViewModel = myPageViewModel,
                navController = navController,
                tokenManager = tokenManager
            )
        }
        // 댓글 화면
        composable(
            "comment/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            CommentPageScreen(commentViewModel, navController, recipeId, tokenManager)
        }
        // 레시피 생성하기
        composable("create-recipe") {
            RecipeCreateScreen(
                navController,
                categorySelectionViewModel,
                mongoSearchViewModel,
                recipeCreateModel
            )
        }
        // 레시피 수정하기
        composable("update-recipe") {
            RecipeUpdateScreen(
                navController = navController,
                recipeDetailViewModel = recipeDetailViewModel,
                categorySelectionViewModel = categorySelectionViewModel,
                mongoSearchViewModel = mongoSearchViewModel,
                recipeCreateModel = recipeCreateModel
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
        // 비밀번호 찾기(재발급) 성공 화면
        composable("passwordFindSuccess") {
            PasswordFindSuccessScreen(navController)
        }
        // 회원가입 1단계: 전화번호 인증 및 회원가입 동의 form 화면
        composable("signUpFirstForm") {
            SignUpFirstPhoneValidScreen(navController, phoneNumberAuthViewModel, signUpViewModel)
        }
        // 개인정보 수집 및 동의 여부
        composable("personalInfoConsent") {
            PersonalInfoConsentScreen(navController)
        }
        // 개인정보 보관 및 파기 동의
        composable("dataRetentionConsent") {
            DataRetentionConsent(navController)
        }
        // 회원가입 2단계: 이메일, 비밀번호 form 화면
        composable("signUpSecondForm") {
            SignUpSecondFormScreen(navController, signUpViewModel, phoneNumberAuthViewModel)
        }
        // 회원가입 3단계: 프로필 세팅 form
        composable("signUpThirdForm") {
            SignUpThirdFormScreen(navController, signUpViewModel, phoneNumberAuthViewModel)
        }
        // 회원가입 4단계: 성공 페이지
        composable("login-success-page") {
            SignUpSuccessScreen(navController)
        }
        // 카테고리 선택 화면
        composable("categorySelect") {
            CategorySelectScreen(
                navController = navController,
                viewModel = categorySelectionViewModel
            )
        }
    }
}