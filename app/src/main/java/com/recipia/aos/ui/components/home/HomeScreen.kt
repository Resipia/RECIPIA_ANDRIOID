package com.recipia.aos.ui.components.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.R
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import com.recipia.aos.ui.model.RecipeAllListViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: RecipeAllListViewModel,
    innerPadding: PaddingValues
) {

    val items by viewModel.items.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val loadFailed by viewModel.loadFailed.observeAsState(initial = false)
    val context = LocalContext.current

    if (loadFailed) {
        Toast.makeText(context, "데이터 로딩 실패", Toast.LENGTH_SHORT).show()
        viewModel.resetLoadFailed() // 경고창을 한 번만 표시하도록 상태를 리셋
    }

    // 화면이 렌더링될 때 데이터 로딩 시작
    LaunchedEffect(key1 = true) {
        viewModel.loadMoreItems()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create-recipe") },
                modifier = Modifier.offset(y = (-70).dp) // 100dp만큼 위로 오프셋
            ) {
                Icon(Icons.Filled.Add, "글쓰기")
            }
        },
        content = {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding() + 80.dp, // 여기에서 추가 패딩을 적용합니다
                    top = innerPadding.calculateTopPadding()
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, item ->
                    ListItem(item = item)

                    // 마지막 아이템에 도달했을 때 추가 데이터 로드
                    if (index == items.lastIndex && !viewModel.isLastPage && !isLoading) {
                        viewModel.loadMoreItems()
                    }
                }

                // 로딩 중이라면 로딩 인디케이터를 표시
                if (isLoading) {
                    item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                }
            }
        }
    )


}

@Composable
fun ListItem(item: RecipeMainListResponseDto) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your image resource
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
//                    .align(Alignment.CenterVertically)
                    .weight(1f)  // 칼럼이 차지하는 공간을 유동적으로 조정
            ) {
                Text(
                    text = item.recipeName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.nickname,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // 서브 카테고리 Assist Chips
                Row(modifier = Modifier.padding(top = 8.dp)) {
                    item.subCategoryList.take(3).forEach { subCategory ->
                        AssistChip(
                            onClick = { /* 서브 카테고리 선택 또는 해제 로직 */ },
                            label = { Text(subCategory.subCategoryNm ?: "", fontSize = 10.sp) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }

            // 북마크 아이콘
            IconButton(
                onClick = {
                          /* 북마크 클릭 처리 로직 */
                },
                modifier = Modifier.align(Alignment.CenterVertically)  // 아이콘을 세로 중앙에 위치시킴
            ) {
                val icon = if (item.bookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder
                val tint = if (item.bookmarked) MaterialTheme.colorScheme.primary else Color.Gray

                Icon(
                    imageVector = icon,
                    contentDescription = "즐겨찾기",
                    tint = tint
                )
            }
        }
    }
}
