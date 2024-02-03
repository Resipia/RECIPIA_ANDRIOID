package com.recipia.aos.ui.components.forgot.email

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * 이메일 찾기 화면
 * 추후 sns 로그인이 생기면 이 화면을 사용할 예정
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindEmailScreen(
    navController: NavController
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White),
                title = {
                    Text(
                        text = "이메일 찾기",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // 뒤로가기 버튼 동작
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // TopAppBar 배경을 투명하게 설정
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 내부 컨텐츠
            RadioOption("이름,전화번호로 찾기", "email", selectedOption) {
                selectedOption = "email"
            }

            Button(
                onClick = {
                    // 선택된 옵션에 따라 다른 화면으로 이동
                    when (selectedOption) {
                        "email" -> navController.navigate("emailVerificationScreen")
                        "sns" -> navController.navigate("snsVerificationScreen")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(206, 212, 218), // 버튼 배경색
                    contentColor = Color.Black // 버튼 내부 글자색
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth() // 최대 너비 설정
                    .padding(vertical = 25.dp, horizontal = 16.dp) // 버튼에 좌우 패딩 추가
            ) {
                Text(text = "다음")
            }
        }
    }
}

@Composable
fun RadioOption(
    label: String,
    optionId: String,
    selectedOption: String?,
    onOptionSelected: () -> Unit
) {
    val isSelected = (selectedOption == optionId)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth() // 최대 너비 설정
            .padding(8.dp)
            .clickable {
                onOptionSelected()
            }
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    onOptionSelected()
                }
            },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = Color.Gray
            ),
            modifier = Modifier.clickable {
                onOptionSelected()
            }
        )

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    onOptionSelected()
                }
        )
    }
}
