import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.recipia.aos.ui.dto.recipecreate.NutritionalInfoDto
import com.recipia.aos.ui.theme.RecipiaaosTheme

@Composable
fun NutritionalInfoForm(
    nutritionalInfoDto: NutritionalInfoDto?,
    onRecipeInfoChanged: (NutritionalInfoDto?) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("영양소 정보")

        // 탄수화물 입력 필드
        OutlinedTextField(
            value = nutritionalInfoDto?.carbohydrates?.toString() ?: "",
            onValueChange = {
                val updatedNutritionalInfo = nutritionalInfoDto?.copy(carbohydrates = it.toIntOrNull())
                onRecipeInfoChanged(updatedNutritionalInfo)
            },
            label = { Text("탄수화물 함량") },
            modifier = Modifier.fillMaxWidth()
        )

        // 단백질 입력 필드
        OutlinedTextField(
            value = nutritionalInfoDto?.protein?.toString() ?: "",
            onValueChange = {
                val updatedNutritionalInfo = nutritionalInfoDto?.copy(protein = it.toIntOrNull())
                onRecipeInfoChanged(updatedNutritionalInfo)
            },
            label = { Text("단백질 함량") },
            modifier = Modifier.fillMaxWidth()
        )

        // 지방 입력 필드
        OutlinedTextField(
            value = nutritionalInfoDto?.fat?.toString() ?: "",
            onValueChange = {
                val updatedNutritionalInfo = nutritionalInfoDto?.copy(fat = it.toIntOrNull())
                onRecipeInfoChanged(updatedNutritionalInfo)
            },
            label = { Text("지방 함량") },
            modifier = Modifier.fillMaxWidth()
        )

        // 비타민 입력 필드
        OutlinedTextField(
            value = nutritionalInfoDto?.vitamins?.toString() ?: "",
            onValueChange = {
                val updatedNutritionalInfo = nutritionalInfoDto?.copy(vitamins = it.toIntOrNull())
                onRecipeInfoChanged(updatedNutritionalInfo)
            },
            label = { Text("비타민 함량") },
            modifier = Modifier.fillMaxWidth()
        )

        // 미네랄 입력 필드
        OutlinedTextField(
            value = nutritionalInfoDto?.minerals?.toString() ?: "",
            onValueChange = {
                val updatedNutritionalInfo = nutritionalInfoDto?.copy(minerals = it.toIntOrNull())
                onRecipeInfoChanged(updatedNutritionalInfo)
            },
            label = { Text("미네랄 함량") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
fun NutritionalInfoFormPreview() {
    RecipiaaosTheme {
        // 여기에서 사용할 데이터를 만들어서 넘겨줄 수 있음
        NutritionalInfoForm(
            nutritionalInfoDto = NutritionalInfoDto(
                id = 1,
                carbohydrates = 30,
                protein = 20,
                fat = 10,
                vitamins = 5,
                minerals = 3
            ),
            onRecipeInfoChanged = {}
        )
    }
}