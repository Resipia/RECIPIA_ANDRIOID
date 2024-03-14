package com.recipia.aos.ui.model.comment

import TokenManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.BuildConfig
import com.recipia.aos.ui.api.recipe.CommentService
import com.recipia.aos.ui.api.dto.PagingResponseDto
import com.recipia.aos.ui.api.dto.comment.CommentDeleteRequestDto
import com.recipia.aos.ui.api.dto.comment.CommentListResponseDto
import com.recipia.aos.ui.api.dto.comment.CommentRegistRequestDto
import com.recipia.aos.ui.api.dto.comment.CommentUpdateRequestDto
import com.recipia.aos.ui.model.jwt.TokenRepublishManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 댓글 view 모델
 */
class CommentViewModel(
    private val tokenManager: TokenManager
) : ViewModel() {

    // 댓글 데이터를 저장할 StateFlow
    private val _comments = MutableStateFlow<com.recipia.aos.ui.api.dto.PagingResponseDto<com.recipia.aos.ui.api.dto.comment.CommentListResponseDto>?>(null)
    val comments = _comments.asStateFlow()

    // 에러 메시지를 위한 상태
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // 현재 수정 중인 댓글 상태
    private val _editingComment = MutableStateFlow<Pair<Long, String>?>(null)
    val editingComment = _editingComment.asStateFlow()

    // 로그인 화면으로 이동해야 함을 알린다.
    val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    private var currentPage = 0
    private var isCommentsLoading = false
    private var initialLoadDone = false
    var isLastPage = false

    // retrofit 설정
    private val commentService: CommentService by lazy {
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
            .baseUrl(BuildConfig.RECIPE_SERVER_URL) // 레시피 서버
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CommentService::class.java)
    }


    // 첫 페이지 댓글 불러오기
    suspend fun loadInitialComments(recipeId: Long) {
        if (!initialLoadDone) {
            viewModelScope.launch {
                val response = commentService.getAllCommentList(recipeId, currentPage, 10, "new")
                if (response.isSuccessful) {
                    if (currentPage == 0) {
                        _comments.value = response.body()
                        currentPage++ // 첫 페이지 로드 후 페이지 번호 증가
                    }
                } else if (response.code() == 401) {
                    handleUnauthorizedError {
                        loadInitialComments(recipeId) // 토큰 재발급 후 재시도
                    }
                } else {
                    _comments.value = null
                }
            }
            initialLoadDone = true
        }
    }

    // 추가 페이지 댓글 불러오기
    fun loadMoreComments(recipeId: Long) {
        // 이미 로딩 중이거나 마지막 페이지인 경우 추가 로드를 시도하지 않음
        if (isCommentsLoading || isLastPage) return

        isCommentsLoading = true

        viewModelScope.launch {
            val response = commentService.getAllCommentList(recipeId, currentPage, 10, "new")

            when {
                response.isSuccessful -> {
                    // 성공적으로 데이터를 받아왔을 경우 처리
                    val newComments = response.body()?.content ?: emptyList()
                    if (newComments.isNotEmpty()) {
                        // 새로운 댓글이 있는 경우, 기존 댓글 목록에 추가
                        val updatedComments = _comments.value?.content.orEmpty() + newComments
                        _comments.value = com.recipia.aos.ui.api.dto.PagingResponseDto(
                            content = updatedComments,
                            totalCount = response.body()?.totalCount
                                ?: updatedComments.size.toLong()
                        )
                        // 다음 페이지 준비
                        currentPage++
                        // 마지막 페이지 확인
                        isLastPage = newComments.size < 10
                    } else {
                        // 새로운 댓글이 없는 경우, 마지막 페이지로 처리
                        isLastPage = true
                    }
                }
                response.code() == 401 -> {
                    // 401 Unauthorized 에러 처리
                    handleUnauthorizedError { loadMoreComments(recipeId) }
                }
                else -> {
                    // 기타 오류 처리
                    _errorMessage.value = "댓글을 불러오는 중 오류가 발생했습니다. 오류 코드: ${response.code()}"
                }
            }

            isCommentsLoading = false
        }
    }

    // 댓글 등록
    suspend fun addComment(
        recipeId: Long,
        commentText: String
    ) {
        viewModelScope.launch {
            val response = commentService.registComment(
                com.recipia.aos.ui.api.dto.comment.CommentRegistRequestDto(
                    recipeId,
                    commentText
                )
            )
            if (response.isSuccessful) {
                // 상태를 강제로 초기화하여 변경을 감지하게 만듦
                _comments.value = null
                // 현재 페이지를 0으로 리셋하고 초기 댓글 목록을 다시 불러옴
                currentPage = 0
                initialLoadDone = false
                loadInitialComments(recipeId) // 댓글 목록을 다시 불러오기
            } else if (response.code() == 401) {
                handleUnauthorizedError { addComment(recipeId, commentText) }
            } else {
                _errorMessage.value = "댓글 등록에 실패했습니다."
            }
        }
    }

    // 댓글 수정
    suspend fun updateComment(
        id: Long, // 댓글의 id
        recipeId: Long, // 댓글이 작성된 recipe의 id
        commentText: String // 수정할 text){}
    ) {
        viewModelScope.launch {
            val response =
                commentService.updateComment(
                    com.recipia.aos.ui.api.dto.comment.CommentUpdateRequestDto(
                        id,
                        recipeId,
                        commentText
                    )
                )
            if (response.isSuccessful) {
                // 기존 댓글 목록에서 수정된 댓글 찾아서 업데이트하고, updated 플래그를 true로 설정
                val updatedList = _comments.value?.content?.map { comment ->
                    if (comment.id == id) comment.copy(
                        commentValue = commentText,
                        updated = true
                    ) else comment
                }
                _comments.value = _comments.value?.copy(content = updatedList.orEmpty())
            } else if (response.code() == 401) {
                handleUnauthorizedError { updateComment(id, recipeId, commentText) }
            } else {
                _errorMessage.value = "댓글 수정에 실패했습니다."
            }
        }
    }


    // 댓글 삭제 요청 처리
    fun requestDeleteComment(id: Long, recipeId: Long) {
        viewModelScope.launch {
            deleteComment(id, recipeId)
        }
    }

    // 댓글 삭제
    suspend fun deleteComment(
        id: Long,
        recipeId: Long
    ) {
        viewModelScope.launch {
            val response = commentService.deleteComment(
                com.recipia.aos.ui.api.dto.comment.CommentDeleteRequestDto(
                    id,
                    recipeId
                )
            )
            if (response.isSuccessful) {
                // 상태를 강제로 초기화하여 변경을 감지하게 만듦
                _comments.value = null
                // 현재 페이지를 0으로 리셋하고 초기 댓글 목록을 다시 불러옴
                currentPage = 0
                isLastPage = false
                initialLoadDone = false
                loadInitialComments(recipeId) // 댓글 목록을 다시 불러오기
            } else if (response.code() == 401) {
                handleUnauthorizedError { deleteComment(id, recipeId) }
            } else {
                _errorMessage.value = "댓글 삭제에 실패했습니다."
            }
        }
    }


    // 댓글 수정 시작
    fun startEditingComment(id: Long, commentValue: String) {
        _editingComment.value = id to commentValue
    }

    // 댓글 수정 취소 또는 완료
    fun clearEditingComment() {
        _editingComment.value = null
    }

    // 댓글 목록 초기화 함수
    fun clearComments() {
        _comments.value = null
        currentPage = 0
        initialLoadDone = false
    }

    // 에러 메시지 초기화
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 홈화면 이동 초기화 함수
    fun resetNavigateToLogin() {
        _navigateToLogin.value = false
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

}