package com.recipia.aos.ui.components.forgot.email

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.common.AnimatedPreloader
import com.recipia.aos.ui.model.forgot.ForgotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailVerificationForgotIdScreen(
    navController: NavController,
    viewModel: ForgotViewModel
) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var foundEmail by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isReadOnly by remember { mutableStateOf(false) } // 읽기 전용 상태 추가

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showNextButton by remember { mutableStateOf(true) }
    val isButtonEnabled = name.isNotEmpty() && phoneNumber.isNotEmpty()

    // 모든 상태를 초기화하는 함수
    fun resetAllStates() {
        name = ""
        phoneNumber = ""
        foundEmail = null
        isLoading = false
        isReadOnly = false
        errorMessage = null
        showNextButton = true
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White),
                title = { Text(text = "이메일 찾기", style = MaterialTheme.typography.bodyMedium) },
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
                .padding(innerPadding) // Scaffold로부터 제공된 패딩 적용
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "이메일 찾기",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "* 가입 시 등록한 이름, 전화번호를 입력해주세요.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // 이름
            OutlinedTextField(
                value = name,
                onValueChange = { if (!isReadOnly) name = it },
                readOnly = isReadOnly, // 읽기 전용 설정 적용
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("이름") }
            )

            // 전화번호
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { input ->
                    if (!isReadOnly) { // 읽기 전용 상태가 아닐 때만 입력 변경 허용
                        val filteredInput = input.filter { it.isDigit() }
                        phoneNumber = filteredInput
                    }
                },
                readOnly = isReadOnly, // 읽기 전용 설정 적용
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                label = { Text("전화번호") },
                placeholder = { Text("01012345678", style = TextStyle(color = Color.Gray)) },
            )

            // 이메일 찾기 버튼
            if (showNextButton) {
                Button(
                    onClick = {
                        isLoading = true
                        viewModel.findEmail(name, phoneNumber, { response ->
                            isLoading = false
                            foundEmail = response?.result // 응답에서 이메일 추출
                            showNextButton = false // 버튼 상태 변경
                            isReadOnly = true // 입력 필드를 읽기 전용으로 설정
                        }, { error ->
                            isLoading = false
                            errorMessage = error // 오류 메시지 설정
                            showNextButton = false // 버튼 상태 변경
                            isReadOnly = true // 입력 필드를 읽기 전용으로 설정
                        })
                    },
                    enabled = isButtonEnabled, // 버튼 활성화 조건
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isButtonEnabled) Color(206, 212, 218) else Color.Gray, // 활성화 여부에 따른 배경색
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    if (isLoading) {
                        AnimatedPreloader(modifier = Modifier.size(100.dp))
                    } else {
                        Text(text = "이메일 찾기")
                    }
                }

                // 필드를 모두 채워야 함을 나타내는 안내 메시지
                if (!isButtonEnabled) {
                    Text(
                        text = "모든 필드를 채운 후 버튼이 활성화됩니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 오류 메시지 또는 찾은 이메일 정보 표시
            if (!showNextButton) {
                // 찾은 이메일 정보 또는 오류 메시지를 표시하는 박스
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(16.dp) // 박스 내부의 여백을 추가
                ) {
                    if (foundEmail != null) {
                        Text(
                            text = "찾은 이메일: $foundEmail",
                            modifier = Modifier.padding(4.dp) // 텍스트에도 여백 추가
                        )
                    } else if (!errorMessage.isNullOrEmpty()) {
                        Text(
                            text = errorMessage!!,
                            color = Color.Red,
                            modifier = Modifier.padding(4.dp) // 오류 메시지에도 여백 추가
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!errorMessage.isNullOrEmpty()) {
                    Button(
                        onClick = { resetAllStates() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(206, 212, 218),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Text("이메일 다시 찾기")
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 비밀번호 찾기 버튼
                        Button(
                            onClick = {
                                // ViewModel에 찾은 이메일 저장
                                viewModel.saveFoundEmail(foundEmail)
                                navController.navigate("findPassword")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(206, 212, 218), // 버튼 배경색
                                contentColor = Color.Black // 버튼 내부 글자색
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .weight(1f) // 여기서 weight를 1로 설정
                                .padding(end = 2.dp) // 오른쪽에 간격 추가
                        ) {
                            Text("임시 비밀번호 발급받기")
                        }

                        // 버튼 사이의 간격
                        Spacer(modifier = Modifier.width(8.dp))

                        // 로그인 버튼
                        Button(
                            onClick = { navController.navigate("login") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(206, 212, 218), // 버튼 배경색
                                contentColor = Color.Black // 버튼 내부 글자색
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .weight(1f) // 여기서 weight를 1로 설정
                                .padding(start = 2.dp) // 왼쪽에 간격 추가
                        ) {
                            Text("로그인")
                        }
                    }
                }
            }
        }
    }
}
