package com.recipia.aos.ui.components.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.login.LoginViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") } // 로그인 오류 메시지 상태
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // 키보드 컨트롤러 (터치시 키보드 닫히게 하기)
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // 이벤트를 감지하여 키보드를 숨깁니다.
                detectTapGestures(
                    onPress = { /* 터치 감지 시 수행할 동작 */ },
                    onTap = { keyboardController?.hide() }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 로그인 타이틀
            Text(
                text = "Recipia",
                style = MaterialTheme.typography.h4,
                color = Color(27, 94, 32)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 이메일 입력 필드
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                    if (loginError.isNotEmpty()) loginError = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("사용자 이메일") },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )

            emailError?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 패스워드 입력 필드
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    if (loginError.isNotEmpty()) loginError = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("비밀번호") },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = Color.Gray
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                )
            )

            passwordError?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 오류 메시지
            if (loginError.isNotEmpty()) {
//            Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = loginError,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.Start) // 왼쪽 정렬
                        .padding(top = 8.dp, bottom = 8.dp) // 상하 패딩 추가
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 로그인 버튼
            Button(
                onClick = {
                    emailError = null
                    passwordError = null
                    when {
                        email.isBlank() && password.isBlank() -> {
                            emailError = "이메일 입력은 필수입니다."
                            passwordError = "비밀번호 입력은 필수입니다."
                        }

                        email.isBlank() -> emailError = "이메일 입력은 필수입니다."
                        password.isBlank() -> passwordError = "비밀번호 입력은 필수입니다."
                        else -> viewModel.login(
                            email = email,
                            password = password,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true } // 로그인 화면을 백스택에서 제거
                                }
                            },
                            onLoginFailure = { error ->
                                loginError = if (error == "403") "존재하지 않는 계정입니다." else error
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(67, 160, 71))
            ) {
                Text(
                    text = "로그인",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 이메일 찾기 텍스트
                Text(
                    text = "이메일 찾기",
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("findId")
                    }
                )
                Text(
                    text = " | ",
                    fontSize = 12.sp
                )

                // 비밀번호 찾기 텍스트
                Text(
                    text = "비밀번호 찾기",
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("findPassword")
                    }
                )
                Text(
                    text = " | ",
                    fontSize = 12.sp
                )

                // 회원가입 텍스트
                Text(
                    text = "회원가입",
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        navController.navigate("signUpFirstForm")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
