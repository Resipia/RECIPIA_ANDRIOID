package com.recipia.aos.ui.components.signup

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.recipia.aos.ui.model.signup.PhoneNumberAuthViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel
import kotlinx.coroutines.delay

/**
 * 회원가입 동의여부 페이지
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberValidAndSignUpAgreeScreen(
    navController: NavController,
    phoneNumberAuthViewModel: PhoneNumberAuthViewModel,
    signUpViewModel: SignUpViewModel
) {

    var isCheckedAll by remember { mutableStateOf(false) }
    var isCheckedTerms by remember { mutableStateOf(false) }
    var isCheckedPrivacy by remember { mutableStateOf(false) }
    var isCheckedOutsourcing by remember { mutableStateOf(false) }
    var isCheckedOptionalPrivacy by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
            onDismissRequest = { showDialog = false },
            title = { Text("주의") },
            text = { Text("뒤로 가시면 입력했던 모든 정보가 초기화 되며 다시 회원가입을 진행하셔야 합니다.") },
            confirmButton = {
                Button(onClick = {
                    signUpViewModel.clearData() // SignUpViewModel 초기화
                    phoneNumberAuthViewModel.clearData() // PhoneNumberAuthViewModel 초기화
                    showDialog = false
                    navController.navigate("login") // "login" 화면으로 이동
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "회원가입",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 전화번호 입력 필드
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("전화번호") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    readOnly = phoneNumberAuthViewModel.isVerificationSuccess // 인증 성공 시 읽기 전용으로 설정
                )
                // 인증번호 전송 버튼
                Button(
                    onClick = {
                        phoneNumberAuthViewModel.phone = phoneNumber // ViewModel에 전화번호 저장
                        phoneNumberAuthViewModel.sendVerificationCode(phoneNumber) // 인증번호 전송
                        isTimerRunning = true
                        isVerificationButtonEnabled = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp) // 오른쪽 여백 추가
                ) {
                    Text("인증번호 전송")
                }
            }

            // 인증번호 전송 버튼 아래의 서버 응답 메시지 표시
            if (phoneNumberAuthViewModel.responseCode == 400 &&
                phoneNumberAuthViewModel.verificationSentMessage.isNotEmpty()
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // 필드와 메시지 사이의 간격
                Text(
                    phoneNumberAuthViewModel.verificationSentMessage,
                    color = Color.Red, // 실패 메시지는 빨간색으로
                    modifier = Modifier.align(Alignment.Start) // 좌측 정렬
                )
            } else if (phoneNumberAuthViewModel.responseCode == 200 &&
                phoneNumberAuthViewModel.verificationSentMessage.isNotEmpty()
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // 필드와 메시지 사이의 간격
                Text(
                    phoneNumberAuthViewModel.verificationSentMessage,
                    color = Color(0xFF006633), // 성공 메시지는 지정된 초록색(#006633)으로
                    fontWeight = FontWeight.Bold, // 볼드 스타일
                    modifier = Modifier.align(Alignment.Start) // 좌측 정렬
                )
            }

            Spacer(modifier = Modifier.height(8.dp)) // 필드와 메시지 사이의 간격

            // 인증번호 입력 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = { verificationCode = it },
                    label = { Text("인증번호") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    readOnly = phoneNumberAuthViewModel.isVerificationSuccess // 인증 성공 시 읽기 전용으로 설정
                )

                // 인증번호 확인 버튼 클릭 이벤트
                Button(
                    onClick = {
                        // 인증코드 검증 요청하기
                        phoneNumberAuthViewModel.checkVerificationCode(verificationCode)
                        if (phoneNumberAuthViewModel.isVerificationSuccess) {
                            // 인증 성공시 ViewModel에 전화번호 저장
                            signUpViewModel.updatePhoneNumber(phoneNumber)
                        }
                    },
                    enabled = isVerificationButtonEnabled, // 버튼 활성화 여부
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp) // 오른쪽 여백 추가
                ) {
                    Text("인증하기")
                }
            }

            // 인증번호 입력창 아래 타이머와 안내 메시지 표시
            if (isTimerRunning && !phoneNumberAuthViewModel.isVerificationSuccess) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val timerText = "남은 시간: ${timeLeft / 60}:${timeLeft % 60}"
                    Text(
                        text = timerText,
                        color = Color.Red,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "인증번호를 받지 못했으면 다시 전송 버튼을 눌러주세요.",
                    modifier = Modifier.align(Alignment.Start),
                    fontWeight = FontWeight.Bold
                )

                LaunchedEffect(key1 = timeLeft) {
                    while (timeLeft > 0) {
                        delay(1000)
                        timeLeft--
                    }
                }
            }

            // 인증 결과 메시지 표시
            if (phoneNumberAuthViewModel.verificationSuccessMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp)) // 필드와 메시지 사이의 간격
                val messageColor = if (phoneNumberAuthViewModel.isVerificationSuccess) {
                    Color(0xFF006633) // 초록색
                } else {
                    Color.Red // 빨간색
                }

                Text(
                    phoneNumberAuthViewModel.verificationSuccessMessage,
                    color = messageColor,
                    fontWeight = FontWeight.Bold, // 볼드 스타일
                    modifier = Modifier.align(Alignment.Start) // 좌측 정렬
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 첫 번째 체크박스 영역 (모두 동의)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        isCheckedAll = !isCheckedAll
                        isCheckedTerms = isCheckedAll
                        isCheckedPrivacy = isCheckedAll
                        isCheckedOutsourcing = isCheckedAll
                        isCheckedOptionalPrivacy = isCheckedAll
                    }
            ) {
                Checkbox(
                    checked = isCheckedAll,
                    onCheckedChange = {
                        isCheckedAll = it
                        isCheckedTerms = it
                        isCheckedPrivacy = it
                        isCheckedOutsourcing = it
                        isCheckedOptionalPrivacy = it
                    },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                )

                Text(
                    text = "모두 동의합니다",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // 두 번째 체크박스 영역 (이용약관 동의)
            CheckboxWithViewButton(
                label = "이용약관 동의 (필수)",
                isChecked = isCheckedTerms,
                onCheckedChange = { isCheckedTerms = it }
            )

            // 세 번째 체크박스 영역 (개인정보 수집 및 이용 동의)
            CheckboxWithViewButton(
                label = "개인정보 수집 및 이용 동의 (필수)",
                isChecked = isCheckedPrivacy,
                onCheckedChange = { isCheckedPrivacy = it }
            )

            // 네 번째 체크박스 영역 (개인정보 처리 위탁 동의)
            CheckboxWithViewButton(
                label = "개인정보 처리 위탁 동의 (필수)",
                isChecked = isCheckedOutsourcing,
                onCheckedChange = { isCheckedOutsourcing = it }
            )

            // 다섯 번째 체크박스 영역 (개인정보 수집 및 이용 동의 선택)
            CheckboxWithViewButton(
                label = "개인정보 수집 및 이용 동의 (선택)",
                isChecked = isCheckedOptionalPrivacy,
                onCheckedChange = { isCheckedOptionalPrivacy = it }
            )

            // "다음" 버튼을 눌렀을 때의 동작
            val onAgreeButtonClick: () -> Unit = {
                // 필수 항목을 모두 동의했는지 확인
                if (isCheckedTerms && isCheckedPrivacy && isCheckedOutsourcing) {
                    // 동의한 경우 회원가입 페이지로 이동
                    navController.navigate("signUpSecondForm")
                } else {
                    // 필수 항목 중 하나라도 동의하지 않은 경우 토스트 메시지 표시
                    Toast.makeText(context, "필수 항목은 동의해주셔야 합니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // "다음" 버튼 활성화 여부 결정
            val isAgreeButtonEnabled =
                phoneNumberAuthViewModel.isVerificationSuccess && isCheckedTerms && isCheckedPrivacy && isCheckedOutsourcing

            // "다음" 버튼 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        // 뒤로 가기 버튼 동작
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "이전")
                }

                // "다음" 버튼 영역
                Button(
                    onClick = onAgreeButtonClick,
                    enabled = isAgreeButtonEnabled, // 동의 버튼 활성화 여부
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "다음")
                }
            }
        }
    }
}
