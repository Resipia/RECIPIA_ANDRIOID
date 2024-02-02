package com.recipia.aos.ui.components.mypage.function.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.signup.SignUpViewModel
import kotlinx.coroutines.launch

/**
 * 마이페이지 내 프로필 수정하기
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ProfileEditScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel,
    signUpViewModel: SignUpViewModel
) {

    // 사용자 프로필 정보 가져오기
    val myPageData by myPageViewModel.myPageData.observeAsState()

    // Context 및 기타 상태 변수
    val context = LocalContext.current
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    profilePictureUri = myPageData?.profileImageUrl?.let { Uri.parse(it) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 초기값 설정
    var oneLineIntroduction by remember { mutableStateOf(myPageData?.introduction ?: "") }
//    var nickname by remember { mutableStateOf(myPageData?.nickname ?: "") }
    var selectedDate by remember { mutableStateOf(myPageData?.birth ?: "") }
    var nicknameError by remember { mutableStateOf("") }
    val nicknameFocusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

    // 중복 체크 결과 관찰
    val nicknameDuplicateCheckResult by signUpViewModel.nicknameDuplicateCheckResult.observeAsState()

    // 초기 성별 값을 "M", "F"에서 "남성", "여성"으로 변환
    var gender by remember {
        mutableStateOf(
            when (myPageData?.gender) {
                "M" -> "남성"
                "F" -> "여성"
                else -> ""
            }
        )
    }

    // GenderSelector에서 사용자가 성별을 선택했을 때 호출될 콜백
    val onGenderSelect: (String) -> Unit = { selectedGender ->
        gender = selectedGender
    }

    // 이미지 픽커
    val imagePickerLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            profilePictureUri = result.uriContent
        } else {
            // 오류 발생 시 스낵바를 표시
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "이미지 선택 취소",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // 키보드 컨트롤러 (터치시 키보드 닫히게 하기)
    val keyboardController = LocalSoftwareKeyboardController.current

    // 사용자의 원래 닉네임을 저장
    val originalNickname = myPageViewModel.myPageData.value?.nickname ?: ""

    // 닉네임 변경 여부를 확인하기 위한 상태
    var isNicknameChanged by remember { mutableStateOf(false) }

    // 닉네임 필드의 값을 추적
    var nickname by remember { mutableStateOf(originalNickname) }

    // 입력 필드 검증
    fun validateFields() {
        // 닉네임 검증
        if (nickname.isBlank()) {
            nicknameError = "필수 입력값입니다."
            nicknameFocusRequester.requestFocus()
        } else {
            nicknameError = ""
        }
    }

    // 닉네임이 변경될 때마다 호출되는 이벤트 핸들러
    val onNicknameChange: (String) -> Unit = { newNickname ->
        nickname = newNickname
        isNicknameChanged = newNickname != originalNickname
        // 닉네임이 변경될 때마다 중복 체크 결과를 초기화
        signUpViewModel.resetNicknameDuplicateCheck()
    }

    // "중복체크" 버튼의 onClick 이벤트
    val onCheckDuplicateClick: () -> Unit = {
        // 키보드를 내립니다.
        keyboardController?.hide()

        // 닉네임 중복 체크 로직
        signUpViewModel.checkDuplicateNickname(nickname) { errorMessage ->
            // 오류 발생 시 스낵바 알림
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // 저장 버튼 클릭 이벤트
    val onSaveClick: () -> Unit = {
        // 입력 필드 검증
        validateFields()

        // 닉네임 변경 여부와 중복 체크 결과 확인
        val canUpdateProfile = when {
            // 닉네임이 변경되지 않았거나, 변경되었으나 중복 체크를 통과한 경우
            !isNicknameChanged || (isNicknameChanged && nicknameDuplicateCheckResult == "사용 가능한 닉네임입니다.") -> true
            // 그 외의 경우
            else -> false
        }

        if (canUpdateProfile) {
            // "s3" 문자열이 포함되어 있는지 검사
            val finalProfilePictureUri = if (profilePictureUri?.toString()?.contains("s3") == true) null else profilePictureUri

            // 프로필 업데이트 로직 실행
            myPageViewModel.updateProfile(
                context = context,
                nickname = nickname,
                introduction = oneLineIntroduction,
                profileImageUri = finalProfilePictureUri,
                deleteFileOrder = 0, // TODO: 이 부분을 실제 값으로 업데이트 필요
                birth = selectedDate,
                gender = when (gender) {
                    "남성" -> "M"
                    "여성" -> "F"
                    else -> null
                }
            )
            // 화면 이동
            navController.navigate("my-page") {
                popUpTo("my-page") { inclusive = true } // 마이페이지를 백스택에서 제거
                launchSingleTop = true // 마이페이지를 새 인스턴스로 시작
            }

        } else {
            // 중복 체크를 통과하지 못한 경우 사용자에게 알림
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "닉네임 변경을 위해 중복체크는 필수입니다.",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    // Composable이 화면에 나타날 때 중복 체크 결과를 초기화
    LaunchedEffect(Unit) {
        signUpViewModel.resetNicknameDuplicateCheck()
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
            containerColor = Color.White,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("프로필 수정", fontSize = 16.sp, color = Color.Black) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로 가기"
                            )
                        }
                    },
                    actions = {
                        // 저장 버튼
                        TextButton(onClick = onSaveClick) {
                            Text("저장")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(24.dp)) }

                // 사진
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
                            profilePictureUri = null
                            bitmap = null
                        }
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 닉네임
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = onNicknameChange,
                            label = { Text("닉네임") },
                            modifier = Modifier
                                .weight(0.7f), // Row 내에서 차지하는 비율 조정
                            isError = nicknameError.isNotEmpty(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // 닉네임 필드와 버튼 사이의 간격 추가

                        Button(
                            onClick = onCheckDuplicateClick, // 중복 체크 함수 호출
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFF673AB7)),
                            modifier = Modifier
                                .weight(0.3f) // Row 내에서 차지하는 비율 조정
                                .padding(top = 7.dp)
                                .height(54.dp), // 높이 지정
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text(
                                "중복 체크",
                                color = Color(0xFF673AB7),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 중복 체크 결과에 따른 메시지 표시
                    nicknameDuplicateCheckResult?.let {
                        Text(
                            text = it,
                            color = if (it.contains("사용 가능")) Color(0xFF006633) else Color.Red,
                            modifier = Modifier.padding(start = 2.dp, top = 4.dp, bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 한줄 소개
                item {
                    OutlinedTextField(
                        value = oneLineIntroduction,
                        onValueChange = { oneLineIntroduction = it },
                        label = { Text("한줄 소개") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 생년월일
                item {
                    Text("생년월일", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))

                    MyDatePickerDialog(
                        onDateSelected = { selectedDate = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 날짜 선택
                if (selectedDate.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = "선택된 날짜: $selectedDate",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 성별 선택
                item {
                    Text("성별", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(4.dp))

                    // 성별 선택 컴포저블
                    GenderSelector(
                        selectedGender = gender,
                        onGenderSelect = onGenderSelect
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
