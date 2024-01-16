package com.recipia.aos

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.MediumTopAppBarExample
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import com.recipia.aos.ui.model.MyViewModel
import com.recipia.aos.ui.theme.RecipiaaosTheme

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            RecipiaaosTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    MediumTopAppBarExample()
//                }
//            }
//        }
//    }
//}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipiaaosTheme {
                val viewModel = viewModel<MyViewModel>() // ViewModel 인스턴스

                Scaffold(
                    topBar = { MediumTopAppBarExample(viewModel) }, // 상단 바
                    bottomBar = { BottomNavigationBar() } // 하단 네비게이션 바
                ) { innerPadding ->
                    // 주요 콘텐츠 영역
                    Surface(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // 여기에 주요 콘텐츠를 배치
                        ItemListWithInfiniteScroll(viewModel, Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun ItemListWithInfiniteScroll(viewModel: MyViewModel = MyViewModel(), modifier: Modifier = Modifier) {
    val items by viewModel.items.observeAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val loadFailed by viewModel.loadFailed.observeAsState(initial = false)

    if (loadFailed) {
        Toast.makeText(LocalContext.current, "데이터 로딩 실패", Toast.LENGTH_SHORT).show()
        viewModel.resetLoadFailed() // 경고창을 한 번만 표시하도록 상태를 리셋
    }

    LazyColumn {
        itemsIndexed(items) { index, item ->
            ListItem(item = item)

            // 마지막 아이템에 도달했을 때 추가 데이터 로드
            if (index == items.size - 1 && !viewModel.isLastPage) {
                viewModel.loadMoreItems()
            }
        }

        // 로딩 중이라면 로딩 인디케이터를 표시
        if (isLoading) {
            item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
        }
    }
}

@Composable
fun ListItem(item: RecipeMainListResponseDto) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
//        Image(
//            painter = rememberImagePainter(data = item.subCategoryList.get(0).subCategoryNm),
//            contentDescription = "Recipe Image",
//            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))
//        )
        Column(modifier = Modifier
            .padding(start = 16.dp)
            .align(Alignment.CenterVertically)) {
            Text(item.recipeName, style = MaterialTheme.typography.titleSmall)
            Text(item.nickname, style = MaterialTheme.typography.bodyMedium)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecipiaaosTheme {
        ItemListWithInfiniteScroll(MyViewModel())
    }
}