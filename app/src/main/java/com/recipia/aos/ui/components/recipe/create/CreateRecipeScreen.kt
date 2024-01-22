package com.recipia.aos.ui.components.recipe.create

import TokenManager
import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.dto.recipe.RecipeCreateUpdateRequestDto
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel


/**
 * 레시피 생성 필드
 */
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    navController: NavController,
    viewModel: CategorySelectionViewModel,
    recipeCreateModel: RecipeCreateModel,
    tokenManager: TokenManager
) {

    val recipeName = recipeCreateModel.recipeName.value
    val recipeDesc = recipeCreateModel.recipeDesc.value
    val timeTaken = recipeCreateModel.timeTaken.value
    val ingredient = recipeCreateModel.ingredient.value
    val hashtag = recipeCreateModel.hashtag.value
    val nutritionalInfoList = recipeCreateModel.nutritionalInfoList
    val context = LocalContext.current // 현재 컨텍스트를 가져옴
    val showNutritionalInfo = mutableStateOf(false)

    // 여기에서 레시피 생성 모델 인스턴스 생성
    val model = RecipeCreateModel(tokenManager)

    // 선택한 이미지 URI
    var selectedImageUris = recipeCreateModel.selectedImageUris

    val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = {
            recipeCreateModel.selectedImageUris.clear()
            recipeCreateModel.selectedImageUris.addAll(it)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "레시피 생성",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        recipeCreateModel.clearData()
                        viewModel.selectedCategories.value = emptySet()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "닫기"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // 데이터 전송 로직
                    val lastNutritionalInfo = nutritionalInfoList.lastOrNull() ?: NutritionalInfoDto()
                    val subCategoryDtoList = viewModel.createSubCategoryDtoList(viewModel.selectedCategories.value)

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
                    model.sendRecipeToServer(
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
                            viewModel.selectedCategories.value = emptySet()

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
                    .padding(16.dp)
            ) {
                Text("작성 완료")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // "이미지 선택" 버튼
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0073E6),
                        contentColor = Color.White
                    ),
                    onClick = {
                        multiplePhotosPickerLauncher.launch(
                            // 이미지만 선택 가능하도록
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }) {

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_input_add),
                            contentDescription = "Add Image"
                        )
                        Text(
                            text = "Pick Photos",
                            style = TextStyle(
                                fontSize = 18.sp
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
                        selectedImageUris.filter { it != removedUri }.toMutableList() as SnapshotStateList<Uri?> // 변경
                }
            }
            item {
                TextField(
                    value = recipeCreateModel.recipeName.value,
                    onValueChange = { newValue ->
                        recipeCreateModel.recipeName.value = newValue
                    },
                    label = { Text("레시피 이름") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }
            item {
                TextField(
                    value = recipeCreateModel.recipeDesc.value,
                    onValueChange = { newValue ->
                        recipeCreateModel.recipeDesc.value = newValue
                    },
                    label = { Text("레시피 설명") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }
            item {
                TextField(
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
                )
            }
            item {
                TextField(
                    value = recipeCreateModel.ingredient.value,
                    onValueChange = { newValue ->
                        recipeCreateModel.ingredient.value = newValue
                    },
                    label = { Text("재료") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }
            item {
                TextField(
                    value = recipeCreateModel.hashtag.value,
                    onValueChange = { newValue ->
                        recipeCreateModel.hashtag.value = newValue
                    },
                    label = { Text("해시태그") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // "영양소 입력하기" 버튼
                    Button(
                        onClick = { showNutritionalInfo.value = !showNutritionalInfo.value },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text("영양소 입력하기", fontSize = 12.sp)
                    }

                    // "카테고리 선택" 버튼
                    Button(
                        onClick = { navController.navigate("categories") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text("카테고리 선택", fontSize = 12.sp)
                    }
                }
            }
            // 영양소 입력 영역
            if (showNutritionalInfo.value) {
                item {
                    NutritionalInfoInputScreen(nutritionalInfoList.lastOrNull() ?: NutritionalInfoDto()) { updatedInfo ->
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
                Text("선택된 카테고리: ${viewModel.selectedCategories.value.joinToString()}")
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
