package com.recipia.aos.ui.components.recipe.create

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.dto.recipe.RecipeCreateUpdateRequestDto
import com.recipia.aos.ui.dto.search.SearchType
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel
import com.recipia.aos.ui.model.recipe.read.RecipeDetailViewModel
import com.recipia.aos.ui.model.search.MongoSearchViewModel


/**
 * 레시피 생성 필드
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class,
    ExperimentalLayoutApi::class
)
@Composable
fun UpdateRecipeScreen(
    navController: NavController,
    recipeDetailViewModel: RecipeDetailViewModel?, // 작성했던 데이터 받기
    categorySelectionViewModel: CategorySelectionViewModel,
    recipeCreateModel: RecipeCreateModel,
    mongoSearchViewModel: MongoSearchViewModel
) {

    // 상태 변수를 사용하여 각 필드에 데이터 바인딩
    val recipeName = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.recipeName ?: "") }

    val recipeDesc = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.recipeDesc ?: "") }

    val timeTaken = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.timeTaken.toString()) }

    val ingredient = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.ingredient ?: "") }

    val hashtag = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.hashtag ?: "") }

    var selectedImageUris = remember { mutableStateListOf<Uri?>() } // 이미지 URI 목록

    val nutritionalInfo = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.nutritionalInfoDto ?: NutritionalInfoDto()) } // 영양 정보 상태 변수

    val selectedCategories = remember { mutableStateOf(
        recipeDetailViewModel?.recipeDetail?.value?.subCategoryDtoList ?: emptyList()) }  // 선택된 카테고리 상태 변수

    val context = LocalContext.current // 현재 컨텍스트를 가져옴
    val showNutritionalInfo = mutableStateOf(false)

    // 필수 필드에 대한 유효성 상태
    val isRecipeNameValid = remember { mutableStateOf(true) }
    val isRecipeDescValid = remember { mutableStateOf(true) }
    val isCategorySelected = remember { mutableStateOf(true) }

    // 문자열을 리스트로 변환하는 함수
    fun String.toList(): List<String> {
        return this.split(", ").filter { it.isNotBlank() }
    }

    LaunchedEffect(key1 = Unit) {
        val initialIngredients = recipeDetailViewModel?.recipeDetail?.value?.ingredient?.toList() ?: emptyList()
        val initialHashtags = recipeDetailViewModel?.recipeDetail?.value?.hashtag?.toList() ?: emptyList()
        mongoSearchViewModel.initializeSelectedIngredientsAndHashtags(initialIngredients, initialHashtags)
    }

    // mongo Model에서 데이터를 가져온다. todo: 이거 상세보기에서 받은 데이터로 바꿔서 세팅해줘야함
    val selectedIngredients by mongoSearchViewModel.selectedIngredients.collectAsState()
    val selectedHashtags by mongoSearchViewModel.selectedHashtags.collectAsState()

    // 사진 선택기 선언
    val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = {
            selectedImageUris.clear()
            selectedImageUris.addAll(it)
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
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    modifier = Modifier.background(Color.White),
                    title = {
                        Text(
                            text = "레시피 작성",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            categorySelectionViewModel.selectedCategories.value = emptySet() // 카테고리 선택 초기화
                            mongoSearchViewModel.changeInitialized() // 초기상태값 초기화
                            navController.popBackStack()
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
                        isCategorySelected.value = categorySelectionViewModel.selectedCategories.value.isNotEmpty()

                        // 유효성 검증 실시
                        if (isRecipeNameValid.value && isRecipeDescValid.value && isCategorySelected.value) {

                            val subCategoryDtoList =
                                categorySelectionViewModel.createSubCategoryDtoList(
                                    categorySelectionViewModel.selectedCategories.value
                                )

                            // 'selectedIngredients'와 'selectedHashtags'를 문자열로 변환
                            val ingredientsString = selectedIngredients.joinToString(separator = ", ")
                            val hashtagsString = selectedHashtags.joinToString(separator = ", ")

                            val requestDto = RecipeCreateUpdateRequestDto(
                                id = recipeDetailViewModel?.recipeDetail?.value?.id, // 수정할 레시피의 ID
                                recipeName = recipeName.value, // .value를 사용하여 실제 문자열 값 추출
                                recipeDesc = recipeDesc.value, // .value를 사용하여 실제 문자열 값 추출
                                timeTaken = timeTaken.value.toIntOrNull() ?: 0,
                                ingredient = ingredientsString,
                                hashtag = hashtagsString,
                                nutritionalInfo = nutritionalInfo.value, // 수정된 영양 정보
                                subCategoryDtoList = subCategoryDtoList,
                                deleteFileOrder = listOf()
                            )

                            // 모델을 사용하여 서버로 데이터와 이미지 전송
                            recipeCreateModel.sendRecipeToServer(
                                requestDto = requestDto,
                                imageUris = selectedImageUris,
                                context = context,
                                onSuccess = { recipeId ->
                                    // 서버로 데이터 전송 성공 후에 상태 초기화
                                    recipeCreateModel.recipeName.value = ""
                                    recipeCreateModel.recipeDesc.value = ""
                                    recipeCreateModel.timeTaken.value = ""
                                    recipeCreateModel.ingredient.value = ""
                                    recipeCreateModel.hashtag.value = ""
                                    nutritionalInfo.value = NutritionalInfoDto() // 새 NutritionalInfoDto 객체로 초기화
                                    recipeCreateModel.selectedImageUris = mutableStateListOf<Uri?>()
                                    categorySelectionViewModel.selectedCategories.value = emptySet()
                                    // MongoSearchViewModel 내의 선택된 재료와 해시태그를 초기화
                                    mongoSearchViewModel.resetSelectedIngredients()
                                    mongoSearchViewModel.resetSelectedHashtags()

                                    Toast.makeText(context, "레시피 업데이트 성공", Toast.LENGTH_SHORT).show()

                                    // 레시피 상세보기 화면으로 네비게이션
                                    navController.navigate("recipeDetail/$recipeId")
                                }
                            ) { errorMessage ->
                                Toast.makeText(context, "레시피 업데이트 실패: $errorMessage", Toast.LENGTH_LONG).show()
                            }
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
                        "작성 완료",
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
                    ImageThumbnails(selectedImageUris) { removedUri ->
                        // 이미지 제거
                        selectedImageUris =
                            selectedImageUris.filter { it != removedUri }
                                .toMutableList() as SnapshotStateList<Uri?> // 변경
                    }
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
                        modifier = Modifier.fillMaxWidth(),
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
                            .height(150.dp),
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
                        modifier = Modifier.fillMaxWidth(),
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
                        onClick = { navController.navigate("search-Screen/${SearchType.INGREDIENT.name}") }, // 백틱(`) 사용
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top,
                        maxItemsInEachRow = Int.MAX_VALUE
                    ) {
                        selectedIngredients.forEach { ingredient ->
                            ElevatedAssistChip(
                                onClick = {},
                                label = { Text(ingredient) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(200, 230, 201),
                                    labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp)) // 여백 추가
                        }
                    }
                }

                // 해시태그 버튼
                item {
                    Button(
                        onClick = { navController.navigate("search-Screen/${SearchType.HASHTAG.name}") }, // 백틱(`) 사용
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top,
                        maxItemsInEachRow = Int.MAX_VALUE
                    ) {
                        selectedHashtags.forEach { hashtag ->
                            ElevatedAssistChip(
                                onClick = {},
                                label = { Text("#$hashtag") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(200, 230, 201),
                                    labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                                )
                            )
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
                    // selectedCategories 상태 변수에서 카테고리 목록을 가져옴
                    val categories = selectedCategories.value

                    // 선택한 카테고리를 가로로 나열하기 위해 Row 사용
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 각 AssistChip과 Spacer를 추가
                        categories.forEachIndexed { index, category ->
                            ElevatedAssistChip(
                                onClick = { /* 각 AssistChip 클릭 시 동작 */ },
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

                            // Spacer를 추가하여 간격 설정
                            if (index < categories.size - 1) {
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

