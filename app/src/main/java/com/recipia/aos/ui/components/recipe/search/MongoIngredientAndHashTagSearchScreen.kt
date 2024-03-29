package com.recipia.aos.ui.components.recipe.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.api.dto.search.SearchType
import com.recipia.aos.ui.model.search.MongoSearchViewModel
import kotlinx.coroutines.launch


/**
 * 재료, 해시태그 검색 및 추가 컴포저
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MongoIngredientAndHashTagSearchScreen(
    navController: NavController,
    mongoViewModel: MongoSearchViewModel,
    type: com.recipia.aos.ui.api.dto.search.SearchType
) {

    val searchText by mongoViewModel.searchText.collectAsState()
    val mongoSearchResults by mongoViewModel.mongoSearchResults.collectAsState()
    val showSearchResults by mongoViewModel.showSearchResults.collectAsState()

    // 화면 터치로 키보드 없애기 위한 상태
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // 추가한 재료, 해시태그 상태를 저장할 변수
    val selectedIngredients = remember { mutableStateListOf<String>() }
    val selectedHashtags = remember { mutableStateListOf<String>() }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

    // 화면이 로드될 때 ViewModel에서 현재 선택된 항목들을 로컬 상태 변수로 복사
    LaunchedEffect(key1 = Unit) {
        selectedIngredients.addAll(mongoViewModel.selectedIngredients.value)
        selectedHashtags.addAll(mongoViewModel.selectedHashtags.value)
    }

    LaunchedEffect(type) {
        mongoViewModel.init(type) // type에 따라 검색 초기화
    }

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
                title = { Text(text = "검색") },
                navigationIcon = {
                    IconButton(onClick = {
                        mongoViewModel.resetTextChange() // 입력 텍스트 초기화
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // 사용자가 선택한 항목들을 ViewModel로 업데이트
                    if (type == com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT) {
                        mongoViewModel.setSelectedIngredients(selectedIngredients)
                    } else if (type == com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG) {
                        mongoViewModel.setSelectedHashtags(selectedHashtags)
                    }
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(56, 142, 60)
                ),
                shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp))
            ) {
                Text(
                    "선택 완료",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            Column {
                // 검색방법 설명 필드
                Text(
                    text = "* 추가할 단어를 입력하신 후 연관단어 선택 또는 엔터 버튼을 눌러주세요.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )


                // 검색어 입력창
                TextField(
                    value = searchText,
                    onValueChange = {
                        val filteredText = it.filter { char -> char.isLetterOrDigit() }
                        mongoViewModel.onSearchTextChange(filteredText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                    placeholder = {
                        if (type == com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT) {
                            Text(
                                "재료를 입력하세요 (한/영, 숫자만 입력 가능)",
                                fontSize = 14.sp
                            )
                        } else if (type == com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG) {
                            Text(
                                "해시태그를 입력하세요 (한/영, 숫자만 입력 가능)",
                                fontSize = 14.sp
                            )
                        }
                    },
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
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (searchText.isNotBlank()) {

                                // 정규 표현식으로 완전한 한글, 영문, 숫자만 허용
                                if (!searchText.matches(Regex("^[가-힣a-zA-Z0-9\\s]+$"))) {

                                    // 키보드를 먼저 숨긴다.
                                    keyboardController?.hide()

                                    // 오류 발생 시 스낵바 알림
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "유효하지 않은 입력입니다. (자음, 모음 단독 사용 불가)",
                                            duration = SnackbarDuration.Short
                                        )
                                    }

                                } else {
                                    // 기존 로직
                                    if (type == com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT && !selectedIngredients.contains(
                                            searchText
                                        )
                                    ) {
                                        selectedIngredients.add(searchText) // 재료 리스트에 추가
                                    } else if (type == com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG && !selectedHashtags.contains(
                                            searchText
                                        )
                                    ) {
                                        selectedHashtags.add(searchText) // 해시태그 리스트에 추가
                                    }
                                    mongoViewModel.onSearchTextChange("") // 입력 필드 초기화
                                    keyboardController?.hide() // 키보드 숨기기
                                    focusManager.clearFocus() // 포커스 해제
                                }
                            }
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                )

                // 검색 유형에 따른 상태 리스트 선택
                val selectedResults = when (type) {
                    com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT -> selectedIngredients
                    com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG -> selectedHashtags
//                    SearchType.INGREDIENT -> mongoViewModel.selectedIngredients.value
//                    SearchType.HASHTAG -> mongoViewModel.selectedHashtags.value
                    else -> listOf() // 기본적으로 빈 리스트
                }

                // 선택된 검색 결과들을 AssistChip으로 표시
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Top,
                    maxItemsInEachRow = Int.MAX_VALUE
                ) {
                    selectedResults.forEach { result ->
                        AssistChip(
                            onClick = {},
                            label = {
                                if (type == com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT) {
                                    Text(result, fontSize = 12.sp)
                                } else if (type == com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG) {
                                    Text("#${result}", fontSize = 12.sp)
                                }
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(238, 238, 238),
                                labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                            ),
//                        elevation = null, // 그림자 제거
//                        border = null, // 테두리 제거
                        )
                        Spacer(modifier = Modifier.width(6.dp)) // 여백 추가
                    }
                }

                // 만약 검색 결과가 있다면 하단에 연관 검색어창을 보여준다.
                if (showSearchResults) {
                    LazyColumn {
                        // 검색 결과는 해시태그, 재료에따라 다르게 상태를 저장한다.
                        items(mongoSearchResults) { result ->
                            Text(
                                text = result,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .clickable {
                                        if (type == com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT) {
                                            if (!selectedIngredients.contains(result)) {
                                                selectedIngredients.add(result) // 재료 리스트에 추가
                                            }
                                        } else if (type == com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG) {
                                            if (!selectedHashtags.contains(result)) {
                                                selectedHashtags.add(result) // 해시태그 리스트에 추가
                                            }
                                        }
                                        mongoViewModel.clearMongoSearchResults() // 연관 검색어 목록 초기화
                                        mongoViewModel.resetTextChange() // 입력 텍스트 초기화
                                        keyboardController?.hide() // 키보드 숨기기
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
    }
}
