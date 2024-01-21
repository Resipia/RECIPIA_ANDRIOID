package com.recipia.aos.ui.components.home

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.recipia.aos.R
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.dto.RecipeMainListResponseDto
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookmarkUpdateState
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class)
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

    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                recipeAllListViewModel.refreshItems() // 여기서 refreshItems 메서드 호출
                while (recipeAllListViewModel.isLoading.value == true) {
                    delay(1000)
                }
                isRefreshing = false
            }
        }
    )

    // 데이터 로딩 완료 감지
    LaunchedEffect(recipeAllListViewModel.items) {
        isRefreshing = false // 데이터 로딩이 완료되면 isRefreshing을 false로 설정
    }

    /**
     * 상태가 변경될 때마다, 즉 북마크가 추가되거나 제거될 때마다 recipeAllListViewModel의 updateItemBookmarkId 함수를 호출하여 전체 목록의 상태를 업데이트합니다.
     * 이 로직은 북마크 상태의 변경이 백엔드에서 성공적으로 처리되었을 때, 앱의 전체 상태(여기서는 레시피 목록)를 업데이트하는 데 사용됩니다.
     */
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
    ) { paddingValues -> // 여기서 innerPadding 대신 paddingValues 사용
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // 예시 이미지 URL 리스트
            val sampleImages = listOf(
                "https://via.placeholder.com/600x200.png?text=First+Image",
                "https://via.placeholder.com/600x200.png?text=Second+Image",
                "https://via.placeholder.com/600x200.png?text=Third+Image",
                "https://via.placeholder.com/600x200.png?text=Fourth+Image",
                "https://via.placeholder.com/600x200.png?text=Fifth+Image"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pullRefresh(pullRefreshState),
                contentAlignment = Alignment.Center // 여기를 수정
            ) {

                // 로딩 중이라면 로딩 인디케이터 표시
                if (isLoading) {
                    AnimatedPreloader(modifier = Modifier.size(100.dp)) // 로딩 바의 크기 조절 가능
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding() + 80.dp
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // AutoScrollingSlider를 LazyColumn 아이템으로 추가
                        item {
                            AutoScrollingSlider(sampleImages)
                        }

                        itemsIndexed(
                            recipeAllListViewModel.items.value
                        ) { index, item ->
                            // 각 아이템을 컴포저로 그려내기
                            ListItem(
                                item,
                                bookmarkViewModel,
                                navController
                            )

                            // 마지막 아이템에 도달했을 때 추가 데이터 로드
                            if (index == recipeAllListViewModel.items.value.lastIndex && !recipeAllListViewModel.isLastPage && !isLoading) {
                                recipeAllListViewModel.loadMoreItems()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(
    item: RecipeMainListResponseDto,
    bookmarkViewModel: BookMarkViewModel,
    navController: NavController
) {
    var isBookmarked by remember { mutableStateOf(item.bookmarkId != null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("recipeDetail/${item.id}") } // 상세보기 화면으로 이동
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지 크기를 1.5배로 늘림
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with your image resource
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .size(130.dp) // 이 부분을 수정
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
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                Text(
                    text = item.nickname,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
                Log.d("ListItem", "After text rendering")

                // 서브 카테고리 Assist Chips
                if (item.subCategoryList.isNotEmpty()) {
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        item.subCategoryList.take(3).forEach { subCategory ->
                            AssistChip(
                                onClick = { /* 서브 카테고리 선택 또는 해제 로직 */ },
                                label = { Text(subCategory, fontSize = 10.sp) },
                                modifier = Modifier.padding(horizontal = 1.dp)
                            )
                        }
                    }
                }
                Log.d("ListItem", "After subcategory rendering")
            }

            /**
             * 사용자가 아이콘을 클릭하면, isBookmarked 상태가 토글되고, BookMarkViewModel의 addBookmark 또는 removeBookmark 함수가 호출됩니다.
             * 이 로직은 사용자 인터랙션에 직접 반응하여 UI 상의 북마크 상태를 변경하고, 백엔드(데이터베이스 또는 서버)에 북마크의 추가 또는 제거를 요청합니다.
             */
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

@Composable
fun AnimatedPreloader(modifier: Modifier = Modifier) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.animation_preloader // 여기에 애니메이션 리소스를 지정합니다.
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    // Lottie 애니메이션을 화면에 표시합니다.
    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
    )
}
