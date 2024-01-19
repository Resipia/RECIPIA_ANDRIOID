package com.recipia.aos.ui.components.login.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CheckboxWithViewButton(
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
