package com.recipia.aos.ui.components.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.model.mypage.MyPageViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel
) {

    val myPageData by myPageViewModel.myPageData.observeAsState()

    // 인스타그램 스타일 색상 정의
    val backgroundColor = Color.White
    val textColor = Color.Black
    val iconColor = Color.Gray

    var menuExpanded by remember { mutableStateOf(false) } // 드롭다운 메뉴 상태

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기", tint = iconColor)
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
                    containerColor = backgroundColor,
                    titleContentColor = textColor
                ),
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
//                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                ),
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->

        myPageData?.let { data ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color.White) // 여기에 배경색을 하얀색으로 설정
                    .padding(horizontal = 24.dp) // 좌우 패딩 추가
            ) {
                // 프로필, 팔로워, 팔로잉 영역
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White) // 여기에 배경색을 하얀색으로 설정
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 프로필 이미지
                    Image(
                        painter = rememberAsyncImagePainter(model = data.profileImageUrl),
                        contentDescription = "프로필 이미지",
                        modifier = Modifier
                            .size(100.dp) // 이미지 크기
                            .clip(CircleShape) // 원형 클리핑
                            .border(0.5.dp, Color.Gray, CircleShape) // 회색 테두리 추가
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // 닉네임, 팔로워, 팔로잉
                    Column {
                        Text(
                            text = data.nickname,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = data.introduction,
                            color = textColor
                        )
                        Row {
                            Text(
                                text = "팔로워 ${data.followerCount}",
                                color = textColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "팔로잉 ${data.followingCount}",
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 가짜 데이터
                val fakeData = List(30) { "https://via.placeholder.com/150" }

                // 이미지 그리드
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 3개의 열
                    contentPadding = PaddingValues(4.dp)
                ) {
                    items(fakeData.size) { index ->
                        Image(
                            painter = rememberAsyncImagePainter(model = fakeData[index]),
                            contentDescription = "이미지 $index",
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    }
}
