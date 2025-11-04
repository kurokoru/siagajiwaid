package com.siagajiwa.siagajiwa.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siagajiwa.siagajiwa.ui.theme.BorderColor
import com.siagajiwa.siagajiwa.ui.theme.BrandColor
import com.siagajiwa.siagajiwa.ui.theme.Tertirary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(
    labelVal: String,
    height: Dp = 58.dp,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    imeAction: ImeAction = ImeAction.Next
) {
    val typeOfKeyboard: KeyboardType = when (labelVal) {
        "email ID" -> KeyboardType.Email
        "mobile" -> KeyboardType.Phone
        else -> KeyboardType.Text
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        label = {
            Text(text = labelVal, fontSize = 9.sp,  lineHeight = 24.sp )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandColor,
            unfocusedBorderColor = BorderColor,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLeadingIconColor = BrandColor,
            unfocusedLeadingIconColor = Tertirary
        ),
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(text = labelVal, color = Tertirary, fontSize = 12.sp,  lineHeight = 24.sp)
        },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
        keyboardOptions = KeyboardOptions(
            keyboardType = typeOfKeyboard,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InputTextPreview() {
    InputText(labelVal = "email ID", height = 48.dp)
}
