package com.recipia.aos.ui.components.home

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.R
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.common.AnimatedPreloader
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.dto.RecipeListResponseDto
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.bookmark.BookmarkUpdateState
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.function.BinaryOperator

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    recipeAllListViewModel: RecipeAllListViewModel,
    bookmarkViewModel: BookMarkViewModel
) {
    /**
     * LiveData에 주로 observeAsState를 사용한다.
     * observeAsState를 사용하면, LiveData가 노출하는 데이터가 변경될 때 Composable 함수가 자동으로 다시 호출되어 UI가 업데이트되는 구조다.
     */
    // items 상태를 직접 사용
    val isLoading by recipeAllListViewModel.isLoading.observeAsState(initial = false)
    val loadFailed by recipeAllListViewModel.loadFailed.observeAsState(initial = false)
    val navigateToLogin by recipeAllListViewModel.navigateToLogin.observeAsState(initial = false)
//    val snackBarMessage by bookmarkViewModel.snackBarMessage.observeAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState() // LazyListState 인스턴스 생성
    val isScrolled = derivedStateOf { lazyListState.firstVisibleItemIndex > 0 }.value
    val bookmarkUpdateState by bookmarkViewModel.bookmarkUpdateState.observeAsState()
    var menuExpanded by remember { mutableStateOf(false) }// 드롭다운 메뉴 상태
    var chipMenuExpanded by remember { mutableStateOf(false) } // 메뉴 확장 상태
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    var showFab by remember { mutableStateOf(true) }

    // `animateDpAsState` 사용하여 부드러운 애니메이션 적용
    val fabWidth by animateDpAsState(
        targetValue = if (isScrolled) 50.dp else 90.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = ""
    )

    // 화면에서 데이터 새로고침할때 사용
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                // 여기서 refreshItems 메서드 호출
                recipeAllListViewModel.refreshItems(recipeAllListViewModel.selectedSubCategories.value)
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

    // 홈 화면이 로딩될때마다 페이지 reload하여 데이터를 받아온다.
    LaunchedEffect(key1 = true) {
        recipeAllListViewModel.loadItemsWithSelectedSubCategories()
    }

    /**
     * 상태가 변경될 때마다, 즉 북마크가 추가되거나 제거될 때마다 recipeAllListViewModel의 updateItemBookmarkId 함수를 호출하여 전체 목록의 상태를 업데이트합니다.
     * 이 로직은 북마크 상태의 변경이 백엔드에서 성공적으로 처리되었을 때, 앱의 전체 상태(여기서는 레시피 목록)를 업데이트하는 데 사용됩니다.
     */
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

    // navigateToLogin 상태가 변경되었을 때 로그인 화면으로 이동
    if (navigateToLogin) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate("login")
            recipeAllListViewModel.resetNavigateToLogin() // 로그인 화면으로 이동 후 `_navigateToLogin`을 리셋하는 함수 호출
        }
    }

    // 스낵바가 생기면 작성버튼이 사라지도록 하는 코루틴
    LaunchedEffect(snackbarHostState.currentSnackbarData) {
        if (snackbarHostState.currentSnackbarData != null) {
            // 스낵바가 표시되면 FAB 숨기기
            showFab = false
        } else {
            // 스낵바가 사라지면 0.3초 후에 FAB 표시
            coroutineScope.launch {
                delay(100) // 0.3초 지연
                showFab = true
            }
        }
    }

    // 스낵바 2초후에 숨기기
    LaunchedEffect(snackbarHostState.currentSnackbarData) {
        snackbarHostState.currentSnackbarData?.let {
            delay(2000) // 2초 동안 기다림
            it.dismiss() // 스낵바 숨기기
        }
    }

    // loadFailed 상태가 true일 때 스낵바를 표시하는 로직
    LaunchedEffect(loadFailed) {
        if (loadFailed) {
            // 스낵바 표시
            snackbarHostState.showSnackbar(
                message = "데이터 로딩 실패", // 스낵바에 표시할 메시지
                duration = SnackbarDuration.Short // 스낵바가 표시되는 시간
            )
            recipeAllListViewModel.resetLoadFailed() // 상태 리셋
        }
    }

