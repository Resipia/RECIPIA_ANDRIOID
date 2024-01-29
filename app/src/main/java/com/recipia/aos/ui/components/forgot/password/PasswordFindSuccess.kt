package com.recipia.aos.ui.components.forgot.password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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


@Composable
fun PasswordFindSuccessScreen(
    navController: NavController
) {
    // Lottie 애니메이션 상태 관리
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.password_find_success))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 성공 메시지 표시 ,
        Text(
            text = "임시 비밀번호 재발급에 성공했습니다.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "이메일을 확인해 주세요. (조금 시간이 걸릴 수 있습니다.)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lottie 애니메이션 표시
        LottieAnimation(
            composition = composition,
            isPlaying = true,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp),
            // 애니메이션 상태 추적
            speed = 0.6f,
            restartOnPlay = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "로그인 하기" 버튼
        Button(
            onClick = {
                navController.navigate("login")
            },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(27, 94, 32),
                contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
            ),
            modifier = Modifier
                .height(56.dp) // 높이 지정
        ) {
            Text("로그인 하기", fontWeight = FontWeight.Bold)
        }
    }
}
