package com.recipia.aos.ui.components.signup

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.signup.function.GenderSelector
import com.recipia.aos.ui.components.signup.function.InputField
import com.recipia.aos.ui.components.signup.function.MyDatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpFormScreen(
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMatchMessage by remember { mutableStateOf("") }
    var passwordMatchColor by remember { mutableStateOf(Color.Black) }
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isAdvertisingAgree by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    // 입력 필드 검증 상태
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf("") }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val nameFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }

    // 입력 필드 검증
    fun validateFields() {
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

        // 이름 검증
        if (name.isBlank()) {
            nameError = "필수 입력값입니다."
            nameFocusRequester.requestFocus()
        } else {
            nameError = ""
        }

        // 휴대폰 필드 검증
        if (phone.isBlank()) {
            phoneError = "필수 입력값입니다."
            phoneFocusRequester.requestFocus()
        } else {
            phoneError = ""
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
                .padding(innerPadding) // Scaffold로부터 제공된 패딩 적용
                .padding(horizontal = 16.dp)
        ) {

            item { Spacer(modifier = Modifier.height(10.dp)) }

            // 입력 필드들
            item {
                InputField(
                    label = "이메일",
                    value = email,
                    onValueChange = { email = it },
                    focusRequester = emailFocusRequester,
                    errorMessage = emailError
                )
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

            // "이름" 입력 필드 관련 코드
            item {
                InputField(
                    label = "이름",
                    value = name,
                    onValueChange = { name = it },
                    focusRequester = nameFocusRequester,
                    errorMessage = nameError
                )
                // 별도의 에러 메시지 표시 부분 제거
                Spacer(modifier = Modifier.height(8.dp))
            }

            // "생년월일" 레이블과 선택 필드
            item {
                Text("생년월일", style = MaterialTheme.typography.bodyMedium)

                // MyDatePickerDialog 호출
                MyDatePickerDialog(onDateSelected = { date ->
                    selectedDate = date
                })
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 선택된 날짜를 보여주는 부분
            item {
                if (selectedDate.isNotEmpty()) {
                    OutlinedTextField(
                        value = "선택된 날짜: $selectedDate",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // "성별" 레이블과 성별 선택 필드
            item {
                Text("성별", style = MaterialTheme.typography.bodyMedium)
                GenderSelector(selectedGender = gender, onGenderSelect = { gender = it })
                Spacer(modifier = Modifier.height(8.dp))
            }

            // "휴대폰" 입력 필드
            item {
                InputField(
                    label = "휴대폰",
                    value = phone,
                    onValueChange = { phone = it },
                    focusRequester = phoneFocusRequester,
                    errorMessage = phoneError,
                    isPhone = true // 휴대폰 입력 필드에 특정 키보드 옵션 적용
                )
            }

            item {
                CheckboxWithViewButton(
                    label = "광고성 정보 수신 전체 동의 (선택)",
                    isChecked = isAdvertisingAgree,
                    onCheckedChange = { isAdvertisingAgree = it }
                )
            }

            // 버튼 영역
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(end = 8.dp) // 버튼 사이 간격 추가
                    ) {
                        Text(text = "이전")
                    }

                    // "다음" 버튼 클릭 시 처리
                    Button(
                        onClick = {
                            validateFields()
                            if (emailError.isBlank() && passwordError.isBlank() /* && 기타 필드 에러 체크 */) {
                                // 모든 필드가 유효한 경우
                                navController.navigate("emailVerification")
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
