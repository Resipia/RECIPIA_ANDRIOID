package com.recipia.aos.ui.components.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.signup.function.InputField
import com.recipia.aos.ui.model.signup.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailValidAndPasswordFormScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel
) {
    // ViewModel에서 각 입력 필드의 현재 값을 가져옴
    val currentName by signUpViewModel.name.collectAsState()
    val currentNickname by signUpViewModel.nickname.collectAsState()
    val currentEmail by signUpViewModel.email.collectAsState()
    val currentPassword by signUpViewModel.password.collectAsState()

    // 초기값 설정
    // remember를 사용하여 초기 값 설정
    var name by remember { mutableStateOf(currentName) }
    var nickname by remember { mutableStateOf(currentNickname) }
    var email by remember { mutableStateOf(currentEmail) }
    var password by remember { mutableStateOf(currentPassword) }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatchMessage by remember { mutableStateOf("") }
    var passwordMatchColor by remember { mutableStateOf(Color.Black) }

    // 입력 필드 검증 상태
    var nameError by remember { mutableStateOf("") }
    var nicknameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    val nameFocusRequester = remember { FocusRequester() }
    val nicknameFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    // ViewModel에서 중복 확인 결과를 관찰
    val emailDuplicateCheckResult by signUpViewModel.emailDuplicateCheckResult.observeAsState()

    // 입력 필드 검증
    fun validateFields() {
        // 이름 검증
        if (name.isBlank()) {
            nameError = "필수 입력값입니다."
            nameFocusRequester.requestFocus()
        } else {
            nameError = ""
        }

        // 닉네임 검증
        if (nickname.isBlank()) {
            nicknameError = "필수 입력값입니다."
            nicknameFocusRequester.requestFocus()
        } else {
            nicknameError = ""
        }

        // 이메일 검증
        if (email.isBlank()) {
            emailError = "필수 입력값입니다."
            emailFocusRequester.requestFocus()
        } else {
            emailError = ""
        }

        // 비밀번호 검증
        if (password.isBlank()) {
            passwordError = "필수 입력값입니다."
            passwordFocusRequester.requestFocus()
        } else {
            passwordError = ""
        }

        // 비밀번호 재확인 검증
        if (confirmPassword.isBlank()) {
            confirmPasswordError = "필수 입력값입니다."
            confirmPasswordFocusRequester.requestFocus()
        } else if (password != confirmPassword) {
            confirmPasswordError = "비밀번호가 일치하지 않습니다."
            confirmPasswordFocusRequester.requestFocus()
        } else {
            confirmPasswordError = ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "회원가입", style = MaterialTheme.typography.bodyMedium) },
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
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            item { Spacer(modifier = Modifier.height(10.dp)) }

            // 각 입력 필드 및 검증 로직...
            item {
                InputField(
                    label = "이름",
                    value = name,
                    onValueChange = { name = it },
                    focusRequester = nameFocusRequester,
                    errorMessage = nameError
                )
            }

            // "닉네임" 입력 필드 및 중복체크 버튼
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InputField(
                        label = "닉네임",
                        value = nickname,
                        onValueChange = { nickname = it },
                        focusRequester = nicknameFocusRequester,
                        errorMessage = nicknameError,
                        modifier = Modifier.weight(0.7f)
                    )

                    Button(
                        onClick = {
                            // ViewModel의 함수를 호출하여 중복 체크
                            signUpViewModel.checkDuplicateNickname(nickname)
                        },
                        modifier = Modifier
                            .weight(0.3f)
                            .height(70.dp)
                            .padding(top = 18.dp)
                    ) {
                        Text("중복체크")
                    }
                }
            }

            // "이메일" 입력 필드 및 중복체크 버튼
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InputField(
                        label = "이메일",
                        value = email,
                        onValueChange = { email = it },
                        focusRequester = emailFocusRequester,
                        errorMessage = emailError,
                        modifier = Modifier.weight(0.7f)
                    )

                    Button(
                        onClick = {
                            signUpViewModel.checkDuplicateEmail(email)
                        },
                        modifier = Modifier
                            .weight(0.3f)
                            .height(70.dp)
                            .padding(top = 18.dp)
                    ) {
                        Text("중복체크")
                    }
                }
            }

            item {
                // 중복 확인 결과 메시지를 표시하는 Text 컴포넌트
                if (!emailDuplicateCheckResult.isNullOrEmpty()) {
                    Text(
                        text = emailDuplicateCheckResult!!,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (emailDuplicateCheckResult == "사용가능한 이메일입니다.") Color(0xFF006633) else Color.Red,
                        modifier = Modifier.fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }

            item {
                InputField(
                    label = "비밀번호",
                    value = password,
                    onValueChange = { password = it },
                    focusRequester = passwordFocusRequester,
                    errorMessage = passwordError,
                    isPassword = true
                )
            }

            // "비밀번호 재확인" 입력 필드
            item {
                InputField(
                    label = "비밀번호 재확인",
                    value = confirmPassword,
                    onValueChange = { newPassword ->
                        confirmPassword = newPassword
                        confirmPasswordError = "" // 사용자가 입력할 때마다 에러 메시지 초기화
                        if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                            if (password == confirmPassword) {
                                passwordMatchMessage = "비밀번호가 일치합니다."
                                passwordMatchColor = Color(0xFF808000) // 올리브 그린
                            } else {
                                passwordMatchMessage = "비밀번호가 일치하지 않습니다."
                                passwordMatchColor = Color.Red
                            }
                        } else {
                            passwordMatchMessage = ""
                        }
                    },
                    focusRequester = confirmPasswordFocusRequester,
                    errorMessage = confirmPasswordError,
                    isPassword = true
                )

                if (passwordMatchMessage.isNotEmpty()) {
                    Text(
                        text = passwordMatchMessage,
                        color = passwordMatchColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 버튼 영역
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 사용자가 "다음" 버튼을 클릭했을 때 데이터 업데이트
                    Button(
                        onClick = {
                            validateFields()
                            if (emailError.isBlank() && passwordError.isBlank()) {
                                // 데이터 업데이트
                                signUpViewModel.updateName(name)
                                signUpViewModel.updateNickname(nickname)
                                signUpViewModel.updateEmail(email)
                                signUpViewModel.updatePassword(password)
                                // 다음 화면으로 네비게이션
                                navController.navigate("signUpThirdForm")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text("다음")
                    }
                }
            }
        }
    }
}
