package com.recipia.aos.ui.components.forgot.email

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindIdScreen(
    navController: NavController
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "아이디 찾기",
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // 여기에 innerPadding 적용
                .padding(16.dp), // 추가적인 외부 패딩
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 내부 컨텐츠
            RadioOption("이메일 인증", "email", selectedOption) {
                selectedOption = "email"
            }

            RadioOption("SNS 계정 휴대폰 번호 인증", "sns", selectedOption) {
                selectedOption = "sns"
            }

            Button(
                onClick = {
                    // 선택된 옵션에 따라 다른 화면으로 이동
                    when (selectedOption) {
                        "email" -> navController.navigate("emailVerificationScreen")
                        "sns" -> navController.navigate("snsVerificationScreen")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth() // 최대 너비 설정
                    .padding(vertical = 16.dp)
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

@Preview
@Composable
fun FindIdScreenPreview() {
    val navController = rememberNavController() // 네비게이션 컨트롤러를 위한 임시 객체
    FindIdScreen(navController)
}
