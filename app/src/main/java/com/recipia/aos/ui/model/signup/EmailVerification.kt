package com.recipia.aos.ui.model.signup

import androidx.lifecycle.ViewModel

class EmailVerificationViewModel : ViewModel() {
    var email: String = ""
    var isEmailVerified: Boolean = false
}