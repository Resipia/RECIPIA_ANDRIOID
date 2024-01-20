package com.recipia.aos.ui.components.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.recipia.aos.ui.components.signup.function.GenderSelector
import com.recipia.aos.ui.components.signup.function.InputField
import com.recipia.aos.ui.components.signup.function.MyDatePickerDialog
import com.recipia.aos.ui.model.signup.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingFormScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var isAdvertisingAgree by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }

    // 입력 필드 검증 상태
    val nameFocusRequester = remember { FocusRequester() }

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

            // "이름" 입력 필드 관련 코드
            item {
                InputField(
                    label = "이름",
                    value = name,
                    onValueChange = { name = it },
                    focusRequester = nameFocusRequester,
                    errorMessage = "nameError"
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
                            // 모든 필드가 유효한 경우
                            navController.navigate("signUpSuccess")
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
