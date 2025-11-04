package com.siagajiwa.siagajiwa.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwa.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.siagajiwa.siagajiwa.ui.theme.LeftStart
import com.siagajiwa.siagajiwa.ui.theme.PurpleDark
import com.siagajiwa.siagajiwa.ui.theme.White
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp

@Composable
fun RoundedLayout(navController: NavHostController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val userViewModel: com.siagajiwa.siagajiwa.viewmodel.UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Login handler function
    val performLogin: () -> Unit = {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Email dan password tidak boleh kosong"
        } else {
            userViewModel.signInWithEmail(
                email = email.trim(),
                password = password,
                onSuccess = {
                    navController.navigate("HomeScreen") {
                        popUpTo("LoginScreen") { inclusive = true }
                    }
                },
                onError = { error ->
                    errorMessage = error
                }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),//.padding(top = 10.dp),
        color = White,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LeftStart),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(top=5.dp)
            ){
                HeadingTextComponent(heading = "Selamat Datang")
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Halo, Silahkan masuk untuk melanjutkan",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ImageCenter(image = R.drawable.locked)
            Spacer(modifier = Modifier.height(30.dp))
            Column {
                InputText(
                    labelVal = "Email",
                    value = email,
                    onValueChange = { email = it },
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    imeAction = ImeAction.Next
                )
                Spacer(modifier = Modifier.height(15.dp))
                PasswordInputComponent(
                    labelVal = "Password",
                    value = password,
                    onValueChange = { password = it },
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            performLogin()
                        }
                    ),
                    imeAction = ImeAction.Done
                )
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ForgotPassword(navController)
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column {
                        SignInBottom(
                            navController = navController,
                            email = email,
                            password = password
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDark)
    ) {
        Column (
        ){
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 20.dp, bottom=10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Back arrow icon (use Icon if available)
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back Arrow",
                    modifier = Modifier
                        .height(18.dp)
                        .width(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            RoundedLayout(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreviewWithData() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleDark)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 20.dp, bottom=10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Back Arrow",
                    modifier = Modifier
                        .height(18.dp)
                        .width(30.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign in",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = White,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(LeftStart),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier.padding(top=5.dp)
                    ){
                        HeadingTextComponent(heading = "Selamat Datang")
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Halo, Silahkan masuk untuk melanjutkan",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    ImageCenter(image = R.drawable.locked)
                    Spacer(modifier = Modifier.height(30.dp))
                    Column {
                        InputTextPreview(labelVal = "Email", value = "example@email.com")
                        Spacer(modifier = Modifier.height(15.dp))
                        PasswordInputPreview(labelVal = "Password", value = "password123")
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ForgotPassword(rememberNavController())
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Column {
                                SignInBottom(rememberNavController())
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputTextPreview(labelVal: String, value: String, height: Dp = 64.dp) {
    var textVal by remember { mutableStateOf(value) }
    val typeOfKeyboard: KeyboardType = when (labelVal) {
        "email ID" -> KeyboardType.Email
        "mobile" -> KeyboardType.Phone
        else -> KeyboardType.Text
    }

    OutlinedTextField(
        value = textVal,
        onValueChange = { textVal = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        label = {
            Text(text = labelVal, fontSize = 12.sp, lineHeight = 24.sp)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = com.siagajiwa.siagajiwa.ui.theme.BrandColor,
            unfocusedBorderColor = com.siagajiwa.siagajiwa.ui.theme.BorderColor,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedLeadingIconColor = com.siagajiwa.siagajiwa.ui.theme.BrandColor,
            unfocusedLeadingIconColor = com.siagajiwa.siagajiwa.ui.theme.Tertirary
        ),
        shape = androidx.compose.material3.MaterialTheme.shapes.large,
        placeholder = {
            Text(text = labelVal, color = com.siagajiwa.siagajiwa.ui.theme.Tertirary, fontSize = 10.sp)
        },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
        keyboardOptions = KeyboardOptions(
            keyboardType = typeOfKeyboard,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordInputPreview(labelVal: String, value: String) {
    var password by remember { mutableStateOf(value) }
    var isShowPassword by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = com.siagajiwa.siagajiwa.ui.theme.BrandColor,
            unfocusedBorderColor = com.siagajiwa.siagajiwa.ui.theme.BorderColor,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        shape = androidx.compose.material3.MaterialTheme.shapes.large,
        placeholder = {
            Text(text = labelVal, color = com.siagajiwa.siagajiwa.ui.theme.Tertirary, fontSize = 12.sp)
        },
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp),
        trailingIcon = {
            val description = if (isShowPassword) "Show Password" else "Hide Password"
            val iconImage = if (isShowPassword) R.drawable.pheyeclosedfill__1_ else R.drawable.eye_closed
            IconButton(onClick = { isShowPassword = !isShowPassword }) {
                Icon(
                    painter = painterResource(id = iconImage),
                    contentDescription = description,
                    tint = com.siagajiwa.siagajiwa.ui.theme.Tertirary,
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isShowPassword) VisualTransformation.None else PasswordVisualTransformation()
    )
}