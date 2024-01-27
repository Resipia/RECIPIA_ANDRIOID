package com.recipia.aos.ui.model.comment

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.recipia.aos.ui.api.recipe.CommentService
import com.recipia.aos.ui.dto.PagingResponseDto
import com.recipia.aos.ui.dto.comment.CommentDeleteRequestDto
import com.recipia.aos.ui.dto.comment.CommentListResponseDto
import com.recipia.aos.ui.dto.comment.CommentRegistRequestDto
import com.recipia.aos.ui.dto.comment.CommentUpdateRequestDto
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
    private val _comments = MutableStateFlow<PagingResponseDto<CommentListResponseDto>?>(null)
    val comments = _comments.asStateFlow()

    // 에러 메시지를 위한 상태
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // 현재 수정 중인 댓글 상태
    private val _editingComment = MutableStateFlow<Pair<Long, String>?>(null)
    val editingComment = _editingComment.asStateFlow()


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
            .baseUrl("http://10.0.2.2:8082/") // 레시피 서버
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(CommentService::class.java)
    }

    // 첫 페이지 댓글 불러오기
    suspend fun loadInitialComments(recipeId: Long) {
        if (!initialLoadDone) {
            getAllComments(recipeId, currentPage)
            currentPage++ // 첫 페이지 로드 후 페이지 번호 증가
            initialLoadDone = true
        }
    }

    // 댓글 콘텐츠를 불러온다.
    suspend fun getAllComments(
        recipeId: Long,
        currentPage: Int,
        size: Int = 5,
        sortType: String = "new"
    ): PagingResponseDto<CommentListResponseDto>? {
        val response = commentService.getAllCommentList(recipeId, currentPage, size, sortType)
        return if (response.isSuccessful) {
            if (currentPage == 0) {
                _comments.value = response.body()
            }
            response.body()
        } else {
            _comments.value = null
            null
        }
    }

    // 추가 페이지 댓글 불러오기
    suspend fun loadMoreComments(recipeId: Long) {

        if (!isCommentsLoading && !isLastPage) {

            isCommentsLoading = true
            val newComments = getAllComments(recipeId, currentPage)

            newComments?.let { newCommentList ->
                if (newCommentList.content.isNotEmpty()) {

                    val existingComments = _comments.value?.content ?: listOf()
                    val updatedComments = existingComments + newCommentList.content

                    _comments.value = PagingResponseDto(
                        content = updatedComments,
                        totalCount = newCommentList.totalCount
                    )
                    currentPage++ // 새 페이지 로드 후 페이지 번호 증가
                }
                isCommentsLoading = false
            } ?: run {
                isCommentsLoading = false
            }
        }
    }

    // 댓글 등록
    suspend fun addComment(
        recipeId: Long,
        commentText: String
    ) {
        try {
            val requestDto = CommentRegistRequestDto(recipeId, commentText)
            val response = commentService.registComment(requestDto)
            if (response.isSuccessful) {
                loadInitialComments(recipeId)
            } else {
                _errorMessage.value = "댓글 등록에 실패했습니다."
            }
        } catch (e: Exception) {
            _errorMessage.value = "네트워크 에러: ${e.localizedMessage}"
        }
    }

    // 댓글 수정
    suspend fun updateComment(
        id: Long, // 댓글의 id
        commentText: String // 수정할 text
    ) {
        try {
            val requestDto = CommentUpdateRequestDto(id, commentText)
            val response = commentService.updateComment(requestDto)
            if (response.isSuccessful) {
                loadInitialComments(id) // 댓글 목록을 다시 불러오기
            } else {
                _errorMessage.value = "댓글 수정에 실패했습니다."
            }
        } catch (e: Exception) {
            _errorMessage.value = "네트워크 에러: ${e.localizedMessage}"
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
        id: Long, // 댓글의 id
        recipeId: Long // 삭제하려는 레시피 id
    ) {
        try {
            val requestDto = CommentDeleteRequestDto(id, recipeId)
            val response = commentService.deleteComment(requestDto)
            if (response.isSuccessful) {
                // 상태를 강제로 초기화하여 변경을 감지하게 만듦
                _comments.value = null
                // 현재 페이지를 0으로 리셋하고 초기 댓글 목록을 다시 불러옴
                currentPage = 0
                initialLoadDone = false
                loadInitialComments(recipeId) // 댓글 목록을 다시 불러오기
            } else {
                _errorMessage.value = "댓글 삭제에 실패했습니다."
            }
        } catch (e: Exception) {
            _errorMessage.value = "네트워크 에러: ${e.localizedMessage}"
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

}