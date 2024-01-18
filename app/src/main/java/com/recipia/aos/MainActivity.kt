package com.recipia.aos

import TokenManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.recipia.aos.ui.components.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val tokenManager = TokenManager(this.applicationContext)

            setContent {
                AppNavigation(tokenManager)
            }
        }
    }
}
