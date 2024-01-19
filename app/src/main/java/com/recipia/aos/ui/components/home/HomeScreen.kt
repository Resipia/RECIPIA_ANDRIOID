package com.recipia.aos.ui.components.home

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookmarkUpdateState
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    recipeAllListViewModel: RecipeAllListViewModel,
    bookmarkViewModel: BookMarkViewModel,
    innerPadding: PaddingValues
) {
    /**
     * LiveData에 주로 observeAsState를 사용한다.
     * observeAsState를 사용하면, LiveData가 노출하는 데이터가 변경될 때 Composable 함수가 자동으로 다시 호출되어 UI가 업데이트되는 구조다.
     */
    // items 상태를 직접 사용
    val isLoading by recipeAllListViewModel.isLoading.observeAsState(initial = false)
    val loadFailed by recipeAllListViewModel.loadFailed.observeAsState(initial = false)
    val navigateToLogin by recipeAllListViewModel.navigateToLogin.observeAsState(initial = false)
    val toastMessage by bookmarkViewModel.toastMessage.observeAsState()
    val context = LocalContext.current

    // 북마크 상태 변경 감지 및 UI 업데이트
    val bookmarkUpdateState by bookmarkViewModel.bookmarkUpdateState.observeAsState()
    LaunchedEffect(bookmarkUpdateState) {
        bookmarkUpdateState?.let { state ->
            when (state) {
                is BookmarkUpdateState.Added -> {
                    recipeAllListViewModel.updateItemBookmarkId(state.recipeId, state.bookmarkId)
                }
                is BookmarkUpdateState.Removed -> {
                    recipeAllListViewModel.updateItemBookmarkId(state.recipeId, null)
                }
            }
            bookmarkViewModel.resetBookmarkUpdateState()
        }
    }

    // 토스트 메시지를 찾아서 띄우고 초기화 진행
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        bookmarkViewModel.toastMessage.value = null
    }

    if (loadFailed) {
        Toast.makeText(context, "데이터 로딩 실패", Toast.LENGTH_SHORT).show()
        recipeAllListViewModel.resetLoadFailed() // 경고창을 한 번만 표시하도록 상태를 리셋
    }

    // 화면이 렌더링될 때 데이터 로딩 시작
    LaunchedEffect(key1 = true) {
        recipeAllListViewModel.loadMoreItems()
    }

    // navigateToLogin 상태가 변경되었을 때 로그인 화면으로 이동
    if (navigateToLogin) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate("login")
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create-recipe") },
                modifier = Modifier.offset(y = (-70).dp) // 100dp만큼 위로 오프셋
            ) {
                Icon(Icons.Filled.Add, "글쓰기")
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                bottom = innerPadding.calculateBottomPadding() + 80.dp, // 추가 패딩 적용
                top = innerPadding.calculateTopPadding()
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            // recipeAllListViewModel.items.value를 사용
            itemsIndexed(recipeAllListViewModel.items.value) { index, item ->
                // State 객체로 변환된 bookmarkUpdateState를 ListItem에 전달
                ListItem(item, bookmarkViewModel)
                // 마지막 아이템에 도달했을 때 추가 데이터 로드
                if (index == recipeAllListViewModel.items.value.lastIndex && !recipeAllListViewModel.isLastPage && !isLoading) {
                    recipeAllListViewModel.loadMoreItems()
                }
            }

            // 로딩 중이라면 로딩 인디케이터 표시
            if (isLoading) {
                item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
            }
        }
    }


}

@Composable
fun ListItem(
    item: RecipeMainListResponseDto,
    bookmarkViewModel: BookMarkViewModel,
) {
    var isBookmarked by remember { mutableStateOf(item.bookmarkId != null) }

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
            Log.d("ListItem", "After image loading")

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
                Log.d("ListItem", "After text rendering")

                // 서브 카테고리 Assist Chips
                if (item.subCategoryList.isNotEmpty()) {
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        item.subCategoryList.take(3).forEach { subCategory ->
                            AssistChip(
                                onClick = { /* 서브 카테고리 선택 또는 해제 로직 */ },
                                label = { Text(subCategory, fontSize = 10.sp) },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                Log.d("ListItem", "After subcategory rendering")
            }

            // 북마크 아이콘
            IconButton(
                onClick = {
                    if (isBookmarked) {
                        // 북마크 제거 로직
                        bookmarkViewModel.removeBookmark(item.bookmarkId!!)
                    } else {
                        // 북마크 추가 로직
                        item.id?.let { bookmarkViewModel.addBookmark(it) }
                    }
                    isBookmarked = !isBookmarked
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                val (icon, tint) = if (isBookmarked) {
                    Pair(Icons.Filled.Bookmark, MaterialTheme.colorScheme.primary)
                } else {
                    Pair(Icons.Outlined.BookmarkBorder, Color.Gray)
                }

                Icon(
                    imageVector = icon,
                    contentDescription = "즐겨찾기",
                    tint = tint
                )
            }
        }
    }
}
