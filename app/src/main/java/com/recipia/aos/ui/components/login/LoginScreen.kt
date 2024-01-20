package com.recipia.aos.ui.components.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import com.recipia.aos.ui.model.login.LoginViewModel
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") } // 로그인 오류 메시지 상태

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
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 이메일 입력 필드
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("사용자 이메일") },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = Color.Gray
            )
        )

        // 패스워드 입력 필드
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
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

        Spacer(modifier = Modifier.height(16.dp))

        // 로그인 버튼
        Button(
            onClick = {
                viewModel.login(
                    email = email,
                    password = password,
                    onLoginSuccess = {
                        navController.navigate("home")
                    }, // todo 애초에 여기서부터 안탐
                    onLoginFailure = { error -> loginError = error }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colors.primary)
        ) {
            Text(text = "로그인", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 오류 메시지
        if (loginError.isNotEmpty()) {
            Text(text = loginError, color = Color.Red)
        }

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
                    navController.navigate("signUpAgree")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
