package com.recipia.aos.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.HorizontalDivider
import com.recipia.aos.ui.dto.Category
import com.recipia.aos.ui.dto.SubCategory
import com.recipia.aos.ui.dto.SubCategoryDto
import com.recipia.aos.ui.model.category.CategorySelectionViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel

/**
 * 카테고리 조회 화면
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CategorySelectRecipeScreen(
    navController: NavController,
    viewModel: CategorySelectionViewModel,
    recipeAllListViewModel: RecipeAllListViewModel,
    onSelectedCategories: (Set<Int>) -> Unit
) {
    val subCategories = listOf(
        SubCategory(2, "냉면", 1),
        SubCategory(3, "국밥", 1),
        SubCategory(4, "반찬", 1),
        SubCategory(5, "찜닭", 1),
        SubCategory(6, "칼국수", 1),
        SubCategory(7, "감자탕", 1),
        SubCategory(8, "부대찌개", 1),
        SubCategory(9, "김치찜", 1),
        SubCategory(10, "삼겹살", 1),
        SubCategory(11, "김치찌개", 1),
        SubCategory(12, "죽", 1),
        SubCategory(53, "기타", 1),

        SubCategory(26, "떡볶이", 2),
        SubCategory(27, "김밥", 2),
        SubCategory(28, "우동", 2),
        SubCategory(29, "순대", 2),
        SubCategory(30, "꽈배기", 2),
        SubCategory(54, "기타", 2),

        SubCategory(5, "찜닭", 3),
        SubCategory(7, "감자탕", 3),
        SubCategory(9, "김치찜", 3),
        SubCategory(32, "아구찜", 3),
        SubCategory(33, "갈비탕", 3),
        SubCategory(34, "설렁탕", 3),
        SubCategory(35, "삼계탕", 3),
        SubCategory(36, "갈비찜", 3),
        SubCategory(37, "닭볶음탕", 3),
        SubCategory(38, "해물찜", 3),
        SubCategory(55, "기타", 3),

        SubCategory(39, "곱창", 4),
        SubCategory(10, "삼겹살", 4),
        SubCategory(40, "갈비", 4),
        SubCategory(56, "기타", 4),

        SubCategory(41, "마라탕", 5),
        SubCategory(42, "짜장면", 5),
        SubCategory(43, "양꼬치", 5),
        SubCategory(57, "기타", 5),

        SubCategory(44, "커피/차", 6),
        SubCategory(45, "간식", 6),
        SubCategory(46, "와플", 6),
        SubCategory(47, "케이크", 6),
        SubCategory(48, "토스트", 6),
        SubCategory(49, "빙수", 6),
        SubCategory(50, "아이스크림", 6),
        SubCategory(51, "도넛", 6),
        SubCategory(58, "기타", 6),

        SubCategory(13, "치킨", 7),
        SubCategory(14, "돈까스", 7),
        SubCategory(15, "족발/보쌈", 7),
        SubCategory(16, "피자", 7),
        SubCategory(17, "일식", 7),
        SubCategory(18, "회/해물", 7),
        SubCategory(19, "양식", 7),
        SubCategory(20, "아시안", 7),
        SubCategory(21, "샌드위치", 7),
        SubCategory(22, "샐러드", 7),
        SubCategory(23, "버거", 7),
        SubCategory(24, "멕시칸", 7),
        SubCategory(52, "기타", 7),
        SubCategory(25, "도시락", 7),
    )

    val categories = listOf(
        Category(1, "한식"),
        Category(2, "분식"),
        Category(3, "찜/탕"),
        Category(4, "구이"),
        Category(5, "중식"),
        Category(6, "디저트"),
        Category(7, "기타"),
    )

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
        bottomBar = {
            Button(
                onClick = {
                    // 사용자가 선택한 서브 카테고리 ID 리스트를 추출
                    val selectedIds = selectedSubCategories.map { it.id }
                    recipeAllListViewModel.setSubCategories(selectedIds)
                    // 현재 화면을 스택에서 제거하고 홈 화면으로 이동
                    navController.popBackStack()
                    navController.navigate("home") //todo: 여기서 전달해야하나?
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(27, 94, 32)
                ),
                shape = MaterialTheme.shapes.small.copy(CornerSize(10.dp))
            ) {
                Text(
                    "조회하기",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White) // 여기에 배경색을 하얀색으로 설정,
                .padding(innerPadding)
                .padding(horizontal = 24.dp) // 좌우 패딩 추가
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
            ) {
                groupedSubCategories.forEach { (categoryId, subCategoryList) ->
                    // 카테고리 이름 찾기
                    val categoryName = categoryNameMap[categoryId] ?: "Unknown Category"

                    // 대카테고리 이름 스타일 조정
                    item {
                        Text(
                            categoryName,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                            fontWeight = FontWeight.Bold,
                            color = Color(27, 94, 32),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(), // 전체 너비를 채우도록 설정
                            thickness = 1.dp, // 구분선의 두께 설정
                            color = Color(222, 226, 230) // 구분선의 색상 설정
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    item {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth(),
//                                .padding(all = 8.dp)
                            horizontalArrangement = Arrangement.Start,
                            verticalArrangement = Arrangement.Top,
                            maxItemsInEachRow = Int.MAX_VALUE
                        ) {
                            subCategoryList.forEach { subCategory ->
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
                                    label = {
                                        Text(
                                            subCategory.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontSize = 14.sp, // 글자 크기 조정
                                                fontWeight = FontWeight.Bold // 글자 굵기 조정
                                            )
                                        )
                                    },
                                    selected = selectedSubCategories.any { it.id == subCategory.id.toLong() },
                                    leadingIcon = if (selectedSubCategories.any { it.id == subCategory.id.toLong() }) {
                                        { Icon(Icons.Filled.Done, contentDescription = "Selected", modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp), // 각 칩의 간격 조절
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(233, 236, 239) // 선택된 칩의 배경색
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}