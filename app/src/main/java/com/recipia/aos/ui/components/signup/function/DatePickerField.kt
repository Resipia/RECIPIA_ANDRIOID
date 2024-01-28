package com.recipia.aos.ui.components.signup.function

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Calendar

/**
 * 생년월일 날짜 선택 컴포저
 */
@Composable
fun MyDatePickerDialog(onDateSelected: (String) -> Unit) {
    val openDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val deepPurpleColor = Color(27, 94, 32) // 초록색

    Button(
        onClick = { openDialog.value = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, deepPurpleColor)
    ) {
        Text(
            "날짜 선택",
            color = deepPurpleColor,
            fontWeight = FontWeight.Bold
        )
    }

    if (openDialog.value) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected("$year-${month + 1}-$dayOfMonth")
                openDialog.value = false
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
