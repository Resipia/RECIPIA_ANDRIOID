package com.recipia.aos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.recipia.aos.ui.components.navigation.AppNavigation
import com.recipia.aos.ui.jwt.JwtTokenManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val jwtTokenManager = JwtTokenManager(this.applicationContext)

            setContent {
                AppNavigation(jwtTokenManager)
            }
        }
    }
}