//    // 북마크 추가/삭제에 따라 메시지가 변경될 때 스낵바를 표시하는 로직 (임시 주석처리)
//    LaunchedEffect(snackBarMessage) {
//        snackBarMessage?.let {
//            // 스낵바 표시
//            snackbarHostState.showSnackbar(
//                message = it, // 스낵바에 표시할 메시지
//                duration = SnackbarDuration.Short // 스낵바가 표시되는 시간
//            )
//            bookmarkViewModel.snackBarMessage.value = null // 메시지 초기화
//        }
//    }

    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                modifier = Modifier.background(Color.White), // 여기에 배경색을 하얀색으로 설정,
                backgroundColor = Color.White,
                title = {
                    // 여기서 로고와 텍스트를 Row로 배치합니다.
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 로고 텍스트
                        Text(
                            text = "Recipia",
                            color = Color(27, 94, 32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        )
                    }
                },
                actions = {
                    // 검색 아이콘
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "레시피 통합검색 기능이 곧 추가됩니다."
                                )
                            }
                            navController.navigate("recipe-search")
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "검색")
                    }

                    // 더보기 아이콘
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "더보기"
                        )
                    }

                    // 맨 우측 드롭다운 메뉴 설정
                    CustomDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        // 드롭다운 메뉴 아이템들
//                        DropdownMenuItem(
//                            text = { Text("신고하기", color = Color.Black) },
//                            onClick = { /* 수정 처리 */ }
//                        )
//                        HorizontalDivider(
//                            modifier = Modifier.fillMaxWidth(),
//                            thickness = 0.5.dp,
//                            color = Color(222, 226, 230)
//                        )
                        DropdownMenuItem(
                            text = { Text("문의/피드백 보내기", color = Color.Black) },
                            onClick = { navController.navigate("ask-create") }
                        )
                    }
                },
//                scrollBehavior = scrollBehavior
            )
        },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        modifier = Modifier.background(Color.White),
        // 하단의 레시피 작성 버튼 설정
        floatingActionButton = {
            if (showFab) {
                // 하단에 떠있는 작성 버튼
                FloatingActionButton(
                    containerColor = Color(27, 94, 32),
                    onClick = { navController.navigate("create-recipe") },
                    modifier = Modifier
                        .height(44.dp) // 높이 설정
                        .width(fabWidth) // 애니메이션화된 너비 사용
                        .background(Color.White),
                    shape = RoundedCornerShape(16.dp) // 모서리 둥글게
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            "글쓰기",
                            tint = Color.White
                        )
                        if (!isScrolled) { // 스크롤 되지 않았을 때만 "작성" 텍스트 표시
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                "작성",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                snackbarHostState = snackbarHostState,
                recipeAllListViewModel = recipeAllListViewModel,
                lazyListState = lazyListState
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // 여기에 배경색을 하얀색으로 설정
                    .pullRefresh(pullRefreshState),
                contentAlignment = Alignment.Center // 여기를 수정
            ) {
                // 로딩 중이라면 로딩 인디케이터 표시
                if (isLoading) {
                    AnimatedPreloader(modifier = Modifier.size(100.dp))
                } else {
                    // 레시피 데이터 목록이 비어있는지 확인
                    if (recipeAllListViewModel.items.value.isEmpty()) {
                        // 데이터 목록이 비어있으면 메시지 표시
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { // Column으로 감싸서 세로 정렬
                                Text(
                                    text = "레시피가 존재하지 않습니다.",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp)) // Text 사이의 간격 추가
                                Text(
                                    text = "홈버튼을 눌러 새로고침 해주세요.",
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
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
                            item {
                                // 여기에 Box 또는 Column 추가
                                Column(
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                ) {
                                    // 최상단 chip 전용 Row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    ) {
                                        // 정렬 chip
                                        AssistChip(
                                            onClick = {
                                                chipMenuExpanded = true
                                            }, // 클릭 시 드롭다운 메뉴 표시
                                            label = {
                                                Text("정렬", fontSize = 12.sp)
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Sort,
                                                    contentDescription = "정렬 아이콘",
                                                    modifier = Modifier.size(18.dp),
                                                    tint = Color.Black
                                                )
                                            },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = Color(238, 238, 238),
                                                labelColor = Color.Black
                                            ),
                                            elevation = null,
                                            border = null
                                        )

                                        Spacer(modifier = Modifier.width(8.dp)) // 칩 사이의 간격

                                        // 카테고리 조회 chip
                                        AssistChip(
                                            onClick = {
                                                navController.navigate("category-recipe-search")
                                            },
                                            label = {
                                                Text("카테고리", fontSize = 12.sp)
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Default.Category,
                                                    contentDescription = "카테고리 아이콘",
                                                    modifier = Modifier
                                                        .size(18.dp), // 아이콘 크기 조절
                                                    tint = Color.Black // 아이콘 색상을 검은색으로 설정
                                                )
                                            },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = Color(238, 238, 238),
                                                labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                                            ),
//                                        elevation = null, // 그림자 제거
                                            border = null, // 테두리 제거
                                        )

                                        // 정렬조건 드롭다운 메뉴
                                        CustomDropdownMenu(
                                            expanded = chipMenuExpanded,
                                            onDismissRequest = { chipMenuExpanded = false },
                                            modifier = Modifier
                                                .background(Color.White)
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("최신순", color = Color.Black) },
                                                onClick = {
                                                    chipMenuExpanded = false
                                                    recipeAllListViewModel.currentRequestSortType =
                                                        "new"
                                                    recipeAllListViewModel.loadItemsWithSelectedSubCategories() // 정렬 조건에 따라 아이템 로드
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("오래된순", color = Color.Black) },
                                                onClick = {
                                                    chipMenuExpanded = false
                                                    recipeAllListViewModel.currentRequestSortType =
                                                        "old"
                                                    recipeAllListViewModel.loadItemsWithSelectedSubCategories() // 정렬 조건에 따라 아이템 로드
                                                }
                                            )
                                        }
                                    }
                                }
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
                                    recipeAllListViewModel.loadMoreItems(recipeAllListViewModel.selectedSubCategories.value) // 서브 카테고리 리스트로 추가 데이터 요청
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 스크롤 위치가 변경될 때 마다 트리거
    LaunchedEffect(remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }) {
        coroutineScope.launch {
            // 필요한 경우 스크롤 위치 조정
            // 예: lazyListState.scrollToItem(0)
        }
    }
}

