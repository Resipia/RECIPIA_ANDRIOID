package com.recipia.aos.ui.model.mypage

import TokenManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.mypage.MyPageService
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.dto.ResponseDto
import com.recipia.aos.ui.dto.mypage.ChangePasswordRequestDto
import com.recipia.aos.ui.dto.mypage.MyPageRequestDto
import com.recipia.aos.ui.dto.mypage.MyPageViewResponseDto
import com.recipia.aos.ui.dto.mypage.ViewMyPageRequestDto
import com.recipia.aos.ui.dto.recipe.detail.MemberProfileRequestDto
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

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
    var items = mutableStateOf<List<RecipeListResponseDto>>(listOf())
        private set // 이렇게 하면 외부에서는 읽기만 가능해짐

    var highCountRecipe = mutableStateOf<List<RecipeListResponseDto>>(listOf())
        private set // 이렇게 하면 외부에서는 읽기만 가능해짐

    // 현재 페이지, 사이즈, 정렬 유형 저장
    var currentRequestPage: Int = 0
    var currentRequestSize: Int = 10
    var currentRequestSortType: String = "new"
    var isLastPage = false

    // 레시피 총 개수 상태
    private val _recipeCount = MutableLiveData<Long?>()
    val recipeCount: MutableLiveData<Long?> = _recipeCount

    val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // api로 받아온 마이페이지 데이터
    private val _myPageData = MutableLiveData<MyPageViewResponseDto?>()
    val myPageData: MutableLiveData<MyPageViewResponseDto?> = _myPageData

    // 프로필 업데이트 상태
    private val _updateResult = MutableLiveData<ResponseDto<Void>?>()
    val updateResult: LiveData<ResponseDto<Void>?> = _updateResult

    // _myPageData를 초기화하는 함수
    fun resetMyPageData() {
        _myPageData.value?.followId = 0L
        // 또는
        // _myPageData.value = MyPageViewResponseDto() // 기본 상태의 객체로 초기화 (구조에 따라 달라짐)
    }

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

            val response = recipeMyPageService.getRecipeTotalCount(MyPageRequestDto(targetMemberId))

            // 응답에 따른 동작
            if (response.isSuccessful) {
                _recipeCount.value = response.body()?.result
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

            val response = recipeMyPageService.getHighRecipe(MyPageRequestDto(targetMemberId))

            // 응답에 따른 동작
            if (response.isSuccessful) {
                highCountRecipe.value = response.body()?.result!!
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

            // 여기서 context를 사용하여 Bitmap을 로드
            val profileImagePart = profileImageUri?.let { uri ->
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val requestBody = byteArrayOutputStream.toByteArray()
                    .toRequestBody("image/jpeg".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profileImage", "file.jpg", requestBody)
            }

            try {
                val response = myPageService.updateProfile(
                    nicknameRequestBody,
                    introductionRequestBody,
                    profileImagePart,
                    deleteFileOrderRequestBody,
                    birthRequestBody,
                    genderRequestBody
                )
                if (response.isSuccessful) {
                    _updateResult.postValue(response.body())
                } else {
                    Log.e(
                        "MyPageViewModel",
                        "Profile update failed: ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "Exception in profile update", e)
            }
        }
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
            } else {
                // 오류 처리
                Log.e("MyPageViewModel", "Error in getAllMyLikeRecipeList: ${response.errorBody()}")
            }
        }
    }

    // targetMemberId가 작성한 레시피를 더 불러오는 함수
    fun loadMoreTargetMemberRecipes(targetMemberId: Long) {

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
                val response = myPageService.viewMyPage(ViewMyPageRequestDto(targetMemberId))
                if (response.isSuccessful) {
                    _myPageData.value = response.body()?.result
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
    fun logout(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.logout()
                if (response.isSuccessful) {
                    clearSession()
                    onSuccess()
                } else {
                    onError("로그아웃 실패")
                }
            } catch (e: Exception) {
                onError("네트워크 에러 발생")
            }
        }
    }

    // 회원 탈퇴
    fun deactivateAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.deactivate()
                if (response.isSuccessful) {
                    clearSession()
                    onSuccess()
                } else {
                    onError("회원탈퇴 실패")
                }
            } catch (e: Exception) {
                onError("네트워크 에러 발생")
            }
        }
    }

    // 마이페이지 작성한 유저 프로필 사진 가져오기
    fun getMemberProfileImage(
        memberId: Long,
        onSuccess: (String?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // 이미지 가져오는 요청 전송
                val response = myPageService.getProfileImage(MemberProfileRequestDto(memberId))
                if (response.isSuccessful && response.body() != null) {
                    // 성공적으로 URL을 받아왔을 경우 onSuccess 콜백 호출
                    onSuccess(response.body()?.result)
                } else {
                    // 응답은 받았으나 실패했거나 바디가 null일 경우 onError 콜백 호출
                    onError("프로필 이미지를 가져오는 요청이 실패했습니다.")
                }
            } catch (e: Exception) {
                // 네트워크 오류 등의 예외 발생 시 onError 콜백 호출
                onError("네트워크 에러 발생")
            }
        }
    }

    // 비밀번호 변경 요청
    fun changePassword(
        originPassword: String,
        newPassword: String,
        onSuccess: (Long?) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = myPageService.changePassword(
                    ChangePasswordRequestDto(
                        originPassword,
                        newPassword
                    )
                )
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()?.result)
                } else {
                    val errorResponseBody = response.errorBody()?.string()
                    val errorJson = errorResponseBody?.let { JSONObject(it) }

                    if (errorJson != null) {
                        val errorCode = errorJson.optInt("code")

                        when (errorCode) {
                            1001 -> onError("기존 비밀번호가 잘못되었습니다.")
                            9002 -> onError("잘못된 요청입니다.")
                            else -> onError("알 수 없는 오류가 발생했습니다.")
                        }
                    }
                }
            } catch (e: Exception) {
                onError("네트워크 에러 발생")
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

}
