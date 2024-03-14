package com.recipia.aos.ui.components.home.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.R
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.api.dto.RecipeListResponseDto
import com.recipia.aos.ui.api.dto.search.SearchType
import com.recipia.aos.ui.model.recipe.bookmark.BookMarkViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel


/**
 * 레시피 통합검색 기능
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    navController: NavController,
    recipeAllListViewModel: RecipeAllListViewModel,
    bookmarkViewModel: BookMarkViewModel
) {

    val searchText = recipeAllListViewModel.searchText.value

    // 화면 터치로 키보드 없애기 위한 상태
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val isLoading by recipeAllListViewModel.isSearchLoading.observeAsState(initial = false)

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // 포커스 해제
                    keyboardController?.hide() // 키보드 숨기기
                })
            },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "레시피 검색",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // todo: 입력했던 검색어 상태 초기화시키기
                            recipeAllListViewModel.searchResults.value = emptyList()
                            recipeAllListViewModel.searchText.value = ""
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        // 검색창
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // 키보드가 활성화될 때 패딩 적용
                .padding(innerPadding)
        ) {
            Column {
                // 검색방법 설명 필드
                Text(
                    text = "* 검색 결과는 레시피 제목을 기준으로 노출됩니다.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )

                // 검색어 입력창
                TextField(
                    value = searchText,
                    onValueChange = { newValue ->
                        recipeAllListViewModel.searchText.value = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    placeholder = { Text("검색어 입력") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Black
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    // 키보드에서 검색을 눌렀을 때 아래의 코드에 적힌 도작을 수행한다.
                    keyboardActions = KeyboardActions(
                        onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        if (searchText.isNotBlank()) {
                            recipeAllListViewModel.searchRecipes(searchText)
                        }
                    })
                )

                // 검색 결과를 뿌려주는 부분
                LazyColumn {
                    itemsIndexed(
                        recipeAllListViewModel.searchResults.value
                    ) { index, recipe ->
                        SearchResultItem(
                            recipe,
                            bookmarkViewModel,
                            navController
                        )

                        // 마지막 아이템에 도달했을 때 추가 데이터 로드
                        if (index == recipeAllListViewModel.searchResults.value.lastIndex && !recipeAllListViewModel.isLastPage && !isLoading) {
                            // 리스트의 끝에 도달했을 때 추가 데이터 로드
                            recipeAllListViewModel.loadMoreSearchItems(searchText)
                        }
                    }
                }
            }
        }
    }
}

// 무한스크롤로 보여줄 아이템
@Composable
fun SearchResultItem(
    item: com.recipia.aos.ui.api.dto.RecipeListResponseDto,
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