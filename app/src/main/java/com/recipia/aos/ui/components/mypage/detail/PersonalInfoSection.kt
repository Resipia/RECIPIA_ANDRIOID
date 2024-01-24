package com.recipia.aos.ui.components.mypage.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.model.mypage.MyPageViewModel

/**
 * 마이페이지 생년 월/일
 */
@Composable
fun PersonalInfoSection(
    myPageViewModel: MyPageViewModel
) {

    val textColor = Color.Black
    val myPageData = myPageViewModel.myPageData.value

    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 20.dp) // 'end' 추가하여 좌우 패딩 설정
    ) {
        if (myPageData != null) {
            Text(text = "생년월일: ${myPageData.birth}", color = textColor)
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (myPageData != null) {
            Text(text = "성별: ${myPageData.gender}", color = textColor)
        }
    }
}