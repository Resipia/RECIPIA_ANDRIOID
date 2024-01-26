package com.recipia.aos.ui.components.mypage

import TokenManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.recipia.aos.R
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectRecipeScreen(
    navController: NavController,
    recipeAllListViewModel: RecipeAllListViewModel,
    bookmarkViewModel: BookMarkViewModel,
    myPageViewModel: MyPageViewModel,
    targetMemberId: Long? = null,
    tokenManager: TokenManager
) {

    val isLoading by myPageViewModel.isLoading.observeAsState(initial = false)
    val myRecipes = myPageViewModel.items.value
    val lazyListState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val currentPageType by myPageViewModel.currentPageType.observeAsState()

    // 타겟 멤버id 호출
    val targetId = targetMemberId ?: tokenManager.loadMemberId() // memberId 결정

    // 데이터 로딩 및 페이지 업데이트 로직
    LaunchedEffect(key1 = currentPageType) {
        when (currentPageType) {
            MyPageViewModel.PageType.BOOKMARK -> myPageViewModel.getAllMyBookmarkRecipeList()
            MyPageViewModel.PageType.LIKE -> myPageViewModel.getAllMyLikeRecipeList()
            MyPageViewModel.PageType.TARGET_MEMBER -> myPageViewModel.loadMoreTargetMemberRecipes(targetId)
            else -> {}
        }
    }

    // 현재 페이지 타입에 따라 적절한 무한 스크롤 로딩 함수 호출
    fun loadMoreRecipesBasedOnCurrentPageType(
        viewModel: MyPageViewModel,
        pageType: MyPageViewModel.PageType?,
        targetMemberId: Long
    ) {
        when (pageType) {
            MyPageViewModel.PageType.BOOKMARK -> viewModel.loadMoreMyBookmarkRecipes()
            MyPageViewModel.PageType.LIKE -> viewModel.loadMoreMyLikeRecipes()
            MyPageViewModel.PageType.TARGET_MEMBER -> viewModel.loadMoreTargetMemberRecipes(targetMemberId)
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 85.dp)
                    ) {
                        Text(
                            text = when (currentPageType) {
                                MyPageViewModel.PageType.BOOKMARK -> "북마크한 레시피 목록"
                                MyPageViewModel.PageType.LIKE -> "좋아요한 레시피 목록"
                                MyPageViewModel.PageType.TARGET_MEMBER -> "타겟 멤버 레시피"
                                else -> ""
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold, // 볼드체 적용
                                color = Color.Black // 텍스트 색상을 검정색으로 설정
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        // 아이템 초기화 로직
                        myPageViewModel.resetItemsAndHighCountRecipe()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        },
        modifier = Modifier.background(Color.White),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // 로딩 중이라면 로딩 인디케이터 표시
                if (isLoading) {
                    MyPageAnimatedPreloader(modifier = Modifier.size(100.dp)) // 로딩 바의 크기 조절 가능
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding(),
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White), // 여기에 배경색을 하얀색으로 설정,,
                        state = lazyListState
                    ) {
                        itemsIndexed(myRecipes) { index, recipe ->
                            MyPageRecipeListItem(
                                recipe,
                                bookmarkViewModel,
                                navController
                            )

                            // 마지막 아이템에 도달했을 때 추가 데이터 로드
                            if (index == myPageViewModel.items.value.lastIndex && !myPageViewModel.isLastPage && !isLoading) {
                                loadMoreRecipesBasedOnCurrentPageType(myPageViewModel, currentPageType, targetId)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyPageRecipeListItem(
    item: RecipeListResponseDto,
    bookmarkViewModel: BookMarkViewModel,
    navController: NavController
) {
    var isBookmarked by remember { mutableStateOf(item.bookmarkId != null) }
    val imagePainter = rememberAsyncImagePainter(
        model = item.thumbnailPreUrl ?: R.drawable.ic_launcher_foreground // 기본 이미지 리소스
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("recipeDetail/${item.id}") } // 상세보기 화면으로 이동
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(130.dp) // 이 부분을 수정
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)), // 연한 테두리 추가
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
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
            color = Color(222,226,230) // 구분선의 색상 설정
        )
    }
}

@Composable
fun MyPageAnimatedPreloader(modifier: Modifier = Modifier) {
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
    // `modifier` 매개변수를 사용하여 사이즈 조절
    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier.size(100.dp) // 여기에서 원하는 크기로 조절합니다.
    )
}

