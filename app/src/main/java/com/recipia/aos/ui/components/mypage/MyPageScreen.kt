package com.recipia.aos.ui.components.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.recipia.aos.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마이페이지", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            MenuItem(navController, "내 정보 관리", "info")
            MenuItem(navController, "작성한 레시피 목록", "recipes")
            MenuItem(navController, "고객 센터", "support")
            MenuItem(navController, "Q&A", "faq")

            Spacer(modifier = Modifier.height(16.dp))

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
fun MenuItem(navController: NavController, text: String, route: String) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


// 기존 MyPageScreen 컴포저블 아래에 이 미리보기 컴포저블을 추가
@Preview(showBackground = true)
@Composable
fun MyPageScreenPreview() {
    val navController = rememberNavController() // 네비게이션 컨트롤러를 위한 임시 객체
    MyPageScreen(navController = navController) // MyPageScreen 호출
}
