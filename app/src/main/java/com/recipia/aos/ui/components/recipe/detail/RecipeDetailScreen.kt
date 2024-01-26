package com.recipia.aos.ui.components.recipe.detail

import TokenManager
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.components.recipe.detail.comment.CommentItem
import com.recipia.aos.ui.components.recipe.detail.comment.CommentsSection
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

//    ModalBottomSheet(
//        sheetState = sheetState,
//        onDismissRequest = {
//            coroutineScope.launch {
//                sheetState.hide()
//            }
//        }
//    ) {
//        if (comments == null || comments!!.content.isEmpty()) {
//            // 댓글 데이터가 없는 경우
//            Text(
//                text = "아직 작성된 댓글이 없습니다.",
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(16.dp)
//            )
//        } else {
//            // 댓글 데이터가 있는 경우
//            CommentsSection(
//                comments = comments!!.content,
//                loadMoreComments = {
//                    coroutineScope.launch {
//                        commentViewModel.loadMoreComments(recipeId)
//                    }
//                }
//            )
//        }
//    }

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
            showSheet = showSheet,
            setShowSheet = setShowSheet  // 상태 변경 함수 전달
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
    showSheet: Boolean,
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
            ) {

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

                Spacer(modifier = Modifier.height(40.dp))

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
                            .size(60.dp) // 이미지 크기
                            .clip(CircleShape) // 원형 클리핑
                            .border(0.5.dp, Color.Gray, CircleShape) // 회색 테두리 추가
                            .padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 작성자 이름과 카테고리
                    Column {
                        Text(
                            text = recipeDetail.nickname,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .padding(horizontal = 16.dp)
                        )

                        // todo: 여기서 회원 닉네임 누르면 이동시킬때 memberId를 onClick콜백에 담아서 마이페이지 api 요청하도록 하면됨
//                        recipeDetail.memberId

                        // 카테고리 정보
                        Row(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            recipeDetail.subCategoryDtoList.forEach { subCategory ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(subCategory.subCategoryNm.orEmpty()) }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color(222, 226, 230) // 구분선의 색상 설정
                )
//                Divider(Modifier.padding(vertical = 8.dp))

                // 제목
                Text(
                    text = "제목: ${recipeDetail.recipeName}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color(222, 226, 230) // 구분선의 색상 설정
                )

                // 재료 목록
                Text(
                    text = "재료: ${recipeDetail.ingredient}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 영양 정보
                recipeDetail.nutritionalInfoDto?.let { info ->
                    Text(
                        text = "영양 정보: 탄수화물 ${info.carbohydrates}," +
                                " 단백질 ${info.protein}," +
                                " 지방 ${info.fat}," +
                                " 비타민 ${info.vitamins}," +
                                " 미네랄 ${info.minerals}",
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

                // 레시피 설명
                Text(
                    text = recipeDetail.recipeDesc,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth() // 전체 너비를 채우도록 설정
                        .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                    thickness = 0.5.dp, // 구분선의 두께 설정
                    color = Color(222, 226, 230) // 구분선의 색상 설정
                )

                // '댓글' 버튼을 눌렀을 때 showSheet 상태를 true로 설정
                Button(
                    onClick = { setShowSheet(true) },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("댓글")
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

@Composable
fun CommentsList(
    commentViewModel: CommentViewModel,
    recipeId: Long,
    maxHeight: Dp
) {
    val commentsResponse by commentViewModel.comments.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.height(maxHeight)  // 여기에서 최대 높이를 적용합니다.
    ) {
        itemsIndexed(commentsResponse?.content ?: emptyList()) { index, comment ->
            CommentItem(comment)
        }

        item {
            Button(onClick = {
                coroutineScope.launch {
                    commentViewModel.loadMoreComments(recipeId)
                }
            }) {
                Text("더 많은 댓글 불러오기")
            }
        }
    }
}