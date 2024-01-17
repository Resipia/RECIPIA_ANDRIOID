package com.recipia.aos.ui.components.signup

import SignUpViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.recipia.aos.ui.model.factory.MyViewModelFactory

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: SignUpViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") } // 로그인 오류 메시지 상태

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "로그인",
            style = MaterialTheme.typography.h4
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("사용자 이메일") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            label = { Text("비밀번호") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
//                onDone = {
//                    // 로그인 버튼을 눌렀을 때의 동작을 여기에 추가
//                    viewModel.login(username, password, onLoginSuccess = {
//                        navController.navigate("home") // 로그인 성공 시 home 화면으로 이동
//                    }, onLoginFailure = {
//                        loginError = "잘못된 입력정보입니다." // 로그인 실패 시 오류 메시지 설정
//                    })
//                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loginError.isNotEmpty()) {
            Text(
                text = loginError,
                color = Color.Red, // 오류 메시지는 빨간색으로 표시
                // 기타 Text 스타일 설정
            )
        }

        Button(
            onClick = {
                viewModel.login(email, password, onLoginSuccess = {
                    navController.navigate("home") // 로그인 성공 시 home 화면으로 이동
                }, onLoginFailure = {
                    loginError = "잘못된 입력정보입니다." // 로그인 실패 시 오류 메시지 설정
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "로그인")
        }
    }
}
