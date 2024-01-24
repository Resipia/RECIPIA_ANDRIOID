package com.recipia.aos.ui.components.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.model.search.MongoSearchViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MongoSearchScreen(
    navController: NavController,
    viewModel: MongoSearchViewModel
) {

    val searchText by viewModel.searchText.collectAsState()
    val mongoSearchResults by viewModel.mongoSearchResults.collectAsState()
    val selectedSearchResults by viewModel.selectedSearchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "검색") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            TextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 선택된 검색 결과들을 AssistChip으로 표시
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalArrangement = Arrangement.Top,
                maxItemsInEachRow = Int.MAX_VALUE
            ) {
                selectedSearchResults.forEach { result ->
                    AssistChip(
                        onClick = { /* 첫 번째 AssistChip 클릭 시 동작 */ },
                        label = {
                            Text(
                                result,
                                fontSize = 12.sp, // 글씨 크기 조절
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(238,238,238),
                            labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                        ),
//                        elevation = null, // 그림자 제거
//                        border = null, // 테두리 제거
                    )
                    Spacer(modifier = Modifier.width(6.dp)) // 여백 추가
                }
            }

            LazyColumn {
                items(mongoSearchResults) { hashtag ->
                    Text(
                        text = hashtag,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                viewModel.onSearchResultClick(hashtag)
                            }
                    )
                    // 항목 사이에 구분선 추가
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color.Gray // 구분선의 색상 설정
                    )
                }
            }
        }
    }

}
