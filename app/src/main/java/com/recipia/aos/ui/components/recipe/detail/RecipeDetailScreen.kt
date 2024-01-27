package com.recipia.aos.ui.components.recipe.detail

import TokenManager
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.components.recipe.detail.comment.CommentItem
import com.recipia.aos.ui.model.comment.CommentViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    tokenManager: TokenManager
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }
    val currentUserMemberId = tokenManager.loadMemberId() // 현재 사용자의 memberId 불러오기

    // 레시피 상세 정보의 상태를 관찰
    val recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()

    // AlertDialog를 표시할지 여부를 관리하는 상태
    var showDialog by remember { mutableStateOf(false) }

    // ModalBottomSheet 상태 관리
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }

    // showSheet 상태를 변경하는 함수
    val setShowSheet = { value: Boolean -> showSheet = value }

    // 이것은 BottomSheet를 호출하는 부분이야.
    if (showSheet) {
        BottomSheet(
            onDismiss = {  // 여기서 상태를 변경하는 람다 함수를 전달함
                coroutineScope.launch {
                    showSheet = false
                }
            },
            commentViewModel = commentViewModel,
            recipeId = recipeId
        )
    }

    // 댓글 상태 관찰
    val comments by commentViewModel.comments.collectAsState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("레시피 삭제") },
            text = { Text("정말 작성한 레시피를 삭제하시겠습니까?") },
            confirmButton = {
                Button(onClick = {
                    recipeDetailViewModel.deleteRecipe(
                        recipeId = recipeId,
                        onSuccess = {
                            // 삭제 성공 시 처리, 예를 들어 홈 화면으로 이동
                            Toast.makeText(context, "레시피가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                            navController.popBackStack()
                            navController.navigate("home")
                        },
                        onError = { errorMessage ->
                            // 오류 발생 시 처리
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                    showDialog = false
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                title = { Text(text = "", style = MaterialTheme.typography.bodyMedium) },
                navigationIcon = {
                    IconButton(onClick = {
                        // 댓글 목록 초기화
                        commentViewModel.clearComments()
                        // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                        navController.popBackStack()
                        navController.navigate("home")
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "더보기"
                        )
                    }
                    CustomDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        // 레시피 작성자가 현재 로그인한 사용자와 같은 경우에만 수정 및 삭제 옵션을 보여줌
                        if (recipeDetailState.value?.memberId == currentUserMemberId) {
                            // 레시피 수정하기
                            DropdownMenuItem(
                                text = { Text("레시피 수정") },
                                onClick = {
                                    navController.navigate("update-recipe")
                                }
                            )
                            // 레시피 삭제 버튼
                            DropdownMenuItem(
                                text = { Text("레시피 삭제") },
                                onClick = {
                                    showDialog = true
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("설정") },
                            onClick = { /* 설정 처리 */ }
                        )
                        DropdownMenuItem(
                            text = { Text("피드백 보내기") },
                            onClick = { /* 피드백 처리 */ }
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
        RecipeDetailContent(
            recipeId = recipeId,
            recipeDetailViewModel = recipeDetailViewModel,
            commentViewModel = commentViewModel,
            navController = navController,
            paddingValues = innerPadding,
            setShowSheet = setShowSheet
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeDetailContent(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    commentViewModel: CommentViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
    setShowSheet: (Boolean) -> Unit  // 상태 변경 함수 받기
) {

    // 레시피 상세 정보 로드
    LaunchedEffect(key1 = recipeId) {
        recipeDetailViewModel.loadRecipeDetail(recipeId)
        commentViewModel.loadInitialComments(recipeId) // 수정된 함수 호출
    }

    // LiveData를 Compose에서 관찰하기 위해 observeAsState() 사용
    val recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()
    val isLoading = recipeDetailViewModel.isLoading.observeAsState()

    if (isLoading.value == true) {
        // 로딩 인디케이터 표시
        CircularProgressIndicator()
    } else {
        recipeDetailState.value?.let { recipeDetail ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

                item {
                    // 이미지 슬라이더 구현
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        initialPageOffsetFraction = 0f
                    ) {
                        recipeDetail.recipeFileUrlList.size
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .height(300.dp) // 높이 설정
                    ) { page ->
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = recipeDetail.recipeFileUrlList[page].preUrl
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .aspectRatio(1.0f / 2.0f) // 이미지의 세로 길이를 가로 길이의 절반으로 설정
                        )
                    }

                    Spacer(modifier = Modifier.height(50.dp))
                }

                item {
                    // 날짜, 좋아요, 북마크 정보
                    Row(
//                        modifier = Modifier
//                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 날짜
                        val dateOnly = recipeDetail.createDate?.substringBefore("T") ?: ""
                        recipeDetail.createDate?.let {
                            Text(
                                text = dateOnly,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                        }
                        // todo: 좋아요

                        // todo: 북마크
                    }

//                    HorizontalDivider(
//                        modifier = Modifier
//                            .fillMaxWidth() // 전체 너비를 채우도록 설정
//                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
//                        thickness = 0.5.dp, // 구분선의 두께 설정
//                        color = Color(222, 226, 230) // 구분선의 색상 설정
//                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    // 작성자 정보
                    Row(
                        modifier = Modifier
                            .clickable {
                                // 여기서 navController를 사용하여 MyPageScreen으로 이동
                                navController.navigate("other-user-page/${recipeDetail.memberId}")
                            }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 프로필 이미지
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = recipeDetail.recipeFileUrlList
                                    ?: "https://example.com/default_profile.jpg"
                            ),
                            contentDescription = "작성자 프로필",
                            modifier = Modifier
                                .size(40.dp) // 이미지 크기
                                .clip(CircleShape) // 원형 클리핑
                                .border(0.5.dp, Color.Gray, CircleShape) // 회색 테두리 추가
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 닉네임
                        Text(
                            text = recipeDetail.nickname,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }

                item {
                    // 레시피명
                    Text(
                        text = recipeDetail.recipeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 카테고리 정보
                    Row(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "카테고리: ",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        recipeDetail.subCategoryDtoList.forEach { subCategory ->
                            Text(
                                subCategory.subCategoryNm.orEmpty(),
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // 레시피 내용
                    Text(
                        text = recipeDetail.recipeDesc,
                        color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }

                item {
                    // todo: 소요 시간
                    Text(
                        text = "소요 시간: ${recipeDetail.timeTaken}분",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // todo: 재료 목록
                    Text(
                        text = "재료: ${recipeDetail.ingredient}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // todo: 해시태그 정보
                    Text(
                        text = "해시태그: ${recipeDetail.hashtag}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // todo: 영양 정보
                    recipeDetail.nutritionalInfoDto?.let { info ->
                        Text(
                            text = "영양 정보: 탄수화물 ${info.carbohydrates}(g)," +
                                    " 단백질 ${info.protein}(g)," +
                                    " 지방 ${info.fat}(g)," +
                                    " 비타민 ${info.vitamins}(g)," +
                                    " 미네랄 ${info.minerals}(g)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                        thickness = 0.5.dp, // 구분선의 두께 설정
                        color = Color(222, 226, 230) // 구분선의 색상 설정
                    )
                }


                item {

                    Spacer(modifier = Modifier.height(8.dp))

                    // 댓글보기
                    Button(
                        onClick = { setShowSheet(true) },
                        modifier = Modifier
                            .fillMaxWidth() // 전체 너비를 채우도록 설정
                            .height(60.dp) // 버튼의 높이를 더 크게 설정
                            .padding(horizontal = 12.dp, vertical = 8.dp), // 주변 여백 설정
                        shape = RoundedCornerShape(12.dp), // 모서리를 둥글게 설정
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(241,243,245), // 버튼 배경색
                            contentColor = MaterialTheme.colorScheme.onPrimary // 텍스트 및 아이콘 색상
                        )
                    ) {
                        Text(
                            text = "댓글보기",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismiss: () -> Unit,
    commentViewModel: CommentViewModel,
    recipeId: Long
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        containerColor = Color.White, // 하단 시트의 배경색을 하얗게 설정
        content = {
            BoxWithConstraints {
                // 'maxHeight'를 여기에서 계산합니다.
                val maxHeight = this.maxHeight * 0.48f  // 화면 높이의 80%

                CommentsList(
                    commentViewModel = commentViewModel,
                    recipeId = recipeId,
                    maxHeight = maxHeight
                )
            }
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CommentsList(
    commentViewModel: CommentViewModel,
    recipeId: Long,
    maxHeight: Dp
) {
    val commentsResponse by commentViewModel.comments.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val comments = commentsResponse?.content ?: emptyList()

    Column {
        // 고정된 헤더 부분
        Text(
            text = "댓글",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth() // 전체 너비를 채우도록 설정
                .padding(start = 16.dp, bottom = 16.dp), // 양쪽에 패딩 적용
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(), // 전체 너비를 채우도록 설정
//                .padding(horizontal = 16.dp), // 양쪽에 패딩 적용
            thickness = 0.5.dp, // 구분선의 두께 설정
            color = Color(222, 226, 230) // 구분선의 색상 설정
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 스크롤 가능한 댓글 리스트 부분
        LazyColumn(
            modifier = Modifier.height(maxHeight)  // 여기에서 최대 높이를 적용합니다.
        ) {
            itemsIndexed(comments) { index, comment ->
                CommentItem(comment)

                // 마지막 항목이 렌더링되면 추가 데이터 로드
                if (index == comments.size - 1) {
                    coroutineScope.launch {
                        commentViewModel.loadMoreComments(recipeId)
                    }
                }
            }
        }
    }
}
