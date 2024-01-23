package com.recipia.aos.ui.components.forgot.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.forgot.ForgotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(
    navController: NavController,
    viewModel: ForgotViewModel
) {
    val foundEmail by viewModel.foundEmail.collectAsState()
    var email by remember { mutableStateOf(foundEmail ?: "") }
    var isPasswordSent by remember { mutableStateOf(false) }
    var isEmailReadOnly by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // 상태 초기화 함수
    fun resetAllStates() {
        email = foundEmail ?: ""
        isPasswordSent = false
        isEmailReadOnly = false
        errorMessage = null
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White),
                title = { Text(text = "임시 비밀번호 발급", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "임시 비밀번호를 발급받을 이메일 정보를 입력해 주세요",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { if (!isEmailReadOnly) email = it },
                readOnly = isEmailReadOnly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("이메일 주소") }
            )

            // 비밀번호 찾기
            if (!isPasswordSent && errorMessage == null) {
                Button(
                    onClick = {
                        viewModel.sendTempPassword(email, { isSuccess ->
                            if (isSuccess) {
                                isPasswordSent = true
                            }
                        }, { error ->
                            errorMessage = error
                        })
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(206, 212, 218), // 버튼 배경색
                        contentColor = Color.Black // 버튼 내부 글자색
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("발급하기")
                }
            } else if (errorMessage != null) {
                // 오류 메시지 및 다시 시도 버튼
                Text(
                    text = errorMessage!!,
                    color = Color.Red, // 실패 메시지는 빨간색으로 표시
                    style = MaterialTheme.typography.bodyMedium
                )

                isEmailReadOnly = true // 오류가 있으면 읽기 전용으로 설정
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        resetAllStates()
                        isEmailReadOnly = false // 상태 초기화 시 읽기 전용 해제
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(206, 212, 218), // 버튼 배경색
                        contentColor = Color.Black // 버튼 내부 글자색
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("발급 재시도")
                }
            } else if (isPasswordSent) {
                Text(
                    text = "임시 비밀번호가 이메일로 전송되었습니다.",
                    color = Color(0xFF006633), // 성공 메시지는 초록색으로 표시
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                isEmailReadOnly = true // 이메일 입력창을 읽기 전용으로 설정
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(206, 212, 218), // 버튼 배경색
                        contentColor = Color.Black // 버튼 내부 글자색
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("로그인 하기")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically // 수직 방향으로 중앙 정렬
            ) {
                Text(
                    text = "이메일이 기억나지 않는다면?",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.width(8.dp)) // 두 텍스트 사이 간격 추가

                Text(
                    text = "이메일 찾기",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { navController.navigate("findId") }
                )
            }

        }
    }
}
