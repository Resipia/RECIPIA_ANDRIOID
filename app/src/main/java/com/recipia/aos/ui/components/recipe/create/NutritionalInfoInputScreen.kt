package com.recipia.aos.ui.components.recipe.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.dto.recipe.NutritionalInfoDto
import com.recipia.aos.ui.model.recipe.create.RecipeCreateModel

/**
 * 영양소 입력 필드
 * NutritionalInfoDto의 각 필드는 사용자가 입력을 할 때마다 즉시 업데이트되고 (onNutritionalInfoChanged(updatedInfo) 적용)
 * 최종적으로 상위 컴포넌트나 ViewModel에 전달될 수 있게 되어,
 * 사용자가 "저장" 버튼을 눌렀을 때 최신 상태의 데이터를 서버에 전송할 준비가 완료된다.
 */
@Composable
fun NutritionalInfoInputScreen(
    nutritionalInfo: NutritionalInfoDto,
    onNutritionalInfoChanged: (NutritionalInfoDto) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        OutlinedTextField(
            value = nutritionalInfo.carbohydrates?.toString() ?: "",
            onValueChange = { text ->
                val updatedInfo = nutritionalInfo.copy(carbohydrates = text.toIntOrNull())
                onNutritionalInfoChanged(updatedInfo)
            },
            label = { Text("탄수화물 함량 (g)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nutritionalInfo.protein?.toString() ?: "",
            onValueChange = { text ->
                val updatedInfo = nutritionalInfo.copy(protein = text.toIntOrNull())
                onNutritionalInfoChanged(updatedInfo)
            },
            label = { Text("단백질 함량 (g)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nutritionalInfo.fat?.toString() ?: "",
            onValueChange = { text ->
                val updatedInfo = nutritionalInfo.copy(fat = text.toIntOrNull())
                onNutritionalInfoChanged(updatedInfo)
            },
            label = { Text("지방 함량 (g)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nutritionalInfo.vitamins?.toString() ?: "",
            onValueChange = { text ->
                val updatedInfo = nutritionalInfo.copy(vitamins = text.toIntOrNull())
                onNutritionalInfoChanged(updatedInfo)
            },
            label = { Text("비타민 함량 (mg)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nutritionalInfo.minerals?.toString() ?: "",
            onValueChange = { text ->
                val updatedInfo = nutritionalInfo.copy(minerals = text.toIntOrNull())
                onNutritionalInfoChanged(updatedInfo)
            },
            label = { Text("미네랄 함량 (mg)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}