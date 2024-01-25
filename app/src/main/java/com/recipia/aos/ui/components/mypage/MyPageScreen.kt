package com.recipia.aos.ui.components.mypage

import TokenManager
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.components.mypage.detail.FollowStatsSection
import com.recipia.aos.ui.components.mypage.detail.MyPageFeatureItem
import com.recipia.aos.ui.components.mypage.detail.PersonalInfoSection
import com.recipia.aos.ui.components.mypage.detail.ProfileSection
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.mypage.follow.FollowViewModel

/**
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel,
    followViewModel: FollowViewModel,
    tokenManager: TokenManager,
    targetMemberId: Long? = null
) {
    val myPageData by myPageViewModel.myPageData.observeAsState()

    // 색상 정의
    val context = LocalContext.current // 현재 컨텍스트를 가져옴
    var menuExpanded by remember { mutableStateOf(false) } // 드롭다운 메뉴 상태
    val targetId = targetMemberId ?: tokenManager.loadMemberId() // memberId 결정

    // 화면이 렌더링될 때 데이터 로딩 시작
    LaunchedEffect(key1 = targetId) { // memberId를 기반으로 데이터 로딩
        myPageViewModel.loadMyPageData(targetId)
    }
    // 스낵바 설정
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                title = { Text(text = "", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        // 드롭다운 메뉴 아이템들
                        DropdownMenuItem(
                            text = { Text("수정") },
                            onClick = { /* 수정 처리 */ }
                        )
                        DropdownMenuItem(
                            text = { Text("설정") },
                            onClick = { /* 설정 처리 */ }
                        )
                        DropdownMenuItem(
                            text = { Text("피드백 보내기") },
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
        bottomBar = { BottomNavigationBar(navController, snackbarHostState) }
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
                    FollowStatsSection(myPageViewModel, navController)
                    Spacer(modifier = Modifier.height(8.dp)) // 여기에 추가 공간
                }

                // 마이페이지 기능 리스트
                item {
                    MyPageFeatureItem(
                        title = "내가 북마크한 레시피",
                        icon = Icons.Default.Bookmark,
                        onClick = { /* 페이지 이동 로직 */ }
                    )
                }
                item {
                    MyPageFeatureItem(
                        title = "내가 좋아요한 레시피",
                        icon = Icons.Default.Favorite,
                        onClick = { /* 페이지 이동 로직 */ }
                    )
                }
                item {
                    MyPageFeatureItem(
                        title = "작성한 댓글/대댓글 보기",
                        icon = Icons.Default.Comment,
                        onClick = { /* 페이지 이동 로직 */ }
                    )
                }
                item {
                    MyPageFeatureItem(
                        title = "문의하기",
                        icon = Icons.Default.QuestionAnswer,
                        onClick = {
                            Toast.makeText(context, "준비중인 서비스입니다.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                item {
                    MyPageFeatureItem(
                        title = "계정 정보 수정",
                        icon = Icons.Default.ManageAccounts,
                        onClick = { /* 페이지 이동 로직 */ }
                    )
                }
                item {
                    MyPageFeatureItem(
                        title = "로그아웃",
                        icon = Icons.Default.ExitToApp,
                        onClick = {
                            // todo: 다이얼로그 띄우기
                            myPageViewModel.logout(
                                onSuccess = {
                                    // 성공시 로그인 화면으로 이동
                                    navController.navigate("login")
                                },
                                onError = { errorMessage ->
                                    // 실패시 에러 메시지 표시
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                        .show()
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
                            // todo: 다이얼로그 띄우기
                            myPageViewModel.deactivateAccount(
                                onSuccess = {
                                    // 성공시 로그인 화면으로 이동
                                    navController.navigate("login")
                                },
                                onError = { errorMessage ->
                                    // 실패시 에러 메시지 표시
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

