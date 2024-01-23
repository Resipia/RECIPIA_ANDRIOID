package com.recipia.aos.ui.model.mypage.follow

import TokenManager
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.FollowService
import com.recipia.aos.ui.dto.mypage.follow.FollowListResponseDto
import com.recipia.aos.ui.dto.mypage.follow.FollowRequestDto
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
): ViewModel() {

    // 팔로잉과 팔로워 목록을 저장할 리스트
    var followList = mutableStateListOf<FollowListResponseDto>()

    // 현재 페이지와 목록 유형을 저장 (following or follower)
    private var currentPage = 0
    private var currentType = "following"

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
            .baseUrl("http://10.0.2.2:8081/") // 멤버 서버 요청
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(FollowService::class.java)
    }

    // 팔로잉/팔로워 목록을 로드하는 함수
    fun loadFollowList(targetMemberId: Long) {
        viewModelScope.launch {

            // Retrofit 서비스를 이용하여 데이터 로드
            val response = followService.getFollowList(currentPage, 10, targetMemberId, currentType)

            if (response.isSuccessful && response.body() != null) {
                // 받아온 데이터를 followList에 추가
                followList.addAll(response.body()!!.content)
            }
        }
    }

    // 타입 변경 시 호출되는 함수
    fun changeType(type: String, targetMemberId: Long) {
        if (currentType != type) {
            currentType = type
            currentPage = 0
            followList.clear()
            loadFollowList(targetMemberId)
        }
    }

    // 무한 스크롤을 위한 추가 데이터 로딩 함수
    fun loadMore(targetMemberId: Long) {
        currentPage++
        loadFollowList(targetMemberId)
    }

    // 팔로우/언팔로우 요청 (응답이 1이면 언팔로우, 1보다 크면 팔로우한 pk 반환)
    fun followOrUnfollow(
        targetMemberId: Long,
        onResult: (Boolean, Long?) -> Unit
    ) {
        viewModelScope.launch {
            // 서버에 팔로우/언팔로우 요청을 보냄
            val response = followService.followOrUnFollow(FollowRequestDto(targetMemberId))

            if (response.isSuccessful && response.body() != null) {
                // 서버로부터 응답 받음
                val result = response.body()!!.result
                // 응답이 1보다 크면 (팔로우한 경우), 결과값으로 followId 반환
                // 그렇지 않으면 (언팔로우한 경우), null 반환
                onResult(true, if (result > 1) result else null)
            } else {
                // 요청 실패 시, false와 null 반환
                onResult(false, null)
            }
        }
    }

}