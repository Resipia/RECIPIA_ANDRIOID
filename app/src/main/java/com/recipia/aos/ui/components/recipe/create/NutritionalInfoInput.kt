package com.recipia.aos.ui.components.recipe.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.dto.recipecreate.NutritionalInfoDto

/**
 * 영양소 입력 필드
 */
@Composable
fun NutritionalInfoInput(
    nutritionalInfo: NutritionalInfoDto,
    onNutritionalInfoChanged: (NutritionalInfoDto) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("영양소 입력하기", fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = nutritionalInfo.carbohydrates?.toString() ?: "",
            onValueChange = { text ->
                onNutritionalInfoChanged(nutritionalInfo.copy(carbohydrates = text.toIntOrNull()))
            },
            label = { Text("탄수화물 함량 (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
        )

        OutlinedTextField(
            value = nutritionalInfo.protein?.toString() ?: "",
            onValueChange = { text ->
                onNutritionalInfoChanged(nutritionalInfo.copy(protein = text.toIntOrNull()))
            },
            label = { Text("단백질 함량 (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
        )

        OutlinedTextField(
            value = nutritionalInfo.fat?.toString() ?: "",
            onValueChange = { text ->
                onNutritionalInfoChanged(nutritionalInfo.copy(fat = text.toIntOrNull()))
            },
            label = { Text("지방 함량 (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
        )

        OutlinedTextField(
            value = nutritionalInfo.vitamins?.toString() ?: "",
            onValueChange = { text ->
                onNutritionalInfoChanged(nutritionalInfo.copy(vitamins = text.toIntOrNull()))
            },
            label = { Text("비타민 함량 (mg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
        )

        OutlinedTextField(
            value = nutritionalInfo.minerals?.toString() ?: "",
            onValueChange = { text ->
                onNutritionalInfoChanged(nutritionalInfo.copy(minerals = text.toIntOrNull()))
            },
            label = { Text("미네랄 함량 (mg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth() // 너비를 화면 전체로 설정
        )

        // "영양소 입력하기" 버튼
        Button(onClick = {
            // 새로운 입력 필드를 추가하면서 현재 입력된 값을 보존합니다.
            val newNutritionalInfo = NutritionalInfoDto(
                carbohydrates = nutritionalInfo.carbohydrates,
                protein = nutritionalInfo.protein,
                fat = nutritionalInfo.fat,
                vitamins = nutritionalInfo.vitamins,
                minerals = nutritionalInfo.minerals
            )
            onNutritionalInfoChanged(newNutritionalInfo)
        }) {
            Text("영양소 입력하기")
        }
    }
}