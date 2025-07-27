package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.siagajiwa.siagajiwaid.ui.theme.BorderColor
import com.siagajiwa.siagajiwaid.ui.theme.BrandColor
import com.siagajiwa.siagajiwaid.ui.theme.Tertirary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(labelVal: String) {
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
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = BrandColor,
            unfocusedBorderColor = BorderColor,
            textColor = Color.Black,
            focusedLeadingIconColor = BrandColor,
            unfocusedLeadingIconColor = Tertirary
        ),
        shape = MaterialTheme.shapes.large,
        placeholder = {
            Text(text = labelVal, color = Tertirary)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = typeOfKeyboard,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}
