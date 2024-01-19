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

    // 필수 입력 필드가 모두 입력되었는지 검사하는 함수
    fun isInputValid(): Boolean {
        return email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                name.isNotBlank() &&
                selectedDate.isNotBlank() &&
                gender.isNotBlank() &&
                phone.isNotBlank()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
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
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 입력 필드들
        item { InputField("이메일", email, { email = it }) }
        item { InputField("비밀번호", password, { password = it }, isPassword = true) }

        // "비밀번호 재확인" 입력 필드
        item {
            InputField("비밀번호 재확인", confirmPassword, { newPassword ->
                confirmPassword = newPassword
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
            }, isPassword = true)

            if (passwordMatchMessage.isNotEmpty()) {
                Text(
                    text = passwordMatchMessage,
                    color = passwordMatchColor,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item { InputField("이름", name, { name = it }) }

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

        // "휴대폰" 레이블과 입력 필드
        item {
            Text("휴대폰", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("-없이 입력") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                        if (isInputValid()) {
                            // 모든 필드가 유효하면 다음 화면으로 이동
                            navController.navigate("emailVerification")
                        } else {
                            // 필수 입력 필드가 비어있을 경우 메시지 표시
                            Toast.makeText(context, "필수 입력 필드를 모두 작성하세요.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Text(text = "다음")
                }
            }
        }

    }
}
