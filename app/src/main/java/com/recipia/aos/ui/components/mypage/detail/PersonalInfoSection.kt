package com.recipia.aos.ui.components.mypage.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.model.mypage.MyPageViewModel

@Composable
fun PersonalInfoSection(
    myPageViewModel: MyPageViewModel
) {
    val myPageData = myPageViewModel.myPageData.value

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 생년월일 AssistChip (값이 있는 경우에만 표시)
            if (!myPageData?.birth.isNullOrEmpty()) {
                AssistChip(
                    onClick = { /* 클릭 시 수행할 작업 */ },
                    label = {
                        if (myPageData != null) {
                            myPageData.birth?.let { Text(text = it) }
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Cake,
                            contentDescription = "생일",
                            modifier = Modifier
                                .size(16.dp), // 아이콘 크기 조절
                            tint = Color.Black // 아이콘 색상을 검은색으로 설정
                        )
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = AssistChipDefaults.assistChipColors(),
                    border = AssistChipDefaults.assistChipBorder(Color(233, 236, 239)),

                    )
            }

            // 성별 AssistChip (값이 있는 경우에만 표시)
            if (!myPageData?.gender.isNullOrEmpty()) {
                val (icon, label) = when (myPageData?.gender) {
                    "M" -> Pair(Icons.Filled.Male, "남성")
                    "F" -> Pair(Icons.Filled.Female, "여성")
                    else -> Pair(null, "")
                }

                if (icon != null && label.isNotEmpty()) {
                    AssistChip(
                        onClick = { /* 클릭 시 수행할 작업 */ },
                        label = { Text(text = label) },
                        leadingIcon = icon.let {
                            {
                                Icon(
                                    it,
                                    contentDescription = label,
                                    modifier = Modifier
                                        .size(16.dp), // 아이콘 크기 조절
                                    tint = Color.Black // 아이콘 색상을 검은색으로 설정
                                )
                            }
                        },
                        trailingIcon = null,
                        shape = MaterialTheme.shapes.small,
                        colors = AssistChipDefaults.assistChipColors(),
                        border = AssistChipDefaults.assistChipBorder(Color(233, 236, 239)),
                    )
                }
            }
        }
    }
}
