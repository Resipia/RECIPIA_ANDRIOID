package com.recipia.aos.ui.model.factory

import TokenManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recipia.aos.ui.model.mypage.MyPageViewModel

class MyPageViewModelFactory(
    private val tokenManager: TokenManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}