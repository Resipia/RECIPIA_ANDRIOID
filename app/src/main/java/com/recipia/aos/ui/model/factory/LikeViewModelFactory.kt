package com.recipia.aos.ui.model.factory

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.model.recipe.like.LikeViewModel

/**
 * 좋아요 view 모델 생성 팩토리
 */
class LikeViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LikeViewModel::class.java)) {
            return LikeViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}