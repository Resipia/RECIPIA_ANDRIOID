package com.recipia.aos.ui.components.signup.function

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.Woman
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 성별 선택을 위한 커스텀 컴포저블
@Composable
fun GenderSelector(selectedGender: String, onGenderSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        GenderBox(
            gender = "남성",
            isSelected = selectedGender == "남성",
            onSelect = { onGenderSelect("남성") },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        GenderBox(
            gender = "여성",
            isSelected = selectedGender == "여성",
            onSelect = { onGenderSelect("여성") },
            modifier = Modifier.weight(1f)
        )
    }
}

// 성별 선택 박스
@Composable
fun GenderBox(gender: String, isSelected: Boolean, onSelect: () -> Unit, modifier: Modifier) {
    val deepPurpleColor = Color(27, 94, 32) // 예시 색상: 초록색

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isSelected) deepPurpleColor else Color.Gray,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (gender == "남성") Icons.Default.Man else Icons.Default.Woman,
            contentDescription = null,
            tint = if (isSelected) deepPurpleColor else Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = gender,
            style = if (isSelected) MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = deepPurpleColor
            ) else MaterialTheme.typography.bodyMedium
        )
    }
}
