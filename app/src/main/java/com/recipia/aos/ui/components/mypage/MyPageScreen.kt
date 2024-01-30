package com.recipia.aos.ui.components.mypage

import TokenManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.components.mypage.function.profile.follow.FollowStatsSection
import com.recipia.aos.ui.components.mypage.function.MyPageFeatureItem
import com.recipia.aos.ui.components.mypage.function.PersonalInfoSection
import com.recipia.aos.ui.components.mypage.function.profile.ProfileSection
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import kotlinx.coroutines.launch

/**
 * 마이페이지 컴포저
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel,
    recipeAllListViewModel: RecipeAllListViewModel,
    followViewModel: FollowViewModel,
    bookMarkViewModel: BookMarkViewModel,
    tokenManager: TokenManager,
    targetMemberId: Long? = null
) {
    val myPageData by myPageViewModel.myPageData.observeAsState()

    // 색상 정의
    var menuExpanded by remember { mutableStateOf(false) } // 드롭다운 메뉴 상태
    val targetId = targetMemberId ?: tokenManager.loadMemberId() // memberId 결정
    val lazyListState = rememberLazyListState() // LazyListState 인스턴스 생성
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope() // 코루틴 스코프 생성

    // targetMemberId가 존재하면 해당 멤버의 레시피를 가져오고, 그렇지 않으면 기본 마이페이지 기능을 표시
    LaunchedEffect(key1 = targetMemberId) {
        targetMemberId?.let {
            myPageViewModel.getHighRecipe(it)
        }
    }

    // 레시피 총 개수 가져오기 (한 번만 호출)
    LaunchedEffect(key1 = Unit) {
        myPageViewModel.getRecipeTotalCount(targetId)
    }

    // 화면이 렌더링될 때 데이터 로딩 시작
    LaunchedEffect(key1 = targetId) { // memberId를 기반으로 데이터 로딩
        myPageViewModel.loadMyPageData(targetId)
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                title = { Text(text = "", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "더보기"
                        )
                    }
                    CustomDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        // 드롭다운 메뉴 아이템들
                        if (myPageData?.memberId?.equals(tokenManager.loadMemberId()) == true) {
                            DropdownMenuItem(
                                text = { Text("프로필 수정", color = Color.Black) },
                                onClick = {
                                    // "profile-edit" 라우트로 이동
                                    navController.navigate("profile-edit")
                                }
                            )

                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 0.5.dp,
                                color = Color(222, 226, 230)
                            )
                        }

                        DropdownMenuItem(
                            text = { Text("설정", color = Color.Black) },
                            onClick = { /* 설정 처리 */ }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = Color(222, 226, 230)
                        )
                        DropdownMenuItem(
                            text = { Text("피드백 보내기", color = Color.Black) },
                            onClick = { /* 피드백 처리 */ }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = Color(222, 226, 230)
                        )
                        DropdownMenuItem(
                            text = { Text("회원 신고", color = Color.Black) },
                            onClick = { /* 피드백 처리 */ }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController,
                snackbarHostState,
                recipeAllListViewModel,
                lazyListState
            )
        }
    ) { innerPadding ->

        myPageData?.let { data ->

            // 무한 스크롤
            LazyColumn(modifier = Modifier.padding(innerPadding)) {

                // 프로필 이미지, 닉네임, 팔로우 버튼, 한줄소개
                item {
                    ProfileSection(myPageViewModel, followViewModel, targetId)
                }

                // 생년월일, 성별
                item {
                    PersonalInfoSection(myPageViewModel)
                    Spacer(modifier = Modifier.height(8.dp)) // 여기에 추가 공간
                }

                // 팔로잉, 팔로워, 레시피, 위글위글 영역
                item {
                    FollowStatsSection(myPageViewModel, navController, snackbarHostState)
                    Spacer(modifier = Modifier.height(8.dp)) // 여기에 추가 공간
                }

                if (targetMemberId != null) {

                    item {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // 닉네임과 아이콘 표시
                                Text(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    text = "${myPageData?.nickname} 님의 레시피 조회수 TOP 5",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // 항목 사이에 구분선 추가
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 16.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color(222, 226, 230) // 구분선의 색상 설정
                        )

                    }

                    // targetMemberId가 있을 경우 해당 멤버의 레시피 목록 표시
                    items(myPageViewModel.highCountRecipe.value) { recipe ->
                        MyPageRecipeListItem(
                            item = recipe,
                            bookmarkViewModel = bookMarkViewModel,
                            navController = navController
                        )
                    }
                } else {
                    // 마이페이지 기능 리스트
                    item {
                        MyPageFeatureItem(
                            title = "내가 북마크한 레시피",
                            icon = Icons.Default.Bookmark,
                            onClick = {
                                myPageViewModel.currentPageType.value =
                                    MyPageViewModel.PageType.BOOKMARK
                                navController.navigate("select-recipe-screen/${myPageData!!.memberId}")
                            }
                        )
                    }

                    item {
                        MyPageFeatureItem(
                            title = "내가 좋아요한 레시피",
                            icon = Icons.Default.Favorite,
                            onClick = {
                                myPageViewModel.currentPageType.value =
                                    MyPageViewModel.PageType.LIKE
                                navController.navigate("select-recipe-screen/${myPageData!!.memberId}")
                            }
                        )
                    }

                    item {
                        MyPageFeatureItem(
                            title = "작성한 댓글/대댓글 보기",
                            icon = Icons.Default.Comment,
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "준비중인 서비스입니다.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }

                    item {
                        MyPageFeatureItem(
                            title = "고객 문의/피드백",
                            icon = Icons.Default.QuestionAnswer,
                            onClick = {
                                // 문의하기 목록 페이지로 이동
                                navController.navigate("ask-list")
                            }
                        )
                    }

                    item {
                        MyPageFeatureItem(
                            title = "계정 정보 수정",
                            icon = Icons.Default.ManageAccounts,
                            onClick = {
                                navController.navigate("account-settings")
                            }
                        )
                    }

                    item {
                        MyPageFeatureItem(
                            title = "로그아웃",
                            icon = Icons.Default.ExitToApp,
                            onClick = {
                                myPageViewModel.logout(
                                    onSuccess = {
                                        // 성공시 로그인 화면으로 이동
                                        navController.navigate("login")
                                    },
                                    onError = {
                                        // 실패시 에러 메시지 표시
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "로그아웃 실패.",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    }

                    item {
                        MyPageFeatureItem(
                            title = "탈퇴",
                            icon = Icons.Default.Delete,
                            onClick = {
                                myPageViewModel.deactivateAccount(
                                    onSuccess = {
                                        // 성공시 로그인 화면으로 이동
                                        navController.navigate("login")
                                    },
                                    onError = {
                                        // 실패시 에러 메시지 표시
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "탈퇴 실패.",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

