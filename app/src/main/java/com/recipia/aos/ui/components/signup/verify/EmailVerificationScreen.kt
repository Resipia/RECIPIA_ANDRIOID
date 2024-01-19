package com.recipia.aos.ui.components.signup.verify

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.recipia.aos.ui.model.signup.EmailVerificationViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationScreen(
    viewModel: EmailVerificationViewModel,
    navController: NavController
) {

    var timeLeft by remember { mutableIntStateOf(300) } // 5분 = 300초
    var verificationCode by remember { mutableStateOf("") }
    var isTimerRunning by remember { mutableStateOf(false) } // 타이머가 동작 중인지 여부를 나타내는 변수
    var isVerificationCompleted by remember { mutableStateOf(false) } // 인증이 완료되었는지 여부를 나타내는 변수
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(text = "이메일 인증", style = MaterialTheme.typography.bodyMedium) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 이메일 영역과 "인증번호 받기" 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("이메일") },
                modifier = Modifier.weight(1f),
                readOnly = true // 이전 화면에서 입력된 이메일 주소는 수정할 수 없도록 설정
            )

            // 인증번호 받기 버튼 클릭 이벤트 핸들링
            Button(
                onClick = {
                    // "인증하기" 버튼을 클릭하면 타이머 시작
                    isTimerRunning = true
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp) // 오른쪽 여백 추가
            ) {
                Text("인증번호 받기")
            }
        }

        // 인증번호 입력 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("인증번호") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    // 인증번호 확인 로직
                    if (verificationCodeIsValid(verificationCode)) {
                        // 인증 성공
                        viewModel.isEmailVerified = true
                        isVerificationCompleted = true
                    } else {
                        // 인증 실패, 사용자에게 알림
                        Toast.makeText(context, "인증번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp) // 오른쪽 여백 추가
            ) {
                Text("인증하기")
            }
        }

        // "인증하기" 버튼 클릭 후에만 인증 타이머를 표시
        if (isTimerRunning) {
            val timerText = "남은 시간: ${timeLeft / 60}:${timeLeft % 60}"
            Text(
                text = timerText,
                color = Color.Red,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(13.dp)
            )

            LaunchedEffect(key1 = timeLeft) {
                while (timeLeft > 0) {
                    delay(1000)
                    timeLeft--
                }
            }
        }
    }
}



// 인증번호 확인 로직 예시
private fun verificationCodeIsValid(verificationCode: String): Boolean {
    // 여기에 실제 인증번호 확인 로직을 구현하세요.
    // 인증 성공 시 true를 반환하고, 실패 시 false를 반환하세요.
    return true // 예시: 항상 true 반환 (실제 로직에 맞게 수정 필요)
}

@Composable
@Preview
fun EmailVerificationScreenPreview() {
    val viewModel = EmailVerificationViewModel() // 이 부분은 필요에 따라 뷰모델을 초기화해야 합니다.
    val navController = rememberNavController() // 이 부분은 필요에 따라 NavController를 초기화해야 합니다.

    EmailVerificationScreen(viewModel, navController)
}
