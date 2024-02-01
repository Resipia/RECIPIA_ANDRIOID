package com.recipia.aos.ui.components.signup

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.signup.function.InputField
import com.recipia.aos.ui.model.signup.PhoneNumberAuthViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SignUpSecondFormScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel,
    phoneNumberAuthViewModel: PhoneNumberAuthViewModel
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

    val emailDuplicateCheckResult by signUpViewModel.emailDuplicateCheckResult.observeAsState()
    val isPasswordMatching by signUpViewModel.isPasswordMatching.observeAsState()
    val nicknameDuplicateCheckResult by signUpViewModel.nicknameDuplicateCheckResult.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    val listState = rememberLazyListState()

    // "중복 체크" 버튼의 폰트 크기 조절
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val fontSize = when {
        screenWidth < 320.dp -> 12.sp // 작은 화면
        screenWidth < 480.dp -> 14.sp // 중간 크기 화면
        else -> 16.sp // 큰 화면
    }

    // AlertDialog를 표시할지 여부를 관리하는 상태
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            containerColor = Color.White, // AlertDialog 배경색을 하얀색으로 설정
            textContentColor = Color.Black, // 글자색을 검정색으로 설정
            onDismissRequest = { showDialog = false },
            title = { Text("주의", color = Color.Black) },
            text = { Text("뒤로 이동하시면 입력했던 모든 정보가 초기화 되며 다시 회원가입을 진행하셔야 합니다.", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        signUpViewModel.clearData() // SignUpViewModel 초기화
                        phoneNumberAuthViewModel.clearData() // PhoneNumberAuthViewModel 초기화
                        showDialog = false
                        navController.navigate("login") // "login" 화면으로 이동
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(27, 94, 32),
                        contentColor = Color.White
                    )
                ) {
                    Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(27, 94, 32),
                        contentColor = Color.White
                    )
                ) {
                    Text("취소", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

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

    // 이메일 형식을 확인하는 정규식
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
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
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "회원가입 (2/3)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { showDialog = true }) {
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
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding() // 키보드가 활성화될 때 패딩 적용
                    .padding(innerPadding)
                    .padding(horizontal = 9.dp)
                    .fillMaxHeight(), // 화면 크기에 맞게 최대 높이로 설정
                verticalArrangement = Arrangement.Top
            ) {

                item { Spacer(modifier = Modifier.height(20.dp)) }

                // 각 입력 필드 및 검증 로직...
                item {
                    Text(
                        text = "모든 입력값과 중복체크는 필수입니다.",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 9.dp)
                    )
                }

                item { Spacer(modifier = Modifier.height(10.dp)) }

                // 각 입력 필드 및 검증 로직...
                item {
                    InputField(
                        label = "이름",
                        value = name,
                        onValueChange = { name = it },
                        focusRequester = nameFocusRequester,
                        errorMessage = nameError,
                        onErrorMessageChange = { nameError = it }, // 에러 메시지 업데이트 함수 전달
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
                            onValueChange = {
                                nickname = it
                                signUpViewModel.resetNicknameDuplicateCheck() // 중복 체크 결과 초기화
                                nicknameError = ""
                            },
                            focusRequester = nicknameFocusRequester,
                            errorMessage = nicknameError,
                            onErrorMessageChange = { nicknameError = it }, // 에러 메시지 업데이트 함수 전달
                            modifier = Modifier.weight(0.7f)
                        )

                        Button(
                            onClick = {
                                signUpViewModel.checkDuplicateNickname(nickname) { errorMessage ->
                                    // 오류 발생 시 스낵바 알림
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = errorMessage,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(27, 94, 32),
                                contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(0.3f)
                                .height(54.dp) // 높이 지정
                                .padding(start = 8.dp, end = 8.dp) // 오른쪽 여백 추가
                        ) {
                            Text(
                                text = "중복체크",
                                fontSize = fontSize, // 조건부 폰트 크기 사용
                                fontWeight = FontWeight.Bold,
                                color = Color(27, 94, 32)
                            )
                        }
                    }
                }

                // 닉네임 중복 확인 결과 메시지를 표시하는 Text 컴포넌트
                item {
                    if (!nicknameDuplicateCheckResult.isNullOrEmpty()) {
                        Text(
                            text = nicknameDuplicateCheckResult!!,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (nicknameDuplicateCheckResult == "사용 가능한 닉네임입니다.") Color(
                                0xFF006633
                            ) else Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 9.dp, bottom = 12.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                // "이메일" 입력 필드 및 중복체크 버튼
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // "이메일" 입력 필드
                        InputField(
                            label = "이메일",
                            value = email,
                            onValueChange = { newEmail ->
                                email = newEmail
                                signUpViewModel.resetEmailDuplicateCheck() // 중복 체크 결과 초기화
                                if (emailPattern.matches(newEmail) || newEmail.isEmpty()) {
                                    emailError = "" // 오류 메시지 제거
                                } else {
                                    emailError = "잘못된 이메일 형식입니다." // 오류 메시지 설정
                                }
                            },
                            focusRequester = emailFocusRequester,
                            errorMessage = emailError, // 현재 오류 메시지
                            onErrorMessageChange = { emailError = it }, // 오류 메시지 업데이트 콜백
                            isEmail = true, // 이 필드가 이메일 필드임을 표시
                            modifier = Modifier.weight(0.7f)
                        )

                        Button(
                            onClick = {
                                signUpViewModel.checkDuplicateEmail(email) { errorMessage ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = errorMessage,
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(27, 94, 32),
                                contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                            ),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(0.3f)
                                .height(54.dp) // 높이 지정
                                .padding(start = 8.dp, end = 8.dp) // 오른쪽 여백 추가
                        ) {
                            Text(
                                text = "중복체크",
                                fontSize = fontSize, // 조건부 폰트 크기 사용
                                fontWeight = FontWeight.Bold,
                                color = Color(27, 94, 32)
                            )
                        }
                    }
                }

                item {
                    // 중복 확인 결과 메시지를 표시하는 Text 컴포넌트
                    if (!emailDuplicateCheckResult.isNullOrEmpty()) {
                        Text(
                            text = emailDuplicateCheckResult!!,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (emailDuplicateCheckResult == "사용 가능한 이메일입니다.") Color(
                                0xFF006633
                            ) else Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 9.dp, bottom = 12.dp),
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
                        onErrorMessageChange = { passwordError = it },
                        isPassword = true,
                        modifier = Modifier
                            .imePadding() // 키보드가 활성화될 때 패딩 적용
                            .padding(bottom = 2.dp) // 키보드와 겹치지 않도록 하기 위한 패딩 추가
                    )
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
                            .padding(start = 9.dp, bottom = 12.dp)
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
                            val isMatching =
                                password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
                            signUpViewModel.updatePasswordMatching(isMatching) // 비밀번호 일치 여부 업데이트
                            if (isMatching) {
                                passwordMatchMessage = "비밀번호가 일치합니다."
                                passwordMatchColor = Color(0xFF006633)
                            } else {
                                passwordMatchMessage = "비밀번호가 일치하지 않습니다."
                                passwordMatchColor = Color.Red
                            }
                        },
                        focusRequester = confirmPasswordFocusRequester,
                        errorMessage = confirmPasswordError,
                        onErrorMessageChange = { confirmPasswordError = it }, // 에러 메시지 업데이트 함수 전달
                        isPasswordConfirm = true,
                        modifier = Modifier
                            .imePadding() // 키보드가 활성화될 때 패딩 적용
                    )

                    // 비밀번호 일치 메시지 표시
                    if (passwordMatchMessage.isNotEmpty()) {
                        Text(
                            text = passwordMatchMessage,
                            color = passwordMatchColor,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 9.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // 버튼 영역
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 사용자가 "다음" 버튼을 클릭했을 때 데이터 업데이트
                        Button(
                            onClick = {
                                validateFields()
                                if (emailError.isBlank() && passwordError.isBlank() &&
                                    emailDuplicateCheckResult == "사용 가능한 이메일입니다." &&
                                    nicknameDuplicateCheckResult == "사용 가능한 닉네임입니다." &&
                                    isPasswordMatching == true
                                ) {

                                    // 데이터 업데이트
                                    signUpViewModel.updateName(name)
                                    signUpViewModel.updateNickname(nickname)
                                    signUpViewModel.updateEmail(email)
                                    signUpViewModel.updatePassword(password)

                                    // 다음 화면으로 네비게이션
                                    navController.navigate("signUpThirdForm")
                                }
                            },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (emailError.isBlank() && passwordError.isBlank() &&
                                    emailDuplicateCheckResult == "사용 가능한 이메일입니다." &&
                                    nicknameDuplicateCheckResult == "사용 가능한 닉네임입니다." &&
                                    isPasswordMatching == true
                                ) Color(
                                    27,
                                    94,
                                    32
                                ) else Color.LightGray, // 유효한 번호일 때와 아닐 때의 색상 설정
                                contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                            ),
                            enabled = emailError.isBlank() && passwordError.isBlank() &&
                                    emailDuplicateCheckResult == "사용 가능한 이메일입니다." &&
                                    nicknameDuplicateCheckResult == "사용 가능한 닉네임입니다." &&
                                    isPasswordMatching == true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterVertically)
                                .height(62.dp) // 높이 지정
                                .padding(top = 6.dp),
                        ) {
                            Text("다음")
                        }
                    }
                }
            }
        }
    }
}
