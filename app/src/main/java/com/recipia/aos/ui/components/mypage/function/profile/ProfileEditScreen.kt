package com.recipia.aos.ui.components.mypage.function.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.recipia.aos.ui.components.common.ProfilePictureInputField
import com.recipia.aos.ui.components.signup.function.GenderSelector
import com.recipia.aos.ui.components.signup.function.InputField
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
    // 서버로부터 받은 이미지 URL을 Uri 객체로 변환
    profilePictureUri = myPageData?.profileImageUrl?.let { Uri.parse(it) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 초기값 설정
    var oneLineIntroduction by remember { mutableStateOf(myPageData?.introduction ?: "") }
    var nickname by remember { mutableStateOf(myPageData?.nickname ?: "") }
    var selectedDate by remember { mutableStateOf(myPageData?.birth ?: "") }
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    var nicknameError by remember { mutableStateOf("") }
    val nicknameFocusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()

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
                        TextButton(
                            onClick = {
                                // "s3" 문자열이 포함되어 있는지 검사
                                val finalProfilePictureUri = if (profilePictureUri?.toString()
                                        ?.contains("s3") == true
                                ) null else profilePictureUri

                                myPageViewModel.updateProfile(
                                    context = context,
                                    nickname = nickname, // nickname을 입력받은 값으로 변경
                                    introduction = oneLineIntroduction,
                                    profileImageUri = finalProfilePictureUri, // 조건에 따라 변경된 URI 사용
                                    deleteFileOrder = 0, // TODO: 이 부분을 실제 값으로 업데이트 필요
                                    birth = selectedDate,
                                    gender = when (gender) {
                                        "남성" -> "M"
                                        "여성" -> "F"
                                        else -> null
                                    }
                                )
                                navController.popBackStack()
                            }) {
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
                item { Spacer(modifier = Modifier.height(10.dp)) }

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
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // todo: 닉네임 수정 (중복된 닉네임이면 수정 못하게 해야함)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nickname,
                            onValueChange = {
                                nickname = it
                                // 중복 체크 결과 초기화 및 에러 메시지 업데이트
                                signUpViewModel.resetNicknameDuplicateCheck()
                                nicknameError = ""
                            },
                            label = { Text("닉네임") },
                            modifier = Modifier
                                .weight(0.7f), // Row 내에서 차지하는 비율 조정
                            isError = nicknameError.isNotEmpty(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp)) // 닉네임 필드와 버튼 사이의 간격 추가

                        Button(
                            onClick = {
                                signUpViewModel.checkDuplicateNickname(nickname) { errorMessage ->
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(0.3f) // Row 내에서 차지하는 비율 조정
                                .padding(top = 8.dp)
                                .height(54.dp), // 높이 지정
                            shape = MaterialTheme.shapes.medium.copy(CornerSize(0.dp)) // 버튼 모서리를 직사각형으로 설정
                        ) {
                            Text("중복체크")
                        }
                    }

                    if (nicknameError.isNotEmpty()) {
                        Text(
                            text = nicknameError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 한줄 소개
                item {
                    OutlinedTextField(
                        value = oneLineIntroduction,
                        onValueChange = { oneLineIntroduction = it },
                        label = { Text("한줄 소개") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 생년월일
                item {
                    Text("생년월일", style = MaterialTheme.typography.bodyMedium)
                    MyDatePickerDialog(onDateSelected = { selectedDate = it })
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 날짜 선택
                if (selectedDate.isNotEmpty()) {
                    item {
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
