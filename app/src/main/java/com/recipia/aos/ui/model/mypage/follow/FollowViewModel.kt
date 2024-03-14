package com.recipia.aos.ui.model.mypage.follow

import TokenManager
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.mypage.FollowService
import com.recipia.aos.ui.api.dto.mypage.follow.FollowListResponseDto
import com.recipia.aos.ui.api.dto.mypage.follow.FollowRequestDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 팔로잉/팔로워 모델
 */
class FollowViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 팔로잉과 팔로워 목록을 저장할 리스트
    var followList = mutableStateListOf<com.recipia.aos.ui.api.dto.mypage.follow.FollowListResponseDto>()

    // 현재 페이지와 목록 유형을 저장 (following or follower)
    private var currentPage = 0
    private var currentType = "following"

    // 로그인 화면으로 이동해야 함을 알린다.
    val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    // 팔로우/언팔로우 작업의 결과를 관찰하기 위한 StateFlow
    private val _followResult = MutableStateFlow<Pair<Boolean, Long?>?>(null)
    val followResult = _followResult.asStateFlow()

    // 북마크 요청 refrofit 설정 (로깅 인터셉터 추가)
    private val followService: FollowService by lazy {
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
            .baseUrl(BuildConfig.MEMBER_SERVER_URL) // 멤버 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(FollowService::class.java)
    }

    // 지정된 유형(following 또는 follower)에 따라 팔로잉/팔로워 목록 데이터를 로드한다.
    fun loadFollowList(
        targetMemberId: Long,
        type: String
    ) {
        currentType = type
        currentPage = 0
        followList.clear()
        fetchFollowList(targetMemberId)
    }

    // 이 함수는 팔로잉 팔로워 리스트를 실제로 호출해서 받아온 후 followList에 값을 세팅한다.
    private fun fetchFollowList(
        targetMemberId: Long
    ) {
        viewModelScope.launch {
            val response = followService.getFollowList(currentPage, 10, targetMemberId, currentType)
            if (response.isSuccessful && response.body() != null) {
                followList.addAll(response.body()!!.content)
            } else if (response.code() == 401) {
                handleUnauthorizedError { fetchFollowList(targetMemberId) }
            }
        }
    }

    //  무한 스크롤 구현을 위해 추가 데이터를 로드한다.
    fun loadMore(
        targetMemberId: Long
    ) {
        currentPage++
        fetchFollowList(targetMemberId)
    }

    // 팔로잉/팔로워 목록 업데이트
    fun updateFollowListItem(index: Int, updatedItem: com.recipia.aos.ui.api.dto.mypage.follow.FollowListResponseDto) {
        if (index >= 0 && index < followList.size) {
            followList[index] = updatedItem
        }
    }

    // 마지막으로 팔로우/언팔로우 요청이 발생한 memberId를 임시 저장
    var lastRequestedMemberId: Long? = null

    // 팔로우/언팔로우 요청 (응답이 1이면 언팔로우, 1보다 크면 팔로우한 pk 반환)
    fun followOrUnfollow(
        targetMemberId: Long
    ) {
        lastRequestedMemberId = targetMemberId // 요청한 memberId 저장
        viewModelScope.launch {
            // 응답 받기
            val response = followService.followOrUnFollow(
                com.recipia.aos.ui.api.dto.mypage.follow.FollowRequestDto(
                    targetMemberId
                )
            )
            // 성공했을때 동작
            if (response.isSuccessful && response.body() != null) {
                val result = response.body()!!.result
                _followResult.value = Pair(true, if (result > 1) result else null)
            } else if (response.code() == 401) {
                handleUnauthorizedError { followOrUnfollow(targetMemberId) }
            } else {
                _followResult.value = Pair(false, null)
            }
        }
    }

    // 팔로우 결과 초기화
    fun resetFollowResult() {
        _followResult.value = null
    }

    // 팔로잉과 팔로워 목록을 초기화하는 함수
    fun clearFollowList() {
        followList.clear()
    }

    // 홈 화면 이동 초기화
    fun resetNavigateToLogin() {
        _navigateToLogin.value = false
    }

    /**
     * 401 Unauthorized 에러 처리 및 토큰 재발급 로직
     */
    private fun handleUnauthorizedError(
        retryAction: () -> Unit
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

}