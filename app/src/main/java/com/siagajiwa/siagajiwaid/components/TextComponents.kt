package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.siagajiwa.siagajiwaid.ui.theme.BorderColor
import com.siagajiwa.siagajiwaid.ui.theme.BrandColor
import com.siagajiwa.siagajiwaid.ui.theme.Tertirary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(labelVal: String, height: Dp = 58.dp) {
    var textVal by remember {
        mutableStateOf("")
    }
    val typeOfKeyboard: KeyboardType = when (labelVal) {
        "email ID" -> KeyboardType.Email
        "mobile" -> KeyboardType.Phone
        else -> KeyboardType.Text
    }

    OutlinedTextField(
        value = textVal,
        onValueChange = {
            textVal = it
        },
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
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun InputTextPreview() {
    InputText(labelVal = "email ID", height = 48.dp)
}
