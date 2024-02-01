package com.recipia.aos.ui.components.forgot.password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.forgot.ForgotViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetScreen(
    navController: NavController,
    viewModel: ForgotViewModel
) {
    var name by remember { mutableStateOf("") }
    var telNo by remember { mutableStateOf("") }
    val foundEmail by viewModel.foundEmail.collectAsState()
    var email by remember { mutableStateOf(foundEmail ?: "") }
    var isPasswordSent by remember { mutableStateOf(false) }
    var isEmailReadOnly by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 이름, 전화번호, 이메일 필드 모두에 입력이 있는 경우 버튼을 활성화하기 위한 조건
    val isButtonEnabled = name.isNotEmpty() && telNo.isNotEmpty() && email.isNotEmpty()

    // 상태 초기화 함수
    fun resetAllStates() {
        name = ""
        telNo = ""
        email = foundEmail ?: ""
        isPasswordSent = false
        isEmailReadOnly = false
        errorMessage = null
    }

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
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(Color.White),
                    title = {
                        Text(
                            text = "비밀번호 찾기",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                // 찾은 이메일 초기화
                                viewModel.resetEmail()
                                navController.popBackStack()
                            }
                        ) {
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
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "가입 시 등록한 이름, 전화번호, 이메일을 모두 입력해주세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 2.dp, bottom = 16.dp),
                    fontWeight = FontWeight.Bold
                )

                // 이름
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text("이름") }
                )

                // 전화번호
                OutlinedTextField(
                    value = telNo,
                    onValueChange = { input ->
                        val filteredInput = input.filter { it.isDigit() }
                        telNo = filteredInput
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text("전화번호") },
                    placeholder = { Text("01012345678", style = TextStyle(color = Color.Gray)) },
                )

                // 이메일
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    label = { Text("이메일 주소") }
                )

                // 비밀번호 발급하기 버튼
                if (!isPasswordSent && errorMessage == null) {
                    Button(
                        onClick = {
                            viewModel.sendTempPassword(name, telNo, email, { isSuccess ->
                                if (isSuccess) {
                                    isPasswordSent = true
                                    // 찾은 이메일 초기화
                                    viewModel.resetEmail()
                                }
                            }, { error ->
                                errorMessage = error
                            })
                        },
                        enabled = isButtonEnabled, // 버튼 활성화 조건
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isButtonEnabled) Color(
                                27,
                                94,
                                32
                            ) else Color.LightGray,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "임시 비밀번호 발급",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
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
                        Text("비밀번호 찾기 재시도")
                    }
                    // 비밀번호 찾기에 성공하면 성공 페이지로 이동
                } else if (isPasswordSent) {
                    navController.navigate("passwordFindSuccess")
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
                            .clickable {
                                navController.navigate("emailVerificationScreen")
                            }
                    )
                }

            }
        }
    }
}
