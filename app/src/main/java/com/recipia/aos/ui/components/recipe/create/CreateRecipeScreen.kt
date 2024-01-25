package com.recipia.aos.ui.components.recipe.create

import TokenManager
import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraEnhance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.dto.recipe.RecipeCreateUpdateRequestDto
import com.recipia.aos.ui.dto.search.SearchType
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel
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
fun CreateRecipeScreen(
    navController: NavController,
    categorySelectionViewModel: CategorySelectionViewModel,
    mongoSearchViewModel: MongoSearchViewModel,
    recipeCreateModel: RecipeCreateModel
) {

    val recipeName = recipeCreateModel.recipeName.value
    val recipeDesc = recipeCreateModel.recipeDesc.value
    val timeTaken = recipeCreateModel.timeTaken.value
    val ingredient = recipeCreateModel.ingredient.value
    val hashtag = recipeCreateModel.hashtag.value
    val nutritionalInfoList = recipeCreateModel.nutritionalInfoList
    val context = LocalContext.current // 현재 컨텍스트를 가져옴
    val showNutritionalInfo = mutableStateOf(false)
    // mongo Model에서 데이터를 가져온다.
    val selectedIngredients by mongoSearchViewModel.selectedIngredients.collectAsState()
    val selectedHashtags by mongoSearchViewModel.selectedHashtags.collectAsState()
    // 선택한 이미지 URI
    var selectedImageUris = recipeCreateModel.selectedImageUris

    // 사진 선택기 선언
    val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = {
            recipeCreateModel.selectedImageUris.clear()
            recipeCreateModel.selectedImageUris.addAll(it)
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
                            categorySelectionViewModel.selectedCategories.value =
                                emptySet() // 카테고리 선택 초기화
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
                        // 데이터 전송 로직
                        val lastNutritionalInfo =
                            nutritionalInfoList.lastOrNull() ?: NutritionalInfoDto()
                        val subCategoryDtoList =
                            categorySelectionViewModel.createSubCategoryDtoList(
                                categorySelectionViewModel.selectedCategories.value
                            )

                        val requestDto = RecipeCreateUpdateRequestDto(
                            id = null,
                            recipeName = recipeName,
                            recipeDesc = recipeDesc,
                            timeTaken = timeTaken.toIntOrNull() ?: 0,
                            ingredient = ingredient,
                            hashtag = hashtag,
                            nutritionalInfo = lastNutritionalInfo,
                            subCategoryDtoList = subCategoryDtoList,
                            deleteFileOrder = listOf()
                        )

                        // 모델을 사용하여 서버로 데이터와 이미지 전송
                        recipeCreateModel.sendRecipeToServer(
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
                                nutritionalInfoList.clear()
                                recipeCreateModel.selectedImageUris = mutableStateListOf<Uri?>()
                                categorySelectionViewModel.selectedCategories.value = emptySet()

                                Toast.makeText(context, "레시피 생성 성공", Toast.LENGTH_SHORT).show()
                                // 추가적인 성공 로직
                            }
                        ) { errorMessage ->
                            Toast.makeText(context, "레시피 생성 실패: $errorMessage", Toast.LENGTH_LONG)
                                .show()
                            // 추가적인 실패 로직
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
                item {
                    OutlinedTextField(
                        value = recipeCreateModel.recipeName.value,
                        onValueChange = { newValue ->
                            recipeCreateModel.recipeName.value = newValue
                        },
                        label = { Text("레시피 이름") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(189, 189, 189), // 포커스가 됐을 때의 테두리 색상
                            unfocusedBorderColor = Color(189, 189, 189), // 포커스가 해제됐을 때의 테두리 색상
                        )
                    )
                }
                item {
                    OutlinedTextField(
                        value = recipeCreateModel.recipeDesc.value,
                        onValueChange = { newValue ->
                            recipeCreateModel.recipeDesc.value = newValue
                        },
                        label = { Text("레시피 설명") },
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
                }
                item {
                    OutlinedTextField(
                        value = recipeCreateModel.timeTaken.value,
                        onValueChange = { newValue ->
                            recipeCreateModel.timeTaken.value = newValue
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
                    Button(
                        onClick = { navController.navigate("search-Screen/${SearchType.INGREDIENT.name}") }, // 백틱(`) 사용
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
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
                            .fillMaxWidth(),
//                            .padding(start = 16.dp, top = 4.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top,
                        maxItemsInEachRow = Int.MAX_VALUE
                    ) {
                        selectedIngredients.forEach { ingredient ->
                            ElevatedAssistChip(
                                onClick = {},
                                label = { Text(ingredient) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(200,230,201),
                                    labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                                )
//                            elevation = null, // 그림자 제거
//                            border = null, // 테두리 제거
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
                            .padding(vertical = 8.dp)
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
                            .fillMaxWidth(),
//                            .padding(start = 16.dp, top = 4.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalArrangement = Arrangement.Top,
                        maxItemsInEachRow = Int.MAX_VALUE
                    ) {
                        selectedHashtags.forEach { hashtag ->
                            ElevatedAssistChip(
                                onClick = {},
                                label = { Text(hashtag) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(200,230,201),
                                    labelColor = Color.Black // 내부 텍스트 및 아이콘 색상
                                )
//                            elevation = null, // 그림자 제거
//                            border = null, // 테두리 제거
                            )
                            Spacer(modifier = Modifier.width(4.dp)) // 여백 추가
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showNutritionalInfo.value = !showNutritionalInfo.value },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    238,
                                    238,
                                    238
                                )
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .height(48.dp), // 높이 지정
                            shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                            border = BorderStroke(1.dp, Color(189, 189, 189)), // 태두리 설정
                        ) {
                            Text("영양소 입력", fontSize = 14.sp, color = Color.Black)
                        }

                        Button(
                            onClick = { navController.navigate("categorySelect") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    238,
                                    238,
                                    238
                                )
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                                .height(48.dp), // 높이 지정
                            shape = RoundedCornerShape(8.dp), // 모서리 둥글게
                            border = BorderStroke(1.dp, Color(189, 189, 189)), // 태두리 설정
                        ) {
                            Text("카테고리 선택", fontSize = 14.sp, color = Color.Black)
                        }
                    }
                }

                // 영양소 입력 영역
                if (showNutritionalInfo.value) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))

                        NutritionalInfoInputScreen(
                            nutritionalInfoList.lastOrNull() ?: NutritionalInfoDto()
                        ) { updatedInfo ->
                            // 사용자가 입력한 영양소 정보 가져오기
                            val carbohydrates = updatedInfo.carbohydrates
                            val protein = updatedInfo.protein
                            val fat = updatedInfo.fat
                            val vitamins = updatedInfo.vitamins
                            val minerals = updatedInfo.minerals

                            // 영양소 정보를 NutritionalInfoDto 객체로 생성
                            val nutritionalInfoDto = NutritionalInfoDto(
                                carbohydrates = carbohydrates,
                                protein = protein,
                                fat = fat,
                                vitamins = vitamins,
                                minerals = minerals
                            )

                            // 생성된 NutritionalInfoDto 객체를 nutritionalInfoList에 추가
                            nutritionalInfoList.add(nutritionalInfoDto)
                        }
                    }
                }

                // 카테고리 정보 표시
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Text 텍스트 변경
                    Text(
                        "선택된 카테고리:",
                        fontSize = 14.sp, // 글씨 크기 조절
                        color = Color.Black, // 텍스트 색상 설정
                        fontWeight = FontWeight.Bold // 텍스트 굵기 설정
                    )

                    // viewModel에서 선택한 카테고리 값을 가져옴
                    val selectedCategories = categorySelectionViewModel.selectedCategories.value

                    Spacer(modifier = Modifier.height(6.dp))

                    // 선택한 카테고리를 가로로 나열하기 위해 Row 사용
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 각 AssistChip과 Spacer를 추가
                        selectedCategories.forEachIndexed { index, category ->
                            AssistChip(
                                onClick = { /* 각 AssistChip 클릭 시 동작 */ },
                                label = {
                                    Text(
                                        category.subCategoryNm.toString(),
                                        fontSize = 12.sp, // 글씨 크기 조절
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(238, 238, 238),
                                    labelColor = Color.Black, // 내부 텍스트 색상
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    borderColor = Color(189, 189, 189)
                                )
                            )

                            // Spacer를 추가하여 간격 설정
                            if (index < selectedCategories.size - 1) {
                                Spacer(modifier = Modifier.width(4.dp)) // 원하는 간격 설정
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ImageThumbnails(selectedImageUris: List<Uri?>, onRemoveImage: (Uri) -> Unit) {
    LazyRow {
        items(selectedImageUris) { uri ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize()
                )
                // X 아이콘을 클릭하여 이미지 제거
                IconButton(
                    onClick = { onRemoveImage(uri!!) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove Image",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
