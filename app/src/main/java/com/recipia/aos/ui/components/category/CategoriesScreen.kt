package com.recipia.aos.ui.components.category

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.dto.Category
import com.recipia.aos.ui.dto.SubCategory
import com.recipia.aos.ui.model.category.CategorySelectionViewModel

/**
 * 이 코드는 LazyRow를 사용하여 주어진 서브 카테고리 목록에 대해 FilterChip을 생성합니다.
 * 각 FilterChip은 해당 서브 카테고리의 이름을 표시하며, 선택된 카테고리는 selectedCategories 세트에 저장됩니다.
 * 사용자가 칩을 클릭하면, 선택 상태가 변경되고, 최대 3개의 카테고리만 선택될 수 있도록 로직이 구현되어 있습니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
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
    var selectedSubCategories by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("카테고리 선택", style = MaterialTheme.typography.titleLarge) },
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
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp) // 좌우 패딩 추가
        ) {
            Column {
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
                            .padding(bottom = 12.dp)
                    ) {
                        items(subCategoryList.size) { index ->
                            val subCategory = subCategoryList[index]
                            FilterChip(
                                onClick = {
                                    // 선택 로직
                                    val currentSelection = selectedSubCategories.toMutableSet()
                                    if (subCategory.id in selectedSubCategories) {
                                        currentSelection.remove(subCategory.id)
                                    } else {
                                        if (currentSelection.size < 3) {
                                            currentSelection.add(subCategory.id)
                                        }
                                    }
                                    selectedSubCategories = currentSelection
                                },
                                label = { Text(subCategory.name) },
                                selected = subCategory.id in selectedSubCategories,
                                leadingIcon = if (subCategory.id in selectedSubCategories) {
                                    { Icon(Icons.Filled.Done, contentDescription = "Selected") }
                                } else null,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(20.dp))
                Button(
                    onClick = {
                        viewModel.setSelectedCategories(selectedSubCategories)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("선택 완료", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}