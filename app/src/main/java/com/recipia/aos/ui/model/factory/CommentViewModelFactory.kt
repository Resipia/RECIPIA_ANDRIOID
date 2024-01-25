package com.recipia.aos.ui.model.factory

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.model.comment.CommentViewModel

/**
 * 댓글 view 모델 생성 팩토리
 */
class CommentViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            return CommentViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}