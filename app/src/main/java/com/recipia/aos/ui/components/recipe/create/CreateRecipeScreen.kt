package com.recipia.aos.ui.components.recipe.create

import TokenManager
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.dto.recipe.RecipeCreateUpdateRequestDto
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel


/**
 * 레시피 생성 필드
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(
    navController: NavController,
    viewModel: CategorySelectionViewModel,
    tokenManager: TokenManager
) {
    val recipeName = remember { mutableStateOf("") }
    val recipeDesc = remember { mutableStateOf("") }
    val timeTaken = remember { mutableStateOf("") }
    val ingredient = remember { mutableStateOf("") }
    val hashtag = remember { mutableStateOf("") }
    val nutritionalInfoList = remember { mutableStateListOf<NutritionalInfoDto>() }
    val showNutritionalInfo = remember { mutableStateOf(false) }
    val context = LocalContext.current // 현재 컨텍스트를 가져옴

    // 여기에서 레시피 생성 모델 인스턴스 생성
    val model = RecipeCreateModel(tokenManager)

    // 선택한 이미지 URI
    var selectedImageUris by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }

    val multiplePhotosPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(
            maxItems = 10
        ),
        onResult = {
            selectedImageUris = it
        }
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("레시피 생성", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
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
                        recipeName = recipeName.value,
                        recipeDesc = recipeDesc.value,
                        timeTaken = timeTaken.value.toIntOrNull() ?: 0,
                        ingredient = ingredient.value,
                        hashtag = hashtag.value,
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
                    selectedImageUris = selectedImageUris.filter { it != removedUri }
                }
            }
            item {
                TextField(
                    value = recipeName.value,
                    onValueChange = { recipeName.value = it },
                    label = { Text("레시피 이름") },
                    modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
                )
            }
            item {
                TextField(
                    value = recipeDesc.value,
                    onValueChange = { recipeDesc.value = it },
                    label = { Text("레시피 설명") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp) // 높이 지정
                )
            }
            item {
                TextField(
                    value = timeTaken.value,
                    onValueChange = { timeTaken.value = it },
                    label = { Text("소요 시간 (분)") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
                )
            }
            item {
                TextField(
                    value = ingredient.value,
                    onValueChange = { ingredient.value = it },
                    label = { Text("재료") },
                    modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
                )
            }
            item {
                TextField(
                    value = hashtag.value,
                    onValueChange = { hashtag.value = it },
                    label = { Text("해시태그") },
                    modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
                )
            }
            // "영양소 입력하기" 버튼
            item {
                Button(
                    onClick = {
                        // 버튼을 클릭하면 영양소 입력 영역 표시 여부를 변경
                        showNutritionalInfo.value = !showNutritionalInfo.value
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("영양소 입력하기")
                }
            }
            // 영양소 입력 필드 영역 (버튼 누르면 필드 열림)
            if (showNutritionalInfo.value) {
                item {
                    NutritionalInfoInputScreen(nutritionalInfoList.lastOrNull() ?: NutritionalInfoDto()) { updatedInfo ->
                        nutritionalInfoList.add(updatedInfo)
                    }
                }
            }
            item {
                // 카테고리 선택 버튼
                Button(
                    onClick = {
                        navController.navigate("categories")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("카테고리 선택")
                }
            }

            item {
                // 선택된 카테고리 정보 표시
                Text("선택된 카테고리: ${viewModel.selectedCategories.value.joinToString()}")
            }
            // 이미지 업로드 UI 추가

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
                    painter = rememberImagePainter(uri),
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
