package com.recipia.aos.ui.components.mypage.function.ask

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

/**
 * 문의하기 성공 페이지
 */
@Composable
fun AskSuccessScreen(
    navController: NavController
) {
    // Lottie 애니메이션 상태 관리
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.ask_success))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // 성공 메시지 표시 ,
        Text(
            text = "문의/피드백 전달완료.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // 성공 메시지 표시 ,
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "꼼꼼히 검토한 후 만족스러우실 답변을 드릴 수 있도록 노력하겠습니다.",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lottie 애니메이션 표시
        LottieAnimation(
            composition = composition,
            isPlaying = true,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp),
            // 애니메이션 상태 추적
            speed = 0.8f,
            restartOnPlay = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 버튼 영역
        Row {
            // 메인 메뉴 이동
            Button(
                onClick = {
                    navController.navigate("home")
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(27, 94, 32),
                    contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                ),
                modifier = Modifier
                    .height(56.dp) // 높이 지정
                    .weight(1f)
            ) {
                Text("홈 화면", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(8.dp)) // Text 사이의 간격 추가

            // 마이페이지로 이동
            Button(
                onClick = {
                    navController.navigate("ask-list")
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(27, 94, 32),
                    contentColor = Color.White // 버튼 내용(텍스트 등)의 색상 설정
                ),
                modifier = Modifier
                    .height(56.dp) // 높이 지정
                    .weight(1f)
            ) {
                Text("문의 목록", fontWeight = FontWeight.Bold)
            }
        }

    }
}
