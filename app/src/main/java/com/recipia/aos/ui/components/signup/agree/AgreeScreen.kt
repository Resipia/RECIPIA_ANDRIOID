package com.recipia.aos.ui.components.signup.agree

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// 개인정보 수집 및 이용 동의
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoConsentScreen(
    navController: NavController
) {
    Scaffold(
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White), // 여기에 배경색을 하얀색으로 설정,
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "개인정보 수집•이용 동의서")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "[레시피아]는 「개인정보보호법」에 의거하여, 아래와 같은 내용으로 개인정보를 수집하고 있습니다. 아래 내용을 자세히 읽어 보시고, 모든 내용을 이해하신 후에 동의 여부를 결정해 주시기 바랍니다.")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "I. 개인정보의 수집 및 이용 동의서")
            Text(text = "이용자가 제공한 모든 정보는 다음의 목적을 위해 활용하며, 하기 목적 이외의 용도로는 사용되지 않습니다.")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "① 개인정보 수집 항목 및 수집·이용 목적: 가) 수집 항목 (필수항목) - 성명(국문), 전화번호(휴대전화), 이메일 등 지원 신청서에 기재된 정보 또는 신청자가 제공한 정보 나) 수집 및 이용 목적 - 회원 식별 및 회원 서비스 이용")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "② 개인정보 보유 및 이용 기간: 수집·이용 동의일로부터 개인정보의 수집·이용 목적을 달성할 때까지")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "③ 동의거부관리: 귀하께서는 본 안내에 따른 개인정보 수집, 이용에 대하여 동의를 거부하실 권리가 있습니다. 다만, 귀하가 개인정보의 수집·이용에 동의를 거부하시는 경우에 [레시피아] 사용 과정에 있어 불이익이 발생할 수 있음을 알려드립니다.")
        }
    }
}


// 개인정보 보관 및 파기 동의
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataRetentionConsent(
    navController: NavController
) {
    Scaffold(
        containerColor = Color.White, // Scaffold의 배경색을 하얀색으로 설정
        topBar = {
            TopAppBar(
                modifier = Modifier.background(Color.White), // 여기에 배경색을 하얀색으로 설정,
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "개인정보 보관•파기 동의서")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "[레시피아]는 아래 내용에 따라 개인정보의 보관 및 파기 절차가 이루어집니다. 아래 내용을 자세히 읽어 보시고, 모든 내용을 이해하신 후에 동의 여부를 결정해 주시기 바랍니다.")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "I. 개인정보의 보관 및 파기")
            Text(text = "가) 보관 기간: 이용자의 개인정보는 회원 탈퇴 시까지 보관되며, 탈퇴 후에는 아래의 ‘나) 파기 절차 및 방법’에 따라 처리됩니다.")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "나) 파기 절차 및 방법: 이용자가 회원에서 탈퇴하거나 개인정보 수집 및 이용 동의를 철회한 경우, 해당 이용자의 개인정보는 즉시 재생할 수 없는 방법으로 파기합니다. 단, 법령에 따라 보존의 필요성이 있는 경우를 제외하고, 탈퇴 후 1년 동안은 이용자의 개인정보를 안전하게 보관한 후, 그 이후에 파기하여 처리합니다. 파기된 개인정보는 어떠한 용도로도 조회나 이용이 불가능합니다.")
        }
    }
}