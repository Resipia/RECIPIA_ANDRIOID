package com.recipia.aos.ui.components.recipe.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    recipeAllListViewModel: RecipeAllListViewModel,
) {
    // 스낵바 설정
    val snackbarHostState = remember { SnackbarHostState() }
    val lazyListState = rememberLazyListState() // LazyListState 인스턴스 생성

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        bottomBar = {
            BottomNavigationBar(
                navController,
                snackbarHostState,
                recipeAllListViewModel,
                lazyListState
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 30.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            DockedSearchBarM3()
        }
    }
}