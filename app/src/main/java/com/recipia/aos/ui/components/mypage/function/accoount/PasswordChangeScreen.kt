package com.recipia.aos.ui.components.mypage.function.accoount

import TokenManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * 비밀번호 수정 컴포저
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PasswordChangeScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatchError by remember { mutableStateOf("") }
    val passwordRegex =
        Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,20}$")
    var passwordValid by remember { mutableStateOf(false) }
    var passwordErrorMessage by remember { mutableStateOf("") }
    val enableButton =
        newPassword.isNotEmpty() && currentPassword.isNotEmpty() && passwordValid && newPassword == confirmPassword && confirmPassword.isNotEmpty()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordChangeSuccess by myPageViewModel.passwordChangeSuccess.observeAsState()
    val passwordChangeError by myPageViewModel.passwordChangeError.observeAsState()
    val navigateToLogin by myPageViewModel.navigateToLogin.observeAsState(initial = false)

    // navigateToLogin 상태가 변경되었을 때 로그인 화면으로 이동
    if (navigateToLogin) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate("login")
            myPageViewModel.resetNavigateToLogin() // 로그인 화면으로 이동 후 `_navigateToLogin`을 리셋하는 함수 호출
        }
    }

    // 성공하거나 뒤로 가기 버튼을 눌렀을 때 필드를 초기화하는 함수
    fun resetFields() {
        currentPassword = ""
        newPassword = ""
        confirmPassword = ""
    }

    // 비밀번호 변경 성공 시 로직
    if (passwordChangeSuccess == true) {
        LaunchedEffect(Unit) {
            resetFields()
            navController.navigate("password-change-success")
            myPageViewModel.passwordChangeSuccess.value = null // 상태 초기화
        }
    }

    // 비밀번호 변경 오류 시 스낵바 표시
    passwordChangeError?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            snackbarHostState.showSnackbar(errorMessage)
            myPageViewModel.passwordChangeError.value = null // 상태 초기화
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                })
            }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "비밀번호 변경",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            resetFields() // 뒤로 가기 버튼을 누르면 필드 초기화
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로 가기"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    // 기존 비밀번호 입력 필드
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("기존 비밀번호") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { keyboardController?.hide() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    // 새 비밀번호 입력 필드
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            // 비밀번호 유효성 검사
                            passwordValid = passwordRegex.matches(newPassword)
                            passwordErrorMessage = if (passwordValid) "" else "잘못된 형식입니다."
                            confirmPassword = "" // 새 비밀번호 변경 시, 비밀번호 확인 필드 초기화
                        },
                        label = { Text("새 비밀번호") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { keyboardController?.hide() }
                        ),
                        isError = !passwordValid && newPassword.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 비밀번호 유효성 검사 오류 메시지 표시
                    if (!passwordValid && newPassword.isNotEmpty()) {
                        Text(
                            text = passwordErrorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                item {
                    val text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("* 영문 대,소문자, 숫자, 특수문자 포함 8-20자")
                        }
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 9.dp, bottom = 2.dp)
                    )
                }

                item {
                    // 새 비밀번호 확인 입력 필드
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            passwordMatchError = if (newPassword != it) "비밀번호가 일치하지 않습니다." else ""
                        },
                        label = { Text("비밀번호 확인") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordMatchError.isNotEmpty()
                    )
                }

                // 비밀번호 일치 여부 메시지
                item {
                    // 비밀번호가 일치하지 않으면 에러 메시지 출력
                    if (passwordMatchError.isNotEmpty()) {
                        Text(
                            text = passwordMatchError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                    // 비밀번호가 일치하고, confirmPassword가 비어있지 않을 경우에만 메시지 출력
                    else if (newPassword == confirmPassword && confirmPassword.isNotEmpty()) {
                        Text(
                            text = "비밀번호가 일치합니다.",
                            color = Color(27, 94, 32),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                item {
                    // 변경하기 버튼
                    Button(
                        onClick = {
                            myPageViewModel.changePassword(currentPassword, newPassword)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(top = 4.dp),
                        enabled = enableButton,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(27, 94, 32)),
                    ) {
                        androidx.compose.material.Text(
                            text = "비밀번호 변경",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}