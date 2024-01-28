package com.recipia.aos.ui.components.mypage.function.profile

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.recipia.aos.ui.components.signup.ProfilePictureInputField
import com.recipia.aos.ui.components.signup.function.GenderSelector
import com.recipia.aos.ui.components.signup.function.MyDatePickerDialog
import com.recipia.aos.ui.model.mypage.MyPageViewModel

/**
 * 마이페이지 내 프로필 수정하기
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navController: NavController,
    myPageViewModel: MyPageViewModel,
) {

    // 사용자 프로필 정보 가져오기
    val myPageData by myPageViewModel.myPageData.observeAsState()

    // Context 및 기타 상태 변수
    val context = LocalContext.current
    var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 초기값 설정
    var oneLineIntroduction by remember { mutableStateOf(myPageData?.introduction ?: "") }
    var selectedDate by remember { mutableStateOf(myPageData?.birth ?: "") }
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

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
            val exception = result.error
            Toast.makeText(
                context,
                "Image selection failed: ${exception?.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // 프로필 이미지 로딩
    if (myPageData?.profileImageUrl != null && profilePictureUri == null) {
        profilePictureUri = Uri.parse(myPageData!!.profileImageUrl)
    }

    // Bitmap 로딩
    if (profilePictureUri != null && bitmap == null) {
        bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, profilePictureUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, profilePictureUri!!)
            ImageDecoder.decodeBitmap(source)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("프로필 수정", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        myPageViewModel.updateProfile(
                            context = context,
                            nickname = myPageData?.nickname ?: "",
                            introduction = oneLineIntroduction,
                            profileImageUri = profilePictureUri,
                            birth = selectedDate,
                            gender = when (gender) {
                                "남성" -> "M"
                                "여성" -> "F"
                                else -> null
                            }
                        )
                        Toast.makeText(context, "프로필 업데이트를 요청했습니다.", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("저장")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                        val cropImageOptions = CropImageContractOptions(null, CropImageOptions())
                        imagePickerLauncher.launch(cropImageOptions)
                    },
                    onImageRemoved = {
                        profilePictureUri = null
                        bitmap = null
                    }
                )
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
