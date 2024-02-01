package com.recipia.aos.ui.components.recipe.detail.content

import TokenManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.recipia.aos.R
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.components.common.AnimatedPreloader
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.model.comment.CommentViewModel
import com.recipia.aos.ui.model.mypage.MyPageViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import kotlinx.coroutines.launch

/**
 * 레시피 상세보기 콘텐츠 컴포저
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailContent(
    recipeId: Long,
    recipeDetailViewModel: RecipeDetailViewModel,
    commentViewModel: CommentViewModel,
    myPageViewModel: MyPageViewModel,
    navController: NavController,
    paddingValues: PaddingValues,
    tokenManager: TokenManager
) {

    val recipeDetailState = recipeDetailViewModel.recipeDetail.observeAsState()
    val isLoading = recipeDetailViewModel.isLoading.observeAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    val currentUserMemberId = tokenManager.loadMemberId() // 현재 사용자의 memberId 불러오기
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    var profileImageUrl by remember { mutableStateOf<String?>(null) } // 프로필 이미지 URL 상태

    // 레시피 상세 정보 로드
    LaunchedEffect(key1 = recipeId) {
        recipeDetailViewModel.loadRecipeDetail(recipeId)
        commentViewModel.loadInitialComments(recipeId)
    }

    // 레시피 상세 정보가 로드된 후에 이미지를 가져오는 로직 수행
    LaunchedEffect(key1 = recipeDetailState.value) {
        recipeDetailState.value?.memberId?.let { memberId ->
            if (memberId > 0) {
                myPageViewModel.getMemberProfileImage(
                    memberId = memberId,
                    onSuccess = { url ->
                        profileImageUrl = url // 성공적으로 URL을 받아오면 상태 업데이트
                    },
                    onError = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "이미지 조회 에러 발생"
                            )
                        }
                    }
                )
            }
        }
    }

    // dialog(알림창) 호출
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("레시피 삭제", color = Color.Black) },
            text = { Text("정말 작성한 레시피를 삭제하시겠습니까?", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        recipeDetailViewModel.deleteRecipe(
                            recipeId = recipeId,
                            onSuccess = {
                                // 성공 시 스낵바 알림
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "레시피가 삭제되었습니다.",
                                        duration = SnackbarDuration.Short
                                    )
                                }

                                // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                                navController.popBackStack()
                                navController.navigate("home")
                            },
                            onError = { errorMessage ->
                                // 오류 발생 시 스낵바 알림
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "레시피 삭제에 실패하였습니다.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                        showDialog = false
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(27, 94, 32),
                        contentColor = Color.White
                    )
                ) {
                    Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(27, 94, 32),
                        contentColor = Color.White
                    ),
                ) {
                    Text("취소", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            // AlertDialog 스타일 커스텀
            containerColor = Color.White, // AlertDialog 배경색을 하얀색으로 설정
            textContentColor = Color.Black // 글자색을 검정색으로 설정
        )
    }

    // 로딩중이면 인디케이터 표시
    if (isLoading.value == true) {
        Box(
            modifier = Modifier
                .fillMaxSize() // 부모 컨테이너를 꽉 채움
                .wrapContentSize(Alignment.Center) // 내용을 중앙에 배치
        ) {
            AnimatedPreloader(modifier = Modifier.size(100.dp)) // 로딩 인디케이터의 크기 설정
        }
    } else {
        Box {
            // TopAppBar 고정
            TopAppBar(
                title = {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        // 댓글 목록 초기화 및 홈 화면으로 이동
                        commentViewModel.clearComments()

                        // 마이페이지 관련 데이터도 초기화 시키기(이래야 바로 적용됨)
                        myPageViewModel.resetItemsAndHighCountRecipe()
                        navController.popBackStack()

//                        navController.navigate("home")
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
                    // 드롭다운 메뉴
                    CustomDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        // 레시피 작성자가 현재 로그인한 사용자와 같은 경우에만 수정 및 삭제 옵션을 보여줌
                        if (recipeDetailState.value?.memberId == currentUserMemberId) {
                            // 레시피 수정
                            DropdownMenuItem(
                                onClick = {
                                    navController.navigate("update-recipe")
                                },
                                text = { Text(text = "레시피 수정", color = Color.Black) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 0.5.dp,
                                color = Color(222, 226, 230)
                            )
                            // 레시피 삭제
                            DropdownMenuItem(
                                onClick = {
                                    showDialog = true
                                },
                                text = { Text(text = "레시피 삭제", color = Color.Black) }
                            )
                            HorizontalDivider(
                                modifier = Modifier.fillMaxWidth(),
                                thickness = 0.5.dp,
                                color = Color(222, 226, 230)
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(text = "설정", color = Color.Black) },
                            onClick = { /* 설정 처리 */ }
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 0.5.dp,
                            color = Color(222, 226, 230)
                        )
                        DropdownMenuItem(
                            text = { Text(text = "피드백 보내기", color = Color.Black) },
                            onClick = { /* 피드백 처리 */ }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.zIndex(1f) // TopAppBar를 내용 위에 겹치도록 설정
            )
            // 상세정보가 있다면 데이터 로딩
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

                        Box {
                            // 이미지 슬라이더 (HorizontalPager)
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

                            // todo: 북마크
                        }

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
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 프로필 이미지
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(profileImageUrl ?: R.drawable.ic_launcher_foreground)
                                        .apply {
                                            placeholder(R.drawable.ic_launcher_foreground)
                                            error(R.drawable.ic_launcher_foreground)
                                            transformations(CircleCropTransformation())
                                        }.build()
                                ),
                                contentDescription = "작성자 프로필",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .border(0.5.dp, Color.Gray, CircleShape)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

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

                            recipeDetail.subCategoryDtoList.forEachIndexed { index, subCategory ->
                                // 서브 카테고리 이름과 조건부로 쉼표 및 공백 추가
                                Text(
                                    text = buildString {
                                        append(subCategory.subCategoryNm.orEmpty())
                                        // 리스트의 크기가 1 초과이고, 현재 요소가 마지막 요소가 아닐 경우, 쉼표와 공백 추가
                                        if (recipeDetail.subCategoryDtoList.size > 1 && index < recipeDetail.subCategoryDtoList.size - 1) {
                                            append(", ")
                                        }
                                    },
                                    fontSize = 12.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 16.dp, vertical = 16.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color(222, 226, 230) // 구분선의 색상 설정
                        )
                    }

                    item {
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
                        // 소요 시간
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            verticalAlignment = Alignment.CenterVertically // 세로축 중앙 정렬
                        ) {
                            Text(
                                text = "소요 시간",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // 좌측 텍스트를 위해 가중치 적용
                            )
                            Text(
                                text = "${recipeDetail.timeTaken}분",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // 우측 텍스트를 위해 가중치 적용
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // 재료
                        Row(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            verticalAlignment = Alignment.CenterVertically // 세로축 중앙 정렬
                        ) {
                            Text(
                                text = "재료",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // 좌측 텍스트를 위해 가중치 적용
                            )
                            Text(
                                text = recipeDetail.ingredient,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // 우측 텍스트를 위해 가중치 적용
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // 해시태그
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "해시태그",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = recipeDetail.hashtag,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f) // 우측 텍스트를 위해 가중치 적용
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth() // 전체 너비를 채우도록 설정
                                .padding(horizontal = 16.dp, vertical = 8.dp), // 양쪽에 패딩 적용
                            thickness = 0.5.dp, // 구분선의 두께 설정
                            color = Color(222, 226, 230) // 구분선의 색상 설정
                        )

                        // 영양 정보
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "영양 정보",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Column(
                                modifier = Modifier.weight(1f) // 우측 텍스트를 위해 가중치 적용
                            ) {
                                recipeDetail.nutritionalInfoDto?.let { info ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // 여기에 아이콘 추가, 예를 들어 Image(...)를 사용
                                        Text(
                                            text = "탄수화물: ${info.carbohydrates}g",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // 아이콘 추가
                                        Text(
                                            text = "단백질: ${info.protein}g",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // 아이콘 추가
                                        Text(
                                            text = "지방: ${info.fat}g",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // 아이콘 추가
                                        Text(
                                            text = "비타민: ${info.vitamins}g",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // 아이콘 추가
                                        Text(
                                            text = "미네랄: ${info.minerals}g",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}