package com.recipia.aos.ui.components.home

import TokenManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.recipia.aos.R
import kotlinx.coroutines.delay

/**
 * 첫 실행시 보일 로딩 화면
 */
@Composable
fun SplashScreen(
    navController: NavController,
    tokenManager: TokenManager
) {

    var isAnimationFinished by remember { mutableStateOf(false) }
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_anime))

    // jwt 존재 여부를 검증한다.
    val isUserLoggedIn = remember {
        mutableStateOf(tokenManager.hasValidAccessToken())
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "레시피 유토피아",
            color = Color(27, 94, 32),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
        )

        LottieAnimation(
            composition = composition,
            modifier = Modifier.size(300.dp),
            isPlaying = true, // 애니메이션 재생
            iterations = LottieConstants.IterateForever, // 무한 반복
            // 애니메이션 상태 추적
            speed = 2.4f,
            restartOnPlay = false
        )

        // 애니메이션이 로드되면 상태 업데이트 및 2초 대기 후 화면 전환
        LaunchedEffect(composition) {
            if (composition != null) {
                isAnimationFinished = true
                delay(1000) // 1초
                if (isUserLoggedIn.value) {
                    navController.navigate("home") { // 메인 화면으로 이동
                        popUpTo("splash-screen") { inclusive = true } // 스플래시 화면 제거
                    }
                } else {
                    navController.navigate("login") { // 로그인 화면으로 이동
                        popUpTo("splash-screen") { inclusive = true } // 스플래시 화면 제거
                    }
                }
            }
        }
    }
}