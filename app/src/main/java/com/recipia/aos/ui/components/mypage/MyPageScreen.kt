package com.recipia.aos.ui.components.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.recipia.aos.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    navController: NavController
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // 요소들을 가운데 정렬
        ) {
            Text(
                "회원정보",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // 회원 프로필 사진
            Image(
                painter = painterResource(id = R.drawable.profile_placeholder),
                contentDescription = "회원 프로필",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .padding(bottom = 8.dp)
            )
            Text("회원 이름", fontSize = 20.sp, fontWeight = FontWeight.Medium)
            Text("한 줄 소개", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // 메뉴 아이템들
            MenuItem("1. 내 정보 관리")
            MenuItem("2. 작성한 레시피 목록")
            MenuItem("3. 고객 센터")
            MenuItem("4. Q&A")

            Spacer(modifier = Modifier.height(16.dp))

            // 로그아웃 버튼
            Button(
                onClick = { /* 로그아웃 로직 */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("로그아웃")
            }
        }
    }
}

@Composable
fun MenuItem(text: String) {
    Text(
        text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.Light
    )
}

// 기존 MyPageScreen 컴포저블 아래에 이 미리보기 컴포저블을 추가
@Preview(showBackground = true)
@Composable
fun MyPageScreenPreview() {
    val navController = rememberNavController() // 네비게이션 컨트롤러를 위한 임시 객체
    MyPageScreen(navController = navController) // MyPageScreen 호출
}
