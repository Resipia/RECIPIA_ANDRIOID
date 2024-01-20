package com.recipia.aos.ui.components.signup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
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
    // 프로필 사진, 한줄 소개, 생년월일, 성별 상태 관리
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var oneLineIntroduction by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }

    // 입력 필드 검증 상태
    val oneLineIntroFocusRequester = remember { FocusRequester() }

    // 이미지 선택기를 초기화
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            profilePictureUri = it
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

            // 프로필 사진 입력 필드
            item {
                ProfilePictureInputField(
                    profilePictureUri = profilePictureUri,
                    onImageSelected = {
                        imagePickerLauncher.launch(
                            // 이미지만 선택 가능하도록
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 한줄 소개 입력 필드
            item {
                OutlinedTextField(
                    value = oneLineIntroduction,
                    onValueChange = { oneLineIntroduction = it },
                    label = { Text("한줄 소개") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(oneLineIntroFocusRequester) // focusRequester를 Modifier에 추가
                )
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

            // 하단 버튼 영역
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { /* 건너뛰기 로직 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("건너뛰기")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { /* 회원가입 완료 로직 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("회원가입 완료")
                    }
                }
            }
        }
    }
}

// 프로필 사진 입력 필드 컴포저블 함수
@Composable
fun ProfilePictureInputField(
    profilePictureUri: Uri?,
    onImageSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp) // 상단 여백 조정
            .height(150.dp) // 박스 높이 조정
            .aspectRatio(1f), // 정사각형 비율 유지
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp) // 프로필 이미지 영역 크기 1.5배로 증가
                .border(2.dp, Color.Gray, shape = RoundedCornerShape(75.dp)) // 원형 테두리 크기 조정
                .clip(RoundedCornerShape(75.dp)), // 이미지 원형으로 클립
            contentAlignment = Alignment.Center
        ) {
            if (profilePictureUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profilePictureUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize() // 이미지 크기 최대로 조정
                )
            } else {
                IconButton(onClick = { onImageSelected() }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "프로필 사진 추가",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}