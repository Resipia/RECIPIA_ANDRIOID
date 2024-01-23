package com.recipia.aos.ui.components.signup.function

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * input 필드 컴포넌트
 */
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    errorMessage: String,
    onErrorMessageChange: (String) -> Unit, // 새로운 에러 메시지를 설정할 콜백 함수
    isEmail: Boolean = false,
    isPassword: Boolean = false,
    isPasswordConfirm: Boolean = false,
    isPhone: Boolean = false,
    modifier: Modifier = Modifier
) {

    // 이메일 형식을 확인하는 정규식
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    // 비밀번호 정규식
    val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()


    Column(
        modifier = modifier
            .padding(
                start = 8.dp,
                end = 8.dp,
                bottom = if (isPasswordConfirm && errorMessage.isNotEmpty()) 100.dp else 8.dp
            )
    ) {

        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                if (isPassword && passwordPattern.matches(newValue)) {
                    onErrorMessageChange("") // 비밀번호가 정규식을 만족하면 에러 메시지를 비웁니다.
                }
            },
            label = { Text(text = label) },
            placeholder = if (isEmail) {
                {
                    Text(
                        "hello@email.com",
                        style = TextStyle(color = Color.Gray) // 여기서 회색으로 설정합니다.
                    )
                }
            } else {
                null
            },
            isError = when {
                isEmail && value.isNotEmpty() && !emailPattern.matches(value) -> {
                    onErrorMessageChange("잘못된 이메일 형식입니다.")
                    true
                }

                isPassword && value.isNotEmpty() && !passwordPattern.matches(value) -> {
                    onErrorMessageChange("비밀번호 형식에 맞지 않습니다.")
                    true
                }

                else -> errorMessage.isNotEmpty()
            },
            visualTransformation = if (isPassword || isPasswordConfirm) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions =
            if (isPasswordConfirm) {
                KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            } else {
                KeyboardOptions.Default.copy(
                    keyboardType = when {
                        isPhone -> KeyboardType.Number
                        isPassword -> KeyboardType.Password
                        isPasswordConfirm -> KeyboardType.Password
                        else -> KeyboardType.Text
                    },
                    imeAction = ImeAction.Next
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )

        // 에러 메시지 표시
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
