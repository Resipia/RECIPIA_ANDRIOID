package com.recipia.aos.ui.components.recipe.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.dto.recipecreate.NutritionalInfoDto


/**
 * 레시피 생성 필드
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRecipeScreen(navController: NavController) {
    val scaffoldState = rememberScaffoldState()
    val recipeName = remember { mutableStateOf("") }
    val recipeDesc = remember { mutableStateOf("") }
    val timeTaken = remember { mutableStateOf("") }
    val ingredient = remember { mutableStateOf("") }
    val hashtag = remember { mutableStateOf("") }
    val nutritionalInfoList = remember { mutableStateListOf<NutritionalInfoDto>() }
    val showNutritionalInfo = remember { mutableStateOf(false) }
    val selectedCategories = remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("레시피 생성") }) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 56.dp), // TopAppBar의 높이(56.dp)만큼 추가 패딩을 제공하여 아래부터 시작
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                    NutritionalInfoInput(nutritionalInfoList.lastOrNull() ?: NutritionalInfoDto()) { updatedInfo ->
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
                Text("선택된 카테고리: ${selectedCategories.value.joinToString()}")
            }
            items(nutritionalInfoList.size) { index ->
                // 추가된 영양소 정보 입력 필드
                NutritionalInfoInput(nutritionalInfoList[index]) { updatedInfo ->
                    nutritionalInfoList[index] = updatedInfo
                }
            }
            // 이미지 업로드 UI 추가
        }
    }
}

// 이미지 업로드 및 기타 필요한 기능 구현
