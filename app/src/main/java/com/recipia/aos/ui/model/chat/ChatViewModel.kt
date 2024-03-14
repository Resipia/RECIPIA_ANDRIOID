package com.recipia.aos.ui.model.chat

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.chat.ChatService
import com.recipia.aos.ui.api.dto.chat.ChatRoomDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatViewModel(
    private val tokenManager: TokenManager
): ViewModel() {

    // 채팅방 목록을 저장할 StateFlow
    private val _chatRoomsDto = MutableStateFlow<List<ChatRoomDto>?>(null)
    val chatRooms = _chatRoomsDto.asStateFlow()

    // 에러 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val chatService: ChatService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 클라이언트에서 보낼 요청을 생성 (여기서 jwt를 헤더에 추가해줌)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                // JwtTokenManager를 사용하여 토큰을 요청 헤더에 추가
                val request = tokenManager.addAccessTokenToHeader(chain)
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.CHAT_SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatService::class.java)
    }

    // 채팅방 목록 조회
    fun getUserChatRooms(memberId: String) {
        viewModelScope.launch {
            try {

                // 로딩 상태 업데이트 예시
                _isLoading.value = true
                val response = chatService.getUserChatRooms(memberId)
                _isLoading.value = false

                if (response.isSuccessful) {
                    _chatRoomsDto.value = response.body()
                } else {
                    // 에러 처리
                    _errorMessage.value = "채팅방을 불러오는 데 실패했습니다."
                }
            } catch (e: Exception) {
                // 예외 처리
            }
        }
    }

    // 채팅방 생성
    fun createChatRoom(creatorId: String, participantId: String) {
        viewModelScope.launch {
            try {

                // 로딩 상태 업데이트 예시
                _isLoading.value = true
                val response = chatService.createChatRoom(creatorId, participantId)
                _isLoading.value = false

                if (response.isSuccessful) {
                    // 생성된 채팅방 처리
                } else {
                    // 에러 처리
                    _errorMessage.value = "채팅방을 불러오는 데 실패했습니다."
                }
            } catch (e: Exception) {
                // 예외 처리
            }
        }
    }
}