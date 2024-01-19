package com.recipia.aos.ui.components.login.signup

import android.widget.Toast
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

/**
 * 회원가입 동의여부 페이지
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpAgreeScreen(
    navController: NavController
) {

    var isCheckedAll by remember { mutableStateOf(false) }
    var isCheckedTerms by remember { mutableStateOf(false) }
    var isCheckedPrivacy by remember { mutableStateOf(false) }
    var isCheckedOutsourcing by remember { mutableStateOf(false) }
    var isCheckedOptionalPrivacy by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

        // "동의" 버튼을 눌렀을 때의 동작
        val onAgreeButtonClick: () -> Unit = {
            // 필수 항목을 모두 동의했는지 확인
            if (isCheckedTerms && isCheckedPrivacy && isCheckedOutsourcing) {
                // 동의한 경우 회원가입 페이지로 이동
                navController.navigate("signUpForm")
            } else {
                // 필수 항목 중 하나라도 동의하지 않은 경우 토스트 메시지 표시
                Toast.makeText(context, "필수 항목은 동의해주셔야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // "동의" 버튼 영역
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
                    .fillMaxWidth()
            ) {
                Text(text = "취소")
            }

            Button(
                onClick = onAgreeButtonClick, // 동의 버튼 동작 변경
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(text = "동의")
            }
        }
    }
}

