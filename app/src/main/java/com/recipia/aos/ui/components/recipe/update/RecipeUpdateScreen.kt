package com.recipia.aos.ui.components.recipe.update

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.recipe.create.ImageThumbnails
import com.recipia.aos.ui.components.recipe.create.NutritionalInfoInputScreen
import com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.api.dto.recipe.RecipeCreateUpdateRequestDto
import com.recipia.aos.ui.api.dto.search.SearchType
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import com.recipia.aos.ui.model.search.MongoSearchViewModel
import kotlinx.coroutines.launch


/**
 * 레시피 업데이트 컴포저
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun RecipeUpdateScreen(
    navController: NavController,
    recipeDetailViewModel: RecipeDetailViewModel?, // 작성했던 데이터 받기
    categorySelectionViewModel: CategorySelectionViewModel,
    recipeCreateModel: RecipeCreateModel,
    mongoSearchViewModel: MongoSearchViewModel
) {

    // 상태 변수를 사용하여 각 필드에 데이터 바인딩
    val recipeName = rememberSaveable {
        mutableStateOf(
            recipeDetailViewModel?.recipeDetail?.value?.recipeName ?: ""
        )
    }
    val recipeDesc = rememberSaveable {
        mutableStateOf(
            recipeDetailViewModel?.recipeDetail?.value?.recipeDesc ?: ""
        )
    }
    val timeTaken =
        rememberSaveable { mutableStateOf(recipeDetailViewModel?.recipeDetail?.value?.timeTaken.toString()) }

    // 기존 이미지 URL들을 Uri 객체로 변환하여 저장할 리스트
    val selectedImageUris = remember { mutableStateListOf<Uri?>() }
    val nutritionalInfo = remember {
        mutableStateOf(
            recipeDetailViewModel?.recipeDetail?.value?.nutritionalInfoDto ?: com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto()
        )
    } // 영양 정보 상태 변수
    val context = LocalContext.current // 현재 컨텍스트를 가져옴
    val showNutritionalInfo = mutableStateOf(false)
    val selectedIngredients by mongoSearchViewModel.selectedIngredients.collectAsState()
    val selectedHashtags by mongoSearchViewModel.selectedHashtags.collectAsState()
    val recipeId = recipeDetailViewModel?.recipeDetail?.value?.id

    // 필수 필드에 대한 유효성 상태
    val isRecipeNameValid = remember { mutableStateOf(true) }
    val isRecipeDescValid = remember { mutableStateOf(true) }
    val isCategorySelected = remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정

    // 문자열을 리스트로 변환하는 함수
    fun String.toList(): List<String> {
        return this.split(", ").filter { it.isNotBlank() }
    }

    // 초기 재료/해시태그 값 세팅
    LaunchedEffect(key1 = Unit) {
        val initialIngredients =
            recipeDetailViewModel?.recipeDetail?.value?.ingredient?.toList() ?: emptyList()
        val initialHashtags =
            recipeDetailViewModel?.recipeDetail?.value?.hashtag?.toList() ?: emptyList()
        mongoSearchViewModel.initializeSelectedIngredientsAndHashtags(
            initialIngredients,
            initialHashtags
        )
    }

    // selectedCategoriesBefore 상태를 rememberSaveable을 사용하여 유지
    val selectedCategoriesBefore = rememberSaveable {
        mutableStateOf(
            recipeDetailViewModel?.recipeDetail?.value?.subCategoryDtoList ?: emptyList()
        )
    }

    // 초기 카테고리 값 세팅에 대한 LaunchedEffect
    LaunchedEffect(key1 = true) { // key를 true로 고정하여 이 컴포저블이 처음 로드될 때만 실행되도록 함
        if (!categorySelectionViewModel.isInitialized.value) { // ViewModel의 초기화 상태를 체크
            categorySelectionViewModel.initializeCategories(selectedCategoriesBefore.value.toSet())
            categorySelectionViewModel.setInitialized(true) // 초기화 완료 상태로 설정
        }
    }

    // 초기 이미지에 기존 저장되어있던 이미지 정보 추가
    LaunchedEffect(key1 = recipeDetailViewModel?.recipeDetail) {
        recipeDetailViewModel?.recipeDetail?.value?.recipeFileUrlList?.forEach { fileResponse ->
            selectedImageUris.add(Uri.parse(fileResponse.preUrl))
        }
    }

    // 사진 선택기 선언
    val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = { uris ->
//            selectedImageUris.clear()
            // 기존 이미지를 유지하면서 새로운 이미지 추가
            selectedImageUris.addAll(uris)
        }
    )

    // 키보드 컨트롤러 (터치시 키보드 닫히게 하기)
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // 이벤트를 감지하여 키보드를 숨깁니다.
                detectTapGestures(
                    onPress = { /* 터치 감지 시 수행할 동작 */ },
                    onTap = { keyboardController?.hide() }
                )
            }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(Color.White),
                    title = {
                        Text(
                            text = "레시피 수정",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            mongoSearchViewModel.changeInitialized() // 몽고db 검색 초기값 세팅 후 initial값 변경
                            mongoSearchViewModel.resetSelectedIngredients()
                            mongoSearchViewModel.resetSelectedHashtags()
                            categorySelectionViewModel.clearSelectedCategories()
                            selectedImageUris.clear() // 이미지 제거

                            navController.popBackStack()
                            navController.navigate("recipeDetail/${recipeId}")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "닫기")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                Button(
                    onClick = {
                        isRecipeNameValid.value = recipeName.value.isNotBlank()
                        isRecipeDescValid.value = recipeDesc.value.isNotBlank()
                        isCategorySelected.value =
                            categorySelectionViewModel.selectedCategories.value.isNotEmpty()

                        // 유효성 검증 실시
                        if (isRecipeNameValid.value && isRecipeDescValid.value && isCategorySelected.value) {

                            // 서브 카테고리를 가져오는 로직
                            val subCategoryDtoList =
                                categorySelectionViewModel.createSubCategoryDtoList(
                                    categorySelectionViewModel.selectedCategories.value
                                )

                            // 'selectedIngredients'와 'selectedHashtags'를 문자열로 변환
                            val ingredientsString =
                                selectedIngredients.joinToString(separator = ", ")
                            val hashtagsString = selectedHashtags.joinToString(separator = ", ")

                            val requestDto =
                                com.recipia.aos.ui.api.dto.recipe.RecipeCreateUpdateRequestDto(
                                    id = recipeDetailViewModel?.recipeDetail?.value?.id, // 수정할 레시피의 ID todo: 여기서 1이 들어옴
                                    recipeName = recipeName.value, // .value를 사용하여 실제 문자열 값 추출
                                    recipeDesc = recipeDesc.value, // .value를 사용하여 실제 문자열 값 추출
                                    timeTaken = timeTaken.value.toIntOrNull() ?: 0,
                                    ingredient = ingredientsString,
                                    hashtag = hashtagsString,
                                    nutritionalInfo = nutritionalInfo.value, // 수정된 영양 정보 (여기에 id값 넣어줘야함)
                                    subCategoryDtoList = subCategoryDtoList,
                                    deleteFileOrder = listOf()
                                )

                            // 모델을 사용하여 서버로 데이터와 이미지 전송
                            recipeCreateModel.updateRecipeRequest(
                                requestDto = requestDto,
                                imageUris = selectedImageUris,
                                context = context,
                                onSuccess = {
                                    // 서버로 데이터 전송 성공 후에 상태 초기화
                                    recipeCreateModel.recipeName.value = ""
                                    recipeCreateModel.recipeDesc.value = ""
                                    recipeCreateModel.timeTaken.value = ""
                                    recipeCreateModel.ingredient.value = ""
                                    recipeCreateModel.hashtag.value = ""
                                    nutritionalInfo.value =
                                        com.recipia.aos.ui.api.dto.recipe.NutritionalInfoDto() // 새 NutritionalInfoDto 객체로 초기화
                                    recipeCreateModel.selectedImageUris = mutableStateListOf<Uri?>()
                                    categorySelectionViewModel.selectedCategories.value = emptySet()
                                    mongoSearchViewModel.resetSelectedIngredients()
                                    mongoSearchViewModel.resetSelectedHashtags()
                                    mongoSearchViewModel.changeInitialized() // 몽고db 검색 초기값 세팅 후 initial값 변경
                                    categorySelectionViewModel.clearSelectedCategories()
                                    selectedImageUris.clear() // 이미지 제거

                                    // 오류 발생 시 스낵바 알림
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "레시피가 업데이트 되었습니다.",
                                            duration = SnackbarDuration.Short
                                        )
                                    }

                                    recipeDetailViewModel?.triggerUpdate()
                                    // 레시피 상세보기 화면으로 네비게이션
                                    navController.navigate("recipeDetail/${recipeId}")
                                }
                            ) { errorMessage ->
                                // 오류 발생 시 스낵바 알림
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "레시피 업데이트 실패: $errorMessage",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        } else {
                            keyboardController?.hide()
                        }
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
                        "수정하기",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .imePadding() // 키보드가 활성화될 때 패딩 적용
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // "이미지 선택" 버튼
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White, // 배경색을 흰색으로 변경
                            contentColor = Color(238, 238, 238) // 텍스트 색상을 회색으로 변경
                        ),
                        border = BorderStroke(1.dp, Color(189, 189, 189)), // 태두리 설정
                        onClick = {
                            multiplePhotosPickerLauncher.launch(
                                // 이미지만 선택 가능하도록
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }) {

                        Row(
//                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.CameraEnhance,
                                contentDescription = "이미지 선택",
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp)) // 여백 추가
                            Text(
                                text = "이미지 선택",
                                color = Color.Black,
                                style = TextStyle(
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }

                item {
                    // 이미지 썸네일 목록
                    ImageThumbnails(
                        selectedImageUris = selectedImageUris,
                        onRemoveImage = { uriToRemove ->
                            // 이미지 제거 로직
                            selectedImageUris.remove(uriToRemove)
                        },
                        onMove = { fromIndex, toIndex ->
                            // 순서 변경 로직
                            // 이미지 순서 변경 로직
                            val updatedList = selectedImageUris.toMutableList().apply {
                                add(toIndex, removeAt(fromIndex))
                            }
                            selectedImageUris.clear()
                            selectedImageUris.addAll(updatedList)
                        }
                    )
                }

                // 레시피 이름 작성 필드
                item {
                    OutlinedTextField(
                        value = recipeName.value,
                        onValueChange = { newValue ->
                            recipeName.value = newValue
                            isRecipeNameValid.value = newValue.isNotBlank() // 유효성 검사
                        },
                        label = { Text("레시피 이름 (*)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(189, 189, 189), // 포커스가 됐을 때의 테두리 색상
                            unfocusedBorderColor = Color(189, 189, 189), // 포커스가 해제됐을 때의 테두리 색상
                        )
                    )
                    if (!isRecipeNameValid.value) {
                        Text("필수 입력값입니다.", color = Color.Red)
                    }
                }

                // 레시피 설명 작성 필드
                item {
                    OutlinedTextField(
                        value = recipeDesc.value,
                        onValueChange = { newValue ->
                            recipeDesc.value = newValue
                            isRecipeDescValid.value = newValue.isNotBlank() // 유효성 검사
                        },
                        label = { Text("레시피 설명 (*)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(189, 189, 189), // 포커스가 됐을 때의 테두리 색상
                            unfocusedBorderColor = Color(189, 189, 189), // 포커스가 해제됐을 때의 테두리 색상
                        )
                    )
                    if (!isRecipeDescValid.value) {
                        Text("필수 입력값입니다.", color = Color.Red)
                    }
                }

                // 소요 시간 작성 필드
                item {
                    OutlinedTextField(
                        value = timeTaken.value,
                        onValueChange = { newValue ->
                            timeTaken.value = newValue
                        },
                        label = { Text("소요 시간 (분)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(189, 189, 189), // 포커스가 됐을 때의 테두리 색상
                            unfocusedBorderColor = Color(189, 189, 189), // 포커스가 해제됐을 때의 테두리 색상
                        )
                    )
                }

                // 재료 버튼
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("search-Screen/${com.recipia.aos.ui.api.dto.search.SearchType.INGREDIENT.name}") }, // 백틱(`) 사용
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(189, 189, 189)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(238, 238, 238)
                        ),
                    ) {
                        Text("재료 검색", fontSize = 14.sp, color = Color.Black)
                    }
                }

                // 선택된 재료를 AssistChip으로 표시
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top,
                        maxItemsInEachRow = Int.MAX_VALUE
                    ) {
                        selectedIngredients.forEach { ingredient ->
                            Box(contentAlignment = Alignment.TopEnd) {
                                ElevatedAssistChip(
                                    onClick = {
                                        // 클릭한 재료 삭제
                                        mongoSearchViewModel.removeSelectedIngredient(ingredient)
                                    },
                                    label = { Text(ingredient) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(200, 230, 201),
                                        labelColor = Color.Black
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(
                                        borderColor = Color(189, 189, 189)
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        // 클릭한 재료 삭제
                                        mongoSearchViewModel.removeSelectedIngredient(ingredient)
                                    },
                                    modifier = Modifier
                                        .size(20.dp) // 아이콘 버튼의 크기 조절
                                        .offset(x = (1).dp, y = 7.dp) // 아이콘 버튼을 우측 상단으로 조정
                                        .padding(0.dp) // 필요한 경우 패딩 조정
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "삭제",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp) // 아이콘 크기 조절
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp)) // 여백 추가
                        }
                    }
                }

                // 해시태그 버튼
                item {
                    Button(
                        onClick = { navController.navigate("search-Screen/${com.recipia.aos.ui.api.dto.search.SearchType.HASHTAG.name}") }, // 백틱(`) 사용
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(189, 189, 189)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(238, 238, 238)
                        ),
                    ) {
                        Text("해시태그 검색", fontSize = 14.sp, color = Color.Black)
                    }
                }

                // 선택된 해시태그를 AssistChip으로 표시
                item {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top,
                        maxItemsInEachRow = Int.MAX_VALUE
                    ) {
                        selectedHashtags.forEach { hashtag ->
                            Box(contentAlignment = Alignment.TopEnd) {
                                ElevatedAssistChip(
                                    onClick = {
                                        // 클릭한 해시태그 삭제
                                        mongoSearchViewModel.removeSelectedHashtag(hashtag)
                                    },
                                    label = { Text("#$hashtag") },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(200, 230, 201),
                                        labelColor = Color.Black
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(
                                        borderColor = Color(189, 189, 189)
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        // 클릭한 해시태그 삭제
                                        mongoSearchViewModel.removeSelectedHashtag(hashtag)
                                    },
                                    modifier = Modifier
                                        .size(20.dp) // 아이콘 버튼의 크기 조절
                                        .offset(x = (1).dp, y = 7.dp) // 아이콘 버튼을 우측 상단으로 조정
                                        .padding(0.dp) // 필요한 경우 패딩 조정
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "삭제",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp) // 아이콘 크기 조절
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp)) // 여백 추가
                        }
                    }
                }


                // 카테고리 선택 버튼
                item {
                    Button(
                        onClick = { navController.navigate("categorySelect") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(189, 189, 189)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(238, 238, 238)
                        )
                    ) {
                        Text("카테고리 선택 (*)", fontSize = 14.sp, color = Color.Black)
                    }
                    // 카테고리 선택 경고 메시지 표시 조건 수정
                    if (!isCategorySelected.value) {
                        Text("카테고리 선택은 필수입니다.", color = Color.Red)
                    }
                }

                // 카테고리 정보 표시
                item {
                    // 선택한 카테고리를 가로로 나열하기 위해 Row 사용
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 각 AssistChip과 Spacer를 추가
                        categorySelectionViewModel.selectedCategories.value.forEachIndexed { index, category ->
                            Box(contentAlignment = Alignment.TopEnd) {
                                ElevatedAssistChip(
                                    onClick = {
                                        // 여기서 클릭한 카테고리 삭제
                                        categorySelectionViewModel.removeSelectedCategory(category)
                                    },
                                    label = {
                                        Text(
                                            category.subCategoryNm.toString(),
                                            fontSize = 12.sp, // 글씨 크기 조절
                                        )
                                    },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = Color(200, 230, 201),
                                        labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                                    ),
                                    border = AssistChipDefaults.assistChipBorder(
                                        borderColor = Color(189, 189, 189)
                                    )
                                )
                                IconButton(
                                    onClick = {
                                        // 여기서 클릭한 카테고리 삭제
                                        categorySelectionViewModel.removeSelectedCategory(category)
                                    },
                                    modifier = Modifier
                                        .size(20.dp) // 아이콘 버튼의 크기 조절
                                        .offset(x = (1).dp, y = 7.dp) // 아이콘 버튼을 우측 상단으로 조정
                                        .padding(0.dp) // 필요한 경우 패딩 조정
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "삭제",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp) // 아이콘 크기 조절
                                    )
                                }
                            }

                            // Spacer를 추가하여 간격 설정
                            if (index < categorySelectionViewModel.selectedCategories.value.size - 1) {
                                Spacer(modifier = Modifier.width(4.dp)) // 원하는 간격 설정
                            }
                        }
                    }
                }

                // 영양소 입력 버튼
                item {
                    Button(
                        onClick = { showNutritionalInfo.value = !showNutritionalInfo.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(189, 189, 189)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(238, 238, 238)
                        )
                    ) {
                        Text("영양소 입력", fontSize = 14.sp, color = Color.Black)
                    }
                }

                // 영양소 입력 영역
                if (showNutritionalInfo.value) {
                    item {
                        NutritionalInfoInputScreen(
                            nutritionalInfo = nutritionalInfo.value,
                            onNutritionalInfoChanged = { updatedInfo ->
                                nutritionalInfo.value = updatedInfo
                            }
                        )
                    }
                }
            }
        }
    }
}

