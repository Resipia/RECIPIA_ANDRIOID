package com.recipia.aos.ui.components.mypage

import TokenManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.components.recipe.detail.FeatureListItem
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
    val backgroundColor = Color.White
    val textColor = Color.Black
    val context = LocalContext.current // 현재 컨텍스트를 가져옴

    var menuExpanded by remember { mutableStateOf(false) } // 드롭다운 메뉴 상태

    // 팔로우 버튼 상태 관리
    var followButtonText by remember { mutableStateOf("팔로우") }
    var followButtonColor by remember { mutableStateOf(Color(149, 117, 205)) }

    val targetId = targetMemberId ?: tokenManager.loadMemberId() // memberId 결정

    // 화면이 렌더링될 때 데이터 로딩 시작
    LaunchedEffect(key1 = targetId) { // memberId를 기반으로 데이터 로딩
        myPageViewModel.loadMyPageData(targetId)
    }

    // 데이터가 로드되면 팔로우 버튼 상태 업데이트
    myPageData?.let { data ->
        if (data.me) {
            // 내 마이페이지인 경우 버튼 숨김 처리
            followButtonText = ""
        } else {
            // 다른 사용자의 마이페이지인 경우
            if (data.followId != null) {
                // 이미 팔로우한 경우
                followButtonText = "팔로잉"
                followButtonColor = Color(206, 212, 218)
            } else {
                // 아직 팔로우하지 않은 경우
                followButtonText = "팔로우"
                followButtonColor = Color(149, 117, 205)
            }
        }
    }

    Scaffold(
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
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        myPageData?.let { data ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(backgroundColor)
                    .padding(horizontal = 24.dp)
            ) {
                // 프로필 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 프로필 이미지
                    Image(
                        painter = rememberAsyncImagePainter(model = data.profileImageUrl),
                        contentDescription = "프로필 이미지",
                        modifier = Modifier
                            .size(100.dp) // 이미지 크기를 100dp로 설정
                            .clip(CircleShape) // 원형으로 이미지를 클리핑
                            .border(0.5.dp, Color.Gray, CircleShape) // 회색 테두리 추가
                    )

                    Spacer(modifier = Modifier.width(25.dp))

                    // 닉네임, 한 줄 소개
                    Column {
                        Text(
                            text = data.nickname,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp)) // 닉네임과 한 줄 소개 사이 공간
                        data.introduction?.let {
                            Text(
                                text = it,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp)) // 여기에 추가 공간

                // 팔로우, 공유하기 버튼 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // isMe가 true이면 버튼을 렌더링하지 않음
                    if (!data.me) {
                        Button(
                            onClick = {
                                // 팔로우/언팔로우 처리
                                followViewModel.followOrUnfollow(
                                    targetMemberId = myPageData?.memberId ?: 0,
                                    onResult = { success, newFollowId ->
                                        if (success) {
                                            if (newFollowId != null) {
                                                myPageData?.followId = newFollowId
                                            } // 팔로우 ID 업데이트
                                            myPageViewModel.loadMyPageData(targetId) // 마이페이지 데이터 재로딩
                                        } else {
                                            Toast.makeText(context, "작업 실패", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = followButtonColor),
                            shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "팔로우",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(followButtonText, fontSize = 16.sp, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp)) // 버튼 사이 간격

                    // 공유하기 버튼
                    Button(
                        onClick = { /* 공유하기 처리 */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(233, 236, 239)),
                        shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp)) // 버튼 모양 변경
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "공유하기",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
                        Text("공유하기", fontSize = 16.sp, color = Color.Black) // 텍스트 색상 변경
                    }
                }

                // 생년월일, 성별 영역
                Column(
                    modifier = Modifier
                        .padding(start = 3.dp, top = 8.dp, bottom = 8.dp) // 시작 부분 왼쪽에서 더 멀리 떨어뜨림
                ) {
                    Text(text = "생년월일: ${data.birth}", color = textColor)
                    Spacer(modifier = Modifier.height(4.dp)) // 여기에 추가 공간
                    Text(text = "성별: ${data.gender}", color = textColor)
                }

                Spacer(modifier = Modifier.height(8.dp)) // 여기에 추가 공간

                // 팔로잉, 팔로워, 레시피, 위글위글 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(233, 236, 239),
                            shape = RoundedCornerShape(8.dp) // 모서리 둥글게
                        )
                        .padding(vertical = 16.dp) // 여기에 패딩을 추가
                ) {
                    // 팔로잉 영역
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                // 팔로잉 리스트 화면으로 이동
                                navController.navigate("followList/following/${data.memberId}")
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "팔로잉", color = textColor)
                        Text(text = "${data.followingCount}", color = textColor)
                        Spacer(modifier = Modifier.height(8.dp)) // 간격 추가
                    }

                    // 팔로워 영역
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                // 팔로잉 리스트 화면으로 이동
                                navController.navigate("followList/follower/${data.memberId}")
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "팔로워", color = textColor)
                        Text(text = "${data.followerCount}", color = textColor)
                        Spacer(modifier = Modifier.height(8.dp)) // 간격 추가
                    }

                    // 레시피 영역
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* 레시피 페이지 이동 로직 */ },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "레시피", color = textColor)
                        Text(text = "10", color = textColor) // todo: 레시피 개수가 없음
                        Spacer(modifier = Modifier.height(8.dp)) // 간격 추가
                    }

                    // 위글위글 영역
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { /* 위글위글 페이지 이동 로직 */ },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "위글위글", color = textColor)
                        Text(text = "0", color = textColor) // 고정된 숫자
                        Spacer(modifier = Modifier.height(8.dp)) // 간격 추가
                    }
                }

                Spacer(modifier = Modifier.height(8.dp)) // 여기에 추가 공간

                // 기능 리스트
                LazyColumn {
                    item {
                        FeatureListItem(
                            title = "내가 북마크한 레시피",
                            icon = Icons.Default.Bookmark,
                            onClick = { /* 페이지 이동 로직 */ }
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color.Gray // 구분선의 색상 설정
                        )
                    }
                    item {
                        FeatureListItem(
                            title = "내가 좋아요한 레시피",
                            icon = Icons.Default.Favorite, // 예시 아이콘, 필요에 따라 변경 가능
                            onClick = { /* 페이지 이동 로직 */ }
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color.Gray // 구분선의 색상 설정
                        )
                    }
                    item {
                        FeatureListItem(
                            title = "작성한 댓글/대댓글 보기",
                            icon = Icons.Default.Comment, // 예시 아이콘, 필요에 따라 변경 가능
                            onClick = { /* 페이지 이동 로직 */ }
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color.Gray // 구분선의 색상 설정
                        )
                    }
                    item {
                        FeatureListItem(
                            title = "문의하기",
                            icon = Icons.Default.QuestionAnswer, // 예시 아이콘, 필요에 따라 변경 가능
                            onClick = {
                                Toast.makeText(context, "준비중인 서비스입니다.", Toast.LENGTH_SHORT).show()
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color.Gray // 구분선의 색상 설정
                        )
                    }
                    item {
                        FeatureListItem(
                            title = "계정 정보 수정",
                            icon = Icons.Default.ManageAccounts, // 예시 아이콘, 필요에 따라 변경 가능
                            onClick = { /* 페이지 이동 로직 */ }
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color.Gray // 구분선의 색상 설정
                        )
                    }
                    item {
                        FeatureListItem(
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
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 1.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color.Gray // 구분선의 색상 설정
                        )
                    }
                    item {
                        FeatureListItem(
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
}

