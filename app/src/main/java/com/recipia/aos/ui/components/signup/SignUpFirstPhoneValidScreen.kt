package com.recipia.aos.ui.components.signup

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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.model.signup.PhoneNumberAuthViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel
import kotlinx.coroutines.launch

/**
 * 회원가입 첫번째 페이지 (전화번호 중복체크 및 동의여부 받기)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpFirstPhoneValidScreen(
    navController: NavController,
    phoneNumberAuthViewModel: PhoneNumberAuthViewModel,
    signUpViewModel: SignUpViewModel
) {
    var phoneNumber by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf(false) } // 전화번호 중복 여부를 나타내는 상태
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    val keyboardController = LocalSoftwareKeyboardController.current

    // 동의여부 체크
    var isCheckedAll by remember { mutableStateOf(false) }
    var isPersonalInfoConsent by remember { mutableStateOf(false) }
    var isDataRetentionConsent by remember { mutableStateOf(false) }

    // 전화번호 유효성 검사 함수
    fun isValidPhoneNumber(number: String): Boolean {
        // 대한민국 전화번호 형식에 맞는 정규식, 필요에 따라 수정 가능
        val regex = "^01[016789][1-9]\\d{6,7}$".toRegex()
        return number.matches(regex) && number.length >= 8
    }

    // 모든 조건 체크
    val isAllConditionsSatisfied = isValidPhoneNumber(phoneNumber) && isPersonalInfoConsent && isDataRetentionConsent

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
                Text(
                    text = "휴대폰 번호를 입력해주세요.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "* 전화번호 입력과 약관 동의를 모두 해주셔야 버튼이 활성화됩니다.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 전화번호 입력 필드
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { input ->
                        phoneNumber = input.filter { it.isDigit() }
                    },
                    label = { Text("휴대폰 번호", color = Color.LightGray) },
                    placeholder = {
                        Text(
                            "01012345678",
                            style = TextStyle(color = Color.Gray)
                        )
                    },
                    isError = phoneNumberError, // 중복일 경우 오류 표시
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(27, 94, 32), // 포커스 상태일 때의 태두리 색상
                        unfocusedBorderColor = Color.Gray, // 포커스가 없을 때의 태두리 색상
                        focusedLabelColor = Color(27, 94, 32) // 포커스 상태일 때의 라벨 색상
                    ),
                )

                // 중복 오류 메시지 표시
                if (phoneNumberError) {
                    Text(
                        text = "이미 사용중인 번호입니다.",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 전체 동의
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isCheckedAll,
                        onClick = {
                            isCheckedAll = !isCheckedAll
                            isPersonalInfoConsent = isCheckedAll
                            isDataRetentionConsent = isCheckedAll
                        },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "전체 동의.",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                isCheckedAll = !isCheckedAll
                                isPersonalInfoConsent = isCheckedAll
                                isDataRetentionConsent = isCheckedAll
                            }
                            .padding(start = 8.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 12.dp, vertical = 4.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color.Gray // 구분선의 색상 설정
                )

                // 개인정보 수집 및 이용 동의
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isPersonalInfoConsent,
                        onClick = { isPersonalInfoConsent = !isPersonalInfoConsent },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                                append("(필수) ")
                            }
                            append("개인정보 수집 및 이용 동의")
                        },
                        modifier = Modifier
                            .clickable { isPersonalInfoConsent = !isPersonalInfoConsent }
                            .padding(start = 8.dp)
                    )
                    Spacer(Modifier.weight(1f)) // 텍스트 사이에 공간을 추가해서 "보기"를 우측 끝으로 밀어냄
                    Text(
                        text = "보기",
                        fontWeight = FontWeight.Bold,
                        color = Color(27, 94, 32),
                        modifier = Modifier
                            .clickable { navController.navigate("personalInfoConsent") }
                            .padding(end = 16.dp)
                    )
                }

                // 개인정보 보관 및 파기 동의
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isDataRetentionConsent,
                        onClick = { isDataRetentionConsent = !isDataRetentionConsent },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                                append("(필수) ")
                            }
                            append("개인정보 보관 및 파기 동의")
                        },
                        modifier = Modifier
                            .clickable { isDataRetentionConsent = !isDataRetentionConsent }
                            .padding(start = 8.dp)
                    )
                    Spacer(Modifier.weight(1f)) // 텍스트 사이에 공간을 추가해서 "보기"를 우측 끝으로 밀어냄
                    Text(
                        text = "보기",
                        fontWeight = FontWeight.Bold,
                        color = Color(27, 94, 32),
                        modifier = Modifier
                            .clickable { navController.navigate("dataRetentionConsent") }
                            .padding(end = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // "다음" 버튼
                Button(
                    onClick = {
                        if (isValidPhoneNumber(phoneNumber) && isPersonalInfoConsent && isDataRetentionConsent) {
                            signUpViewModel.updatePhoneNumber(phoneNumber)
                            signUpViewModel.updatePersonalInfoConsent(isPersonalInfoConsent)
                            signUpViewModel.updateDataRetentionConsent(isDataRetentionConsent)
                            navController.navigate("signUpSecondForm")
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("모든 조건을 만족해야 합니다.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isAllConditionsSatisfied,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(27, 94, 32),
                        contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                    )
                ) {
                    Text("다음", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
