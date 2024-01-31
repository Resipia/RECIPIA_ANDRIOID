package com.recipia.aos.ui.components.mypage.function.ask

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.dto.mypage.ask.AskListResponseDto
import com.recipia.aos.ui.model.mypage.ask.AskViewModel
import kotlinx.coroutines.launch

/**
 * 문의하기 페이지 (무한스크롤 리스트)
 */
@Composable
fun AskListPageScreen(
    navController: NavController,
    askViewModel: AskViewModel
) {
    val askItems by askViewModel.askItems.observeAsState(emptyList())
    val lazyListState = rememberLazyListState()

    // 초기 데이터 로딩
    LaunchedEffect(Unit) {
        askViewModel.loadFirstInitItems()
    }

    Scaffold(
        containerColor = Color.White,
        topBar = { TopBar(navController) },
        bottomBar = {
            Column {
                Divider(
                    color = Color(222, 226, 230),
                    thickness = 1.dp,  // 구분선 두께 설정
                )
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            if (askItems.isEmpty()) {
                EmptyAskItemsMessage()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    state = lazyListState
                ) {
                    itemsIndexed(askItems) { index, askItem ->
                        AskItemView(askItem, navController)

                        // 각 항목 아래에 구분선 추가
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            thickness = 0.5.dp,
                            color = Color(222, 226, 230)
                        )

                        if (index == askItems.lastIndex && !askViewModel.isLastPage) {
                            askViewModel.loadMoreItems()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    TopAppBar(
        modifier = Modifier.background(Color.White),
        title = {
            Text(
                text = "고객 문의/피드백",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}


@Composable
fun BottomBar(
    navController: NavController
) {
    Button(
        onClick = {
            navController.navigate("ask-create")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(27, 94, 32))
    ) {
        Text(
            "문의/피드백 작성",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun EmptyAskItemsMessage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize() // 화면 전체를 채우도록 설정
            .padding(16.dp)
    ) {
        Text(
            text = "작성된 문의/피드백 사항이 없습니다.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AskItemView(
    askItem: AskListResponseDto,
    navController: NavController
) {

    Row(
        modifier = Modifier
            .padding(8.dp)
            .clickable { /* 문의사항 상세 화면으로 이동 로직 */ }
            .fillMaxWidth()
            .heightIn(min = 52.dp) // 최소 높이 설정
            .padding(vertical = 8.dp) // 내부 상하 패딩 추가
            .clickable {
                navController.navigate("askDetail/${askItem.id}")
            }
    ) {
        // 답변 여부
        Text(
            text = if (askItem.answerYn) "[답변 완료]" else "[답변 대기]",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = 8.dp) // 답변 여부와 제목 사이의 간격 추가
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 제목
        Text(
            text = askItem.title,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )

        // 생성일자
        Text(
            text = askItem.createDate,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 8.dp) // 제목과 생성일자 사이의 간격 추가
        )
    }
}