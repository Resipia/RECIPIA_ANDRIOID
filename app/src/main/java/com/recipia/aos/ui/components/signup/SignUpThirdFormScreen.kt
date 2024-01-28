package com.recipia.aos.ui.components.signup

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.recipia.aos.ui.components.common.ProfilePictureInputField
import com.recipia.aos.ui.components.signup.function.GenderSelector
import com.recipia.aos.ui.components.signup.function.MyDatePickerDialog
import com.recipia.aos.ui.model.signup.PhoneNumberAuthViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpThirdFormScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel,
    phoneNumberAuthViewModel: PhoneNumberAuthViewModel
) {
    // 프로필 사진, 한줄 소개, 생년월일, 성별 상태 관리
    val context = LocalContext.current
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var oneLineIntroduction by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    val scope = rememberCoroutineScope() // 코루틴 스코프 생성

    // 입력 필드 검증 상태
    val oneLineIntroFocusRequester = remember { FocusRequester() }

    // 이미지 선택기를 초기화
    val imagePickerLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            // 크롭된 이미지의 Uri를 받아서 저장
            profilePictureUri = result.uriContent
        } else {
            // 오류 처리
            val exception = result.error
        }
    }

    if (profilePictureUri != null) {
        bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, profilePictureUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, profilePictureUri!!)
            ImageDecoder.decodeBitmap(source)
        }
    }

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "회원가입 (3/3)",
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Scaffold로부터 제공된 패딩 적용
                .padding(horizontal = 16.dp)
        ) {

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                // 여기에 "프로필 설정 (선택)" 텍스트 추가
                Text(
                    text = "프로필 설정 (선택)",
                    fontSize = 12.sp,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), // 스타일 설정
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp), // 하단 패딩 추가
                    color = MaterialTheme.colorScheme.onSurface // 텍스트 색상 설정
                )
            }

            // 프로필 사진 입력 필드 사용하는 부분
            item {
                ProfilePictureInputField(
                    profilePictureUri = profilePictureUri,
                    onImageSelected = {
                        // CropImageContractOptions 객체를 생성하여 이미지 선택 및 크롭 로직 호출
                        val cropImageOptions = CropImageContractOptions(
                            CropImage.CancelledResult.uriContent,
                            CropImageOptions()
                        )
                        imagePickerLauncher.launch(cropImageOptions)
                    },
                    onImageRemoved = {
                        // 이미지 제거 로직
                        profilePictureUri = null
                    }
                )
            }

            // 문자 수를 계산하여 표시할 변수 추가
            var charCount = oneLineIntroduction.length

            // 한줄 소개 입력 필드
            item {
                Text(
                    "한 줄 소개 (선택)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = oneLineIntroduction,
                    onValueChange = { newValue ->
                        val byteCount = newValue.toByteArray(Charsets.UTF_8).size
                        if (byteCount <= 300) {
                            oneLineIntroduction = newValue
                            // 문자 수 업데이트
                            charCount = newValue.length
                        }
                    },
                    label = { Text("한 줄 소개") }, // 초기 문자 수 표시
                    isError = oneLineIntroduction.toByteArray(Charsets.UTF_8).size > 300, // 300바이트를 초과하면 에러 처리
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(oneLineIntroFocusRequester) // focusRequester를 Modifier에 추가
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // "생년월일" 레이블과 선택 필드
            item {
                Text(
                    "생년월일 (선택)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // MyDatePickerDialog 호출
                MyDatePickerDialog(onDateSelected = { date ->
                    selectedDate = date
                })
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 선택된 날짜를 보여주는 부분
            item {
                if (selectedDate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))

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
                Text(
                    "성별 (선택)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                GenderSelector(selectedGender = gender, onGenderSelect = { gender = it })
                Spacer(modifier = Modifier.height(28.dp))
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
                        onClick = {
                            // 성별 데이터 변환 및 업데이트
                            val genderCode = when (gender) {
                                "남성" -> "M"
                                "여성" -> "F"
                                else -> ""
                            }
                            signUpViewModel.updateGender(genderCode)

                            // 기타 데이터 업데이트
                            signUpViewModel.updateProfilePictureUri(profilePictureUri)
                            signUpViewModel.updateOneLineIntroduction(oneLineIntroduction)
                            signUpViewModel.updateSelectedDate(selectedDate)

                            // 회원가입 요청
                            signUpViewModel.signUp(
                                context,
                                onSuccess = {
                                    signUpViewModel.clearData() // 데이터 초기화
                                    phoneNumberAuthViewModel.clearData() // PhoneNumberAuthViewModel 초기화
                                    navController.navigate("login-success-page")
                                },
                                onFailure = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "회원가입 실패",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(27, 94, 32),
                            contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(54.dp) // 높이 지정
                            .weight(1f)
                    ) {
                        Text("건너뛰기", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            // 성별 데이터 변환 및 업데이트
                            val genderCode = when (gender) {
                                "남성" -> "M"
                                "여성" -> "F"
                                else -> ""
                            }
                            signUpViewModel.updateGender(genderCode)

                            // 기타 데이터 업데이트
                            signUpViewModel.updateProfilePictureUri(profilePictureUri)
                            signUpViewModel.updateOneLineIntroduction(oneLineIntroduction)
                            signUpViewModel.updateSelectedDate(selectedDate)

                            // 회원가입 요청
                            signUpViewModel.signUp(
                                context,
                                onSuccess = {
                                    signUpViewModel.clearData() // 데이터 초기화
                                    phoneNumberAuthViewModel.clearData() // PhoneNumberAuthViewModel 초기화
                                    navController.navigate("login-success-page")
                                },
                                onFailure = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "회원가입 실패",
                                            duration = SnackbarDuration.Short
                                        )
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(27, 94, 32),
                            contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .height(54.dp) // 높이 지정
                            .weight(1f)
                    ) {
                        Text("회원가입 완료", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
