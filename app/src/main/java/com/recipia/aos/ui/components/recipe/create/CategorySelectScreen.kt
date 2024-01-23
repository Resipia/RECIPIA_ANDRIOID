package com.recipia.aos.ui.components.recipe.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.dto.Category
import com.recipia.aos.ui.dto.SubCategory
import com.recipia.aos.ui.dto.SubCategoryDto
import com.recipia.aos.ui.model.category.CategorySelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectScreen(
    navController: NavController, // NavController 인자 추가
    viewModel: CategorySelectionViewModel,
    subCategories: List<SubCategory>,
    categories: List<Category>,
    onSelectedCategories: (Set<Int>) -> Unit // 콜백 함수 추가
) {
    // Category ID를 Category 이름으로 매핑
    val categoryNameMap = categories.associate { it.id to it.name }

    // 서브카테고리를 categoryId 별로 그룹화
    val groupedSubCategories = subCategories.groupBy { it.categoryId }

    // 선택된 서브 카테고리를 추적하는 상태
    var selectedSubCategories by remember { mutableStateOf(setOf<SubCategoryDto>()) }


    Scaffold(
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White), // 여기에 배경색을 하얀색으로 설정,
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.selectedCategories.value = emptySet() // 카테고리 선택 초기화
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White) // 여기에 배경색을 하얀색으로 설정,
                .padding(innerPadding)
                .padding(horizontal = 24.dp) // 좌우 패딩 추가
        ) {
            Column(
                modifier = Modifier.background(Color.White) // 내부 Column 배경색 설정
            ) {
                groupedSubCategories.forEach { (categoryId, subCategoryList) ->

                    // Category 이름 찾기
                    val categoryName = categoryNameMap[categoryId] ?: "Unknown Category"

                    // 대카테고리 이름 스타일 조정
                    Text(
                        categoryNameMap[categoryId] ?: "Unknown Category",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    // 서브 카테고리 리스트 스타일 조정
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(bottom = 12.dp)
                    ) {
                        items(subCategoryList.size) { index ->
                            val subCategory = subCategoryList[index]
                            FilterChip(
                                onClick = {
                                    // 선택 로직
                                    val currentSelection = selectedSubCategories.toMutableSet()
                                    val subCategoryDto = SubCategoryDto(subCategory.id.toLong(), subCategory.name)
                                    if (currentSelection.any { it.id.toInt() == subCategory.id }) {
                                        currentSelection.removeIf { it.id.toInt() == subCategory.id }
                                    } else {
                                        if (currentSelection.size < 3) {
                                            currentSelection.add(subCategoryDto)
                                        }
                                    }
                                    selectedSubCategories = currentSelection
                                },
                                label = { Text(subCategory.name) },
                                selected = selectedSubCategories.any { it.id == subCategory.id.toLong() },
                                leadingIcon = if (selectedSubCategories.any { it.id == subCategory.id.toLong() }) {
                                    { Icon(Icons.Filled.Done, contentDescription = "Selected") }
                                } else null,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(20.dp))

                Button(
                    // 선택 완료 버튼 클릭 로직
                    onClick = {
                        viewModel.setSelectedCategories(selectedSubCategories)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White) // 여기에 배경색을 하얀색으로 설정,
                        .padding(2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("선택 완료", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}