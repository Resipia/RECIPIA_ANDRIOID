package com.recipia.aos.ui.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.recipia.aos.ui.api.dto.chat.ChatRoomDto
import com.recipia.aos.ui.components.BottomNavigationBar
import com.recipia.aos.ui.components.menu.CustomDropdownMenu
import com.recipia.aos.ui.model.chat.ChatViewModel
import com.recipia.aos.ui.model.recipe.read.RecipeAllListViewModel
import kotlinx.coroutines.launch

@Composable
fun ChatRoomScaffoldScreen(
    chatViewModel: ChatViewModel,
    navController: NavController,
    recipeAllListViewModel: RecipeAllListViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // 스낵바 설정
    var menuExpanded by remember { mutableStateOf(false) }// 드롭다운 메뉴 상태
    val lazyListState = rememberLazyListState() // LazyListState 인스턴스 생성

    Scaffold(
        topBar = {
            androidx.compose.material.TopAppBar(
                elevation = 0.dp,
                modifier = Modifier.background(Color.White), // 여기에 배경색을 하얀색으로 설정,
                backgroundColor = Color.White,
                title = {
                    // 여기서 로고와 텍스트를 Row로 배치합니다.
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 로고 텍스트
                        Text(
                            text = "Recipia",
                            color = Color(27, 94, 32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                        )
                    }
                },
                actions = {
                    // 검색 아이콘
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "레시피 통합검색 기능이 곧 추가됩니다."
                                )
                            }
                            navController.navigate("recipe-search")
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = "검색")
                    }

                    // 더보기 아이콘
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "더보기"
                        )
                    }

                    // 맨 우측 드롭다운 메뉴 설정
                    CustomDropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            text = { Text("문의/피드백 보내기", color = Color.Black) },
                            onClick = { navController.navigate("ask-create") }
                        )
                    }
                },
//                scrollBehavior = scrollBehavior
            )
        },
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        modifier = Modifier.background(Color.White),
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                snackbarHostState = snackbarHostState,
                recipeAllListViewModel = recipeAllListViewModel,
                lazyListState = lazyListState
            )
        }
    ) { paddingValues ->
        ChatRoomListScreen(
            viewModel = chatViewModel,
            modifier = Modifier.padding(paddingValues) // 여기에 패딩을 적용합니다
        )
    }
}

@Composable
fun ChatRoomListScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier // Modifier 매개변수 추가
) {

    val chatRooms by viewModel.chatRooms.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 에러 메시지가 있을 경우 스낵바를 표시합니다.
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = errorMessage!!,
                duration = SnackbarDuration.Short
            )
            viewModel.clearErrorMessage()
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(chatRooms ?: listOf(), key = { it.id!! }) { chatRoom ->
            ChatRoomItem(chatRoom = chatRoom)
        }
    }
}

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoomDto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            tint = Color.LightGray
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = chatRoom.roomIdentifier, // 여기에 채팅방 이름 또는 사용자 이름을 표시
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "마지막 메시지 내용", // 마지막 메시지 내용
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "오후 3:45", // 마지막 메시지 시간
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}