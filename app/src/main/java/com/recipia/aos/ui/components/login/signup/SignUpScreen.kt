package com.recipia.aos.ui.components.login.signup

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController
) {

    var isCheckedAll by remember { mutableStateOf(false) }
    var isCheckedTerms by remember { mutableStateOf(false) }
    var isCheckedPrivacy by remember { mutableStateOf(false) }
    var isCheckedOutsourcing by remember { mutableStateOf(false) }
    var isCheckedOptionalPrivacy by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "회원가입",
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

        // 첫 번째 체크박스 영역 (모두 동의)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isCheckedAll = !isCheckedAll
                    isCheckedTerms = isCheckedAll
                    isCheckedPrivacy = isCheckedAll
                    isCheckedOutsourcing = isCheckedAll
                    isCheckedOptionalPrivacy = isCheckedAll
                }
        ) {
            Checkbox(
                checked = isCheckedAll,
                onCheckedChange = {
                    isCheckedAll = it
                    isCheckedTerms = it
                    isCheckedPrivacy = it
                    isCheckedOutsourcing = it
                    isCheckedOptionalPrivacy = it
                }
            )

            Text(
                text = "모두 동의합니다",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // 두 번째 체크박스 영역 (이용약관 동의)
        CheckboxWithViewButton(
            label = "이용약관 동의 (필수)",
            isChecked = isCheckedTerms,
            onCheckedChange = { isCheckedTerms = it }
        )

        // 세 번째 체크박스 영역 (개인정보 수집 및 이용 동의)
        CheckboxWithViewButton(
            label = "개인정보 수집 및 이용 동의 (필수)",
            isChecked = isCheckedPrivacy,
            onCheckedChange = { isCheckedPrivacy = it }
        )

        // 네 번째 체크박스 영역 (개인정보 처리 위탁 동의)
        CheckboxWithViewButton(
            label = "개인정보 처리 위탁 동의 (필수)",
            isChecked = isCheckedOutsourcing,
            onCheckedChange = { isCheckedOutsourcing = it }
        )

        // 다섯 번째 체크박스 영역 (개인정보 수집 및 이용 동의 선택)
        CheckboxWithViewButton(
            label = "개인정보 수집 및 이용 동의 (선택)",
            isChecked = isCheckedOptionalPrivacy,
            onCheckedChange = { isCheckedOptionalPrivacy = it }
        )

        // "취소" 및 "동의" 버튼 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    // 뒤로 가기 버튼 동작
                    navController.popBackStack()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "취소")
            }

            Button(
                onClick = {
                    // 동의 버튼 동작 추가, 회원가입 페이지로 이동
                    navController.navigate("회원가입 페이지로 이동")
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = "동의")
            }
        }
    }
}

@Composable
private fun CheckboxWithViewButton(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically // 중앙 정렬로 변경
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedChange(!isChecked) },
            modifier = Modifier
                .padding(end = 8.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .clickable { onCheckedChange(!isChecked) }
                .align(Alignment.CenterVertically) // 체크박스와 텍스트 중앙 정렬
        )

        TextButton(
            onClick = {
                // "보기" 버튼 동작 추가, 해당 내용을 보여주는 페이지로 이동
            },
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            Text(text = "보기")
        }
    }
}
