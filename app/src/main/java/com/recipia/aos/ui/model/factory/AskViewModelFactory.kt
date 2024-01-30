package com.recipia.aos.ui.model.factory

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.model.mypage.ask.AskViewModel

/**
 * 문의하기 팩토리
 */
class AskViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AskViewModel::class.java)) {
            return AskViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}