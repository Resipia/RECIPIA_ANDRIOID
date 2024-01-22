package com.recipia.aos.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoScrollingSlider(
    images: List<String>
) {

    // 이미지 슬라이더 구현
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        5
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(4000) // 3초마다
            val nextPage = (pagerState.currentPage + 1) % images.size
            coroutineScope.launch { pagerState.animateScrollToPage(nextPage) }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 17.dp) // 좌우 패딩 추가
            .clip(RoundedCornerShape(4.dp)) // 모서리 둥글게
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp)) // 연한 테두리 추가
    ) { page ->
        Image(
            painter = rememberAsyncImagePainter(model = images[page]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}