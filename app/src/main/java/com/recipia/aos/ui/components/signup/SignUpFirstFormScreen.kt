package com.recipia.aos.ui.components.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.model.signup.PhoneNumberAuthViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 회원가입 동의여부 페이지 (임시 주석처리: 이 페이지는 전화번호 인증이 불가능하여 그냥 놔둠)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SignUpFirstFormScreen(
    navController: NavController,
    phoneNumberAuthViewModel: PhoneNumberAuthViewModel,
    signUpViewModel: SignUpViewModel
) {

    var isCheckedAll by remember { mutableStateOf(false) }
    var isPersonalInfoConsent by remember { mutableStateOf(false) }
    var isDataRetentionConsent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

    // 인증번호 입력 영역 표시 여부를 관리하는 상태
    var isVerificationCodeVisible by remember { mutableStateOf(false) }

    // 이메일 인증 필드
    var timeLeft by remember { mutableIntStateOf(300) } // 5분 = 300초
    var verificationCode by remember { mutableStateOf("") }
    var isTimerRunning by remember { mutableStateOf(false) } // 타이머가 동작 중인지 여부를 나타내는 변수
    var isVerificationButtonEnabled by remember { mutableStateOf(false) } // "인증하기" 버튼 활성화 여부
    var phoneNumber by remember { mutableStateOf("") } // 전화번호 입력 상태

    // AlertDialog를 표시할지 여부를 관리하는 상태
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            // AlertDialog 스타일 커스텀
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

    // 타이머 종료 후 처리
    LaunchedEffect(key1 = timeLeft) {
        if (timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else {
            isVerificationButtonEnabled = false // 타이머가 종료되면 인증하기 버튼 비활성화
            // 필요하다면 여기에 타이머 종료 관련 추가 로직을 구현
        }
    }

    // 전화번호 형식 검증 함수
    fun isValidPhoneNumber(number: String): Boolean {
        return number.matches("^[0-9]{9,11}$".toRegex())
    }

    // 인증코드 형식 검증 함수
    fun isValidVerificationCode(code: String): Boolean {
        return code.matches("^[0-9]{6}$".toRegex())
    }

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
            containerColor = Color.White, // 배경색을 하얀색으로 설정
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(Color.White),
                    title = {
                        Text(
                            text = "회원가입 (1/3)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
//                Spacer(modifier = Modifier.height(80.dp))

                Text(
                    text = "휴대폰 번호를 입력해주세요.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "입력된 번호로 인증번호가 전송됩니다.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // 전화번호 입력 필드
                    OutlinedTextField(
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(27, 94, 32), // 포커스 상태일 때의 태두리 색상
                            unfocusedBorderColor = Color.Gray, // 포커스가 없을 때의 태두리 색상
                            focusedLabelColor = Color(27, 94, 32) // 포커스 상태일 때의 라벨 색상
                        ),
                        value = phoneNumber,
                        onValueChange = { input ->
                            val filteredInput = input.filter { it.isDigit() }
                            if (filteredInput.length <= 11) {
                                // 전화번호가 변경되고 타이머가 실행 중이라면 초기화
                                if (phoneNumber != filteredInput && isTimerRunning) {
                                    // 타이머와 관련된 상태 및 서버 응답 메시지 초기화
                                    timeLeft = 0
                                    isTimerRunning = false
                                    isVerificationCodeVisible = false
                                    verificationCode = ""
                                    // ViewModel 상태 초기화
                                    phoneNumberAuthViewModel.resetVerificationState()
                                    // 서버 응답 메시지 초기화
                                    phoneNumberAuthViewModel.responseCode = 0
                                    phoneNumberAuthViewModel.verificationMessage = ""
                                }
                                phoneNumber = filteredInput
                            }
                        },
                        label = { Text("휴대폰 번호", color = Color.LightGray) },
                        placeholder = {
                            Text(
                                "01012345678",
                                style = TextStyle(color = Color.Gray)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        readOnly = phoneNumberAuthViewModel.isVerificationSuccess
                    )

                    // 인증코드 전송 버튼
                    Button(
                        onClick = {
                            phoneNumberAuthViewModel.phone = phoneNumber // ViewModel에 전화번호 저장
                            // 인증코드 전송 함수 호출
                            phoneNumberAuthViewModel.sendVerificationCode(phoneNumber)
                            timeLeft = 300 // 타이머를 5분으로 재설정
                            isTimerRunning = true // 타이머 시작
                            isVerificationButtonEnabled = true // 인증하기 버튼 활성화
                            isVerificationCodeVisible = true // 인증코드 입력 영역 표시

                            // 키보드 숨기기
                            keyboardController?.hide()
                        },
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isValidPhoneNumber(phoneNumber)) Color(
                                27,
                                94,
                                32
                            ) else Color.LightGray, // 유효한 번호일 때와 아닐 때의 색상 설정
                            contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                        ),
                        enabled = isValidPhoneNumber(phoneNumber), // 버튼 활성화 여부
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(62.dp) // 높이 지정
                            .padding(top = 6.dp, start = 8.dp) // 오른쪽 여백 추가
                    ) {
                        Text("인증코드 전송")
                    }
                }

                // 인증코드 전송 버튼 아래의 서버 응답 메시지 표시
                if (phoneNumberAuthViewModel.responseCode != 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        phoneNumberAuthViewModel.verificationMessage,
                        color = if (phoneNumberAuthViewModel.responseCode == 400) Color.Red else Color(
                            0xFF006633
                        ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 2.dp)
                    )
                }

                // 인증코드 입력 영역과 관련된 UI를 AnimatedVisibility로 감싸서 부드러운 표시/숨김 처리
                AnimatedVisibility(
                    visible =  phoneNumberAuthViewModel.verificationMessage == "인증코드가 발송되었습니다."
//                    visible = isVerificationCodeVisible && phoneNumberAuthViewModel.responseCode != 400,
//                enter = fadeIn(initialAlpha = 0.3f),
//                exit = fadeOut(targetAlpha = 0f)
                ) {
                    // 인증코드 입력 영역
                    Column {  // 인증코드 입력 영역을 Column으로 감싸서 정렬
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),  // 여기에 padding을 추가하여 내부 요소 간 간격 조정
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedTextField(
                                value = verificationCode,
                                onValueChange = { input ->
                                    verificationCode = input.filter { it.isDigit() }.take(6)
                                },
                                label = { Text("인증코드") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number,
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color(27, 94, 32), // 포커스 상태일 때의 태두리 색상
                                    unfocusedBorderColor = Color.Gray, // 포커스가 없을 때의 태두리 색상
                                    focusedLabelColor = Color(27, 94, 32) // 포커스 상태일 때의 라벨 색상
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp)) // 필드와 메시지 사이의 간격

                            Button(
                                onClick = {
                                    // 인증코드 검증 요청하기
                                    phoneNumberAuthViewModel.checkVerificationCode(verificationCode)
                                    if (phoneNumberAuthViewModel.isVerificationSuccess) {
                                        // 인증 성공시 ViewModel에 전화번호 저장
                                        signUpViewModel.updatePhoneNumber(phoneNumber)
                                    }

                                    // 키보드 닫기
                                    keyboardController?.hide()
                                },
                                enabled = isValidVerificationCode(verificationCode), // 버튼 활성화 여부
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isValidVerificationCode(verificationCode)) Color(
                                        27,
                                        94,
                                        32
                                    ) else Color.LightGray, // 유효한 번호일 때와 아닐 때의 색상 설정
                                    contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .height(62.dp) // 높이 지정
                                    .padding(top = 6.dp, start = 8.dp) // 오른쪽 여백 추가
                            ) {
                                Text("인증하기")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp)) // 필드와 메시지 사이의 간격

                        // 타이머 및 인증 성공/실패 메시지 처리
                        if (phoneNumberAuthViewModel.isVerificationSuccess) {
                            // 인증 성공 메시지 표시
                            Text(
                                phoneNumberAuthViewModel.verificationSuccessMessage,
                                color = Color(0xFF006633), // 초록색
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            // 인증 성공 시 타이머 비활성화
                            isTimerRunning = false
                        } else {
                            // 인증 실패 메시지 표시
                            if (phoneNumberAuthViewModel.verificationSuccessMessage.isNotEmpty()) {
                                Text(
                                    phoneNumberAuthViewModel.verificationSuccessMessage,
                                    color = Color.Red, // 빨간색으로 실패 메시지 표시
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Start)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp)) // 필드와 메시지 사이의 간격

                            // 타이머와 관련된 UI는 인증 실패 메시지가 표시된 경우에만 보여주도록 함
                            if (phoneNumberAuthViewModel.verificationMessage == "인증코드가 발송되었습니다.") {
                                if (timeLeft > 0) {
                                    // 타이머가 활성화되어 있고 시간이 남아 있는 경우
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val minutes = timeLeft / 60
                                        val seconds = timeLeft % 60
                                        val timerText =
                                            String.format("남은 시간: %02d:%02d", minutes, seconds)

                                        Text(
                                            text = timerText,
                                            color = Color.Red,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "인증코드가 도착하지 않았다면 다시 전송 버튼을 눌러주세요.",
                                        modifier = Modifier.align(Alignment.Start),
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    // 타이머가 0이 되었을 때
                                    Text(
                                        "인증코드 재전송이 필요합니다.",
                                        color = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 첫 번째 체크박스 영역 (모두 동의)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isCheckedAll = !isCheckedAll
                                    isPersonalInfoConsent = isCheckedAll
                                    isDataRetentionConsent = isCheckedAll
                                }
                        ) {
                            Checkbox(
                                checked = isCheckedAll,
                                onCheckedChange = {
                                    isCheckedAll = it
                                    isPersonalInfoConsent = it
                                    isDataRetentionConsent = it
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(27, 94, 32),
                                    uncheckedColor = Color(27, 94, 32).copy(alpha = 0.6f)
                                )
                            )

                            Text(
                                text = "모두 동의합니다",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        // 첫번째 체크박스 영역 (개인정보 수집 및 이용 동의)
                        CheckboxWithViewButton(
                            label = "개인정보 수집 및 이용 동의 (필수)",
                            isChecked = isPersonalInfoConsent,
                            onCheckedChange = { isPersonalInfoConsent = it }
                        )

                        // 두번째 체크박스 영역 (개인정보 보관 및 파기 동의)
                        CheckboxWithViewButton(
                            label = "개인정보 보관 및 파기 동의 (필수)",
                            isChecked = isDataRetentionConsent,
                            onCheckedChange = { isDataRetentionConsent = it }
                        )

                        // "다음" 버튼을 눌렀을 때의 동작
                        val onAgreeButtonClick: () -> Unit = {
                            // 필수 항목을 모두 동의했는지 확인
                            if (isPersonalInfoConsent && isDataRetentionConsent) {
                                // 동의 상태를 ViewModel에 저장
                                signUpViewModel.updatePersonalInfoConsent(isPersonalInfoConsent)
                                signUpViewModel.updateDataRetentionConsent(isDataRetentionConsent)
                                signUpViewModel.updatePhoneNumber(phoneNumber)

                                // 동의한 경우 다음 단계로 이동
                                navController.navigate("signUpSecondForm")
                            } else {
                                // 필수 항목 중 하나라도 동의하지 않은 경우 스낵바 메시지 표시
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "필수 항목은 동의해주셔야 합니다.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }

                        // "다음" 버튼 활성화 여부 결정
                        val isAgreeButtonEnabled =
                            phoneNumberAuthViewModel.isVerificationSuccess && isPersonalInfoConsent && isDataRetentionConsent

                        // "다음" 버튼 영역
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // "다음" 버튼 영역
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.CenterVertically)
                                    .height(62.dp) // 높이 지정
                                    .padding(top = 6.dp),
                                onClick = onAgreeButtonClick,
                                enabled = isAgreeButtonEnabled, // 동의 버튼 활성화 여부
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isValidPhoneNumber(phoneNumber)) Color(
                                        27,
                                        94,
                                        32
                                    ) else Color.LightGray, // 유효한 번호일 때와 아닐 때의 색상 설정
                                    contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                                )
                            ) {
                                Text(text = "다음")
                            }
                        }
                    }
                }
            }
        }
    }
}
