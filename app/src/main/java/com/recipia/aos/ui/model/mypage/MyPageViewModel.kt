package com.recipia.aos.ui.model.mypage

import TokenManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.mypage.MyPageService
import com.recipia.aos.ui.api.dto.RecipeListResponseDto
import com.recipia.aos.ui.api.dto.ResponseDto
import com.recipia.aos.ui.api.dto.mypage.ChangePasswordRequestDto
import com.recipia.aos.ui.api.dto.mypage.MyPageRequestDto
import com.recipia.aos.ui.api.dto.mypage.MyPageViewResponseDto
import com.recipia.aos.ui.api.dto.mypage.ViewMyPageRequestDto
import com.recipia.aos.ui.api.dto.recipe.detail.MemberProfileRequestDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * 마이페이지 전용 Model
 */
class MyPageViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    enum class PageType {
        BOOKMARK, LIKE, TARGET_MEMBER
    }

    var currentPageType: MutableLiveData<PageType> = MutableLiveData(PageType.TARGET_MEMBER)
    var isFollowing = MutableLiveData<Boolean>()

    // 여기에서 북마크한 레시피, 좋아요한 레시피를 모두 저장하고 뒤로가면 데이터 초기화 시키도록 한다.
    var items = mutableStateOf<List<com.recipia.aos.ui.api.dto.RecipeListResponseDto>>(listOf())
    var highCountRecipe = mutableStateOf<List<com.recipia.aos.ui.api.dto.RecipeListResponseDto>>(listOf())

    // 현재 페이지, 사이즈, 정렬 유형 저장
    var currentRequestPage: Int = 0
    var currentRequestSize: Int = 10
    var currentRequestSortType: String = "new"
    var isLastPage = false

    // 레시피 총 개수 상태
    val _recipeCount = MutableLiveData<Long?>()
    val recipeCount: MutableLiveData<Long?> = _recipeCount

    // 로딩중인지 파악하는 상태
    val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // api로 받아온 마이페이지 데이터
    val _myPageData = MutableLiveData<com.recipia.aos.ui.api.dto.mypage.MyPageViewResponseDto?>()
    val myPageData: MutableLiveData<com.recipia.aos.ui.api.dto.mypage.MyPageViewResponseDto?> = _myPageData

    // 프로필 업데이트 상태
    private val _updateResult = MutableLiveData<com.recipia.aos.ui.api.dto.ResponseDto<Void>?>()
    val updateResult: LiveData<com.recipia.aos.ui.api.dto.ResponseDto<Void>?> = _updateResult

    // 로그인 화면으로 이동해야 함을 알린다.
    val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // 로그인 페이지로 이동시키고 에러도 표시한다.
    val logoutSuccess = MutableLiveData<Boolean>()
    val deActiveAccount = MutableLiveData<Boolean>()
    val logoutError = MutableLiveData<String?>()
    val deactivateAccountError = MutableLiveData<String?>()

    // 프로필 변경시 마이페이지에서 로딩 안하도록 하는 플래그
    val updateComplete = MutableLiveData<Boolean>(true)

    // items와 highCountRecipe를 초기화하는 함수
    fun resetItemsAndHighCountRecipe() {
        items.value = listOf()
        highCountRecipe.value = listOf()
    }

    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val myPageService: MyPageService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = tokenManager.addAccessTokenToHeader(chain)
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.MEMBER_SERVER_URL) // 멤버 서버 요청 // 멤버 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MyPageService::class.java)
    }

    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val recipeMyPageService: MyPageService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = tokenManager.addAccessTokenToHeader(chain)
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.RECIPE_SERVER_URL) // 레시피 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MyPageService::class.java)
    }

    // targetMemberId가 작성한 레시피 갯수 가져오기
    fun getRecipeTotalCount(
        targetMemberId: Long
    ) {
        viewModelScope.launch {

            val response = recipeMyPageService.getRecipeTotalCount(
                com.recipia.aos.ui.api.dto.mypage.MyPageRequestDto(
                    targetMemberId
                )
            )

            // 응답에 따른 동작
            if (response.isSuccessful) {
                _recipeCount.value = response.body()?.result
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    getRecipeTotalCount(targetMemberId) // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
            }
        }
    }

    // targetMemberId가 작성한 레시피 중 조회수 높은 레시피 최대 5개 가져오기 (여기서는 List로 dto를 받음)
    fun getHighRecipe(
        targetMemberId: Long
    ) {
        viewModelScope.launch {

            val response = recipeMyPageService.getHighRecipe(
                com.recipia.aos.ui.api.dto.mypage.MyPageRequestDto(
                    targetMemberId
                )
            )

            // 응답에 따른 동작
            if (response.isSuccessful) {
                highCountRecipe.value = response.body()?.result!!
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    getHighRecipe(targetMemberId) // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
            }
        }
    }

    // 프로필 업데이트 로직
    fun updateProfile(
        context: Context, // Context 추가
        nickname: String,
        introduction: String?,
        deleteFileOrder: Int?,
        profileImageUri: Uri?,
        birth: String?,
        gender: String?
    ) {
        viewModelScope.launch {
            val nicknameRequestBody = nickname.toRequestBody("text/plain".toMediaTypeOrNull())
            val introductionRequestBody =
                introduction?.toRequestBody("text/plain".toMediaTypeOrNull())
            val deleteFileOrderRequestBody =
                deleteFileOrder?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val birthRequestBody = birth?.toRequestBody("text/plain".toMediaTypeOrNull())
            val genderRequestBody = gender?.toRequestBody("text/plain".toMediaTypeOrNull())

            // 이미지 Uri를 MultipartBody.Part로 변환하는 함수를 사용하여 압축된 이미지 처리
            val profileImagePart = profileImageUri?.let { uri ->
                uriToMultipartBodyPart(uri, context)
            }

            // 프로필 업데이트 api 호출
            try {
                val response = myPageService.updateProfile(
                    nicknameRequestBody,
                    introductionRequestBody,
                    profileImagePart,
                    deleteFileOrderRequestBody,
                    birthRequestBody,
                    genderRequestBody
                )
                // api응답 성공
                if (response.isSuccessful) {
                    _updateResult.postValue(response.body())
                    updateComplete.value = false
                    loadMyPageData(tokenManager.loadMemberId()) // 이미지를 불러오기 위해 성공 응답을 받고 업데이트 진행
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        // 토큰 재발급 후 재시도
                        updateProfile(context, nickname, introduction, deleteFileOrder, profileImageUri, birth, gender)
                    }
                } else {
                    Log.e("MyPageViewModel", "Profile update failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "Exception in profile update", e)
            }
        }
    }

    // 이미지를 압축하여 파일로 저장하는 함수
    @Throws(IOException::class)
    private fun compressImageFile(
        context: Context,
        uri: Uri,
        targetSizeBytes: Long = 1024 * 1024 // 기본값으로 1MB 설정
    ): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        var quality = 100
        val byteArrayOutputStream = ByteArrayOutputStream()

        do {
            byteArrayOutputStream.reset()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
            quality -= 5
        } while (byteArrayOutputStream.size() > targetSizeBytes && quality > 0)

        val compressedFileName = "compressed_${System.currentTimeMillis()}.jpg"
        val compressedFile = File(context.cacheDir, compressedFileName).apply {
            FileOutputStream(this).use { fileOutputStream ->
                fileOutputStream.write(byteArrayOutputStream.toByteArray())
            }
        }

        return compressedFile
    }

    // Uri를 MultipartBody.Part로 변환하는 함수
    private fun uriToMultipartBodyPart(uri: Uri, context: Context): MultipartBody.Part? {
        val compressedFile: File = try {
            compressImageFile(context, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        val mimeType = context.contentResolver.getType(uri) ?: "image/*"
        val requestFile = compressedFile.asRequestBody(mimeType.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("profileImage", compressedFile.name, requestFile)
    }

    // 내가 북마크한 레시피 조회
    fun getAllMyBookmarkRecipeList() {

        viewModelScope.launch {
            currentRequestPage = 0
            val response = recipeMyPageService.getAllMyBookmarkRecipeList(
                currentRequestPage,
                currentRequestSize
            )

            // 성공적인 응답 처리 - items에 데이터 설정
            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    getAllMyBookmarkRecipeList() // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
                Log.e(
                    "MyPageViewModel",
                    "Error in getAllMyBookmarkRecipeList: ${response.errorBody()}"
                )
            }
        }
    }

    // 내가 좋아요한 레시피 조회
    fun getAllMyLikeRecipeList() {

        viewModelScope.launch {
            currentRequestPage = 0
            val response =
                recipeMyPageService.getAllMyLikeRecipeList(currentRequestPage, currentRequestSize)

            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    getAllMyLikeRecipeList() // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
                Log.e("MyPageViewModel", "Error in getAllMyLikeRecipeList: ${response.errorBody()}")
            }
        }
    }

    // targetMemberId가 작성한 레시피를 더 불러오는 함수
    fun loadMoreTargetMemberRecipes(
        targetMemberId: Long
    ) {

        viewModelScope.launch {
            currentRequestPage = 0
            val response = recipeMyPageService.getAllTargetMemberRecipeList(
                currentRequestPage,
                currentRequestSize,
                currentRequestSortType,
                targetMemberId
            )

            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    loadMoreTargetMemberRecipes(targetMemberId) // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
                Log.e(
                    "MyPageViewModel",
                    "Error in loadMoreTargetMemberRecipes: ${response.errorBody()}"
                )
            }
            _isLoading.value = false
        }
    }

    // 내가 북마크한 레시피를 더 불러오는 함수
    fun loadMoreMyBookmarkRecipes() {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        viewModelScope.launch {
            val response = recipeMyPageService.getAllMyBookmarkRecipeList(
                currentRequestPage,
                currentRequestSize
            )

            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    loadMoreMyBookmarkRecipes() // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
                Log.e(
                    "MyPageViewModel",
                    "Error in loadMoreMyBookmarkRecipes: ${response.errorBody()}"
                )
            }
            _isLoading.value = false
        }
    }

    // 내가 좋아요한 레시피를 더 불러오는 함수
    fun loadMoreMyLikeRecipes() {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        viewModelScope.launch {
            val response =
                recipeMyPageService.getAllMyLikeRecipeList(currentRequestPage, currentRequestSize)

            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else if (response.code() == 401) {
                handleUnauthorizedError {
                    loadMoreMyLikeRecipes() // 토큰 재발급 후 재시도
                }
            } else {
                // 오류 처리
                Log.e("MyPageViewModel", "Error in loadMoreMyLikedRecipes: ${response.errorBody()}")
            }
            _isLoading.value = false
        }
    }

    // 마이페이지 정보 로딩
    fun loadMyPageData(
        targetMemberId: Long
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.viewMyPage(
                    com.recipia.aos.ui.api.dto.mypage.ViewMyPageRequestDto(
                        targetMemberId
                    )
                )
                if (response.isSuccessful) {
                    _myPageData.value = response.body()?.result
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        loadMyPageData(targetMemberId) // 토큰 재발급 후 재시도
                    }
                } else {
                    // 실패한 응답 처리
                    Log.e(
                        "MyPageViewModel",
                        "Failed to load my page data: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                // 네트워크 오류 등의 예외 처리
                Log.e("MyPageViewModel", "Exception when loading my page data", e)
            }
        }
    }

    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            try {
                val response = myPageService.logout()
                if (response.isSuccessful) {
                    clearSession() // 세션 클리어
                    logoutSuccess.postValue(true) // 로그아웃 성공 표시
                } else if (response.code() == 401) {
                    handleUnauthorizedError { logout() } // 토큰 재발급 후 재시도
                } else {
                    logoutError.postValue("로그아웃 실패") // 로그아웃 실패 메시지 설정
                }
            } catch (e: Exception) {
                logoutError.postValue("네트워크 오류 발생") // 네트워크 오류 메시지 설정
            }
        }
    }

    // 회원 탈퇴
    fun deactivateAccount() {
        viewModelScope.launch {
            try {
                val response = myPageService.deactivate()
                if (response.isSuccessful) {
                    clearSession()
                    deActiveAccount.postValue(true)  // 로그아웃 성공 (탈퇴 성공으로 간주)
                } else if (response.code() == 401) {
                    handleUnauthorizedError { deactivateAccount() } // 토큰 재발급 후 재시도
                } else {
                    deactivateAccountError.postValue("회원 탈퇴에 실패했습니다.") // 탈퇴 실패 메시지 설정
                }
            } catch (e: Exception) {
                deactivateAccountError.postValue("네트워크 오류 발생") // 네트워크 오류 메시지 설정
            }
        }
    }

    // 프로필 이미지 URL 상태
    val profileImageUrl = MutableLiveData<String?>()

    // 레시피를 작성한 유저 프로필 사진 가져오기
    fun getMemberProfileImage(
        memberId: Long
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.getProfileImage(
                    com.recipia.aos.ui.api.dto.recipe.detail.MemberProfileRequestDto(
                        memberId
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    profileImageUrl.postValue(response.body()?.result)
                } else if (response.code() == 401) {
                    handleUnauthorizedError { getMemberProfileImage(memberId) } // 토큰 재발급 후 재시도
                } else {
                    // 오류 메시지 업데이트
                    profileImageUrl.postValue(null)
                }
            } catch (e: Exception) {
                // 오류 메시지 업데이트
                profileImageUrl.postValue(null)
            }
        }
    }

    // 비밀번호 변경 성공 여부를 나타내는 LiveData
    val passwordChangeSuccess = MutableLiveData<Boolean>()

    // 비밀번호 변경 시 오류 메시지를 나타내는 LiveData
    val passwordChangeError = MutableLiveData<String?>()


    // 비밀번호 변경 요청
    fun changePassword(
        originPassword: String,
        newPassword: String
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.changePassword(
                    com.recipia.aos.ui.api.dto.mypage.ChangePasswordRequestDto(
                        originPassword,
                        newPassword
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    passwordChangeSuccess.postValue(true)
                } else if (response.code() == 401) {
                    handleUnauthorizedError { changePassword(originPassword, newPassword) }
                } else {
                    val errorJson = JSONObject(response.errorBody()?.string())
                    val errorMessage = when (errorJson.optInt("code")) {
                        1001 -> "기존 비밀번호가 잘못되었습니다."
                        9002 -> "잘못된 요청입니다."
                        else -> "알 수 없는 오류가 발생했습니다."
                    }
                    passwordChangeError.postValue(errorMessage)
                }
            } catch (e: Exception) {
                passwordChangeError.postValue("네트워크 에러 발생")
            }
        }
    }

    // jwt 관련정보 초기화
    private fun clearSession() {
        tokenManager.saveAccessToken("")
        tokenManager.saveRefreshToken("")
        tokenManager.saveMemberId(0)
    }

    // 남의 마이페이지 팔로우 관리
    fun updateFollowingStatus(newStatus: Boolean) {
        isFollowing.value = newStatus
    }

    /**
     * 401 Unauthorized 에러 처리 및 토큰 재발급 로직
     */
    private fun handleUnauthorizedError(
        retryAction: suspend () -> Unit
    ) {
        viewModelScope.launch {
            val tokenRepublishManager = TokenRepublishManager(tokenManager)
            val result = tokenRepublishManager.renewTokenIfNeeded()
            if (result) {
                retryAction() // 토큰 재발급 성공 시 전달받은 작업 재시도
            } else {
                // 토큰 재발급에 실패했다면, 로그인 화면으로 이동합니다.
                _navigateToLogin.value = true
            }
        }
    }

    // 홈 화면 이동 초기화
    fun resetNavigateToLogin() {
        _navigateToLogin.value = false
    }

}
