package com.recipia.aos.ui.model.mypage

import TokenManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.recipe.mypage.MyPageService
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.dto.mypage.MyPageRequestDto
import com.recipia.aos.ui.dto.mypage.MyPageViewResponseDto
import com.recipia.aos.ui.dto.mypage.ViewMyPageRequestDto
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private val _myPageData = MutableLiveData<MyPageViewResponseDto?>()
    val myPageData: MutableLiveData<MyPageViewResponseDto?> = _myPageData

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
            .baseUrl("http://10.0.2.2:8081/") // 멤버 서버 요청
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
            .baseUrl("http://10.0.2.2:8082/") // 레시피 서버 요청
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

    // 내가 북마크한 레시피 조회
    fun getAllMyBookmarkRecipeList() {

        viewModelScope.launch {
            currentRequestPage = 0
            val response = recipeMyPageService.getAllMyBookmarkRecipeList(currentRequestPage, currentRequestSize)

            // 성공적인 응답 처리 - items에 데이터 설정
            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else {
                // 오류 처리
                Log.e("MyPageViewModel", "Error in getAllMyBookmarkRecipeList: ${response.errorBody()}")
            }
        }
    }

    // 내가 좋아요한 레시피 조회
    fun getAllMyLikeRecipeList() {

        viewModelScope.launch {
            currentRequestPage = 0
            val response = recipeMyPageService.getAllMyLikeRecipeList(currentRequestPage, currentRequestSize)

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
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        viewModelScope.launch {
            val response = recipeMyPageService.getAllTargetMemberRecipeList(currentRequestPage, currentRequestSize, currentRequestSortType, targetMemberId)

            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else {
                // 오류 처리
                Log.e("MyPageViewModel", "Error in loadMoreTargetMemberRecipes: ${response.errorBody()}")
            }
            _isLoading.value = false
        }
    }

    // 내가 북마크한 레시피를 더 불러오는 함수
    fun loadMoreMyBookmarkRecipes() {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        viewModelScope.launch {
            val response = recipeMyPageService.getAllMyBookmarkRecipeList(currentRequestPage, currentRequestSize)

            if (response.isSuccessful) {
                val newItems = response.body()?.content ?: emptyList()
                items.value = items.value + newItems // 기존 리스트에 새 아이템들을 추가
                isLastPage = newItems.size < currentRequestSize
                currentRequestPage++
            } else {
                // 오류 처리
                Log.e("MyPageViewModel", "Error in loadMoreMyBookmarkRecipes: ${response.errorBody()}")
            }
            _isLoading.value = false
        }
    }

    // 내가 좋아요한 레시피를 더 불러오는 함수
    fun loadMoreMyLikeRecipes() {
        if (_isLoading.value == true || isLastPage) return

        _isLoading.value = true
        viewModelScope.launch {
            val response = recipeMyPageService.getAllMyLikeRecipeList(currentRequestPage, currentRequestSize)

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
                    // 에러 처리
                }
            } catch (e: Exception) {
                // 네트워크 오류 등의 예외 처리
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

    // jwt 관련정보 초기화
    private fun clearSession() {
        tokenManager.saveAccessToken("")
        tokenManager.saveRefreshToken("")
        tokenManager.saveMemberId(0)
    }

}