@Composable
fun ListItem(
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
            .padding(vertical = 3.dp)
            .clickable { navController.navigate("recipeDetail/${item.id}") } // 상세보기 화면으로 이동
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 이미지 썸네일 설정
            Image(
                painter = imagePainter,
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(110.dp) // 이 부분을 수정
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(8.dp)), // 연한 테두리 추가
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 1.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically // 여기에 추가
                    ) {
                        // 닉네임
                        Text(
                            text = item.nickname,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.padding(bottom = 20.dp),
                            fontSize = 12.sp,
                        )

                        // 날짜 표시
                        item.createDate?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                modifier = Modifier.weight(1f), // 여기 weight(1f)를 적용
                                textAlign = TextAlign.End // 날짜를 왼쪽으로 정렬
                            )
                        }

                        // 북마크 아이콘
                        IconButton(
                            onClick = {
                                if (isBookmarked) {
                                    bookmarkViewModel.removeBookmark(item.bookmarkId!!)
                                } else {
                                    item.id?.let { bookmarkViewModel.addBookmark(it) }
                                }
                                isBookmarked = !isBookmarked
                            },
                            // 아이콘 크기 조절
                            modifier = Modifier
                                .size(28.dp)
                                .padding(start = 2.dp)
                        ) {
                            val (icon, tint) = if (isBookmarked) {
                                Pair(Icons.Filled.Bookmark, MaterialTheme.colorScheme.primary)
                            } else {
                                Pair(Icons.Outlined.BookmarkBorder, Color.Gray)
                            }

                            Icon(
                                imageVector = icon,
                                contentDescription = "북마크",
                                tint = Color(27, 94, 32)
                            )
                        }
                    }

                    // 레시피명(제목)
                    Text(
                        text = item.recipeName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp, // 글씨 크기를 줄임 (기존 값보다 작게 설정)
                        modifier = Modifier.padding(start = 1.dp, end = 16.dp) // 오른쪽에 패딩 추가
                    )

                    // 서브 카테고리 Assist Chips
                    if (item.subCategoryList.isNotEmpty()) {
                        Row(
                            modifier = Modifier.padding(top = 2.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp) // AssistChip 사이의 간격을 조절
                        ) {
                            item.subCategoryList.take(3).forEach { subCategory ->
                                AssistChip(
                                    onClick = { /* 서브 카테고리 선택 또는 해제 로직 */ },
                                    label = { Text(subCategory, fontSize = 10.sp) }
                                )
                            }
                        }
                    }

                }
            }


        }
        // 항목 사이에 구분선 추가
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth() // 전체 너비를 채우도록 설정
                .padding(horizontal = 16.dp), // 양쪽에 패딩 적용
            thickness = 0.5.dp, // 구분선의 두께 설정
            color = Color(222, 226, 230) // 구분선의 색상 설정
        )
    }
}
