package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.ui.theme.*
import com.siagajiwa.siagajiwaid.viewmodel.UserViewModel

@Composable
fun RoundedLayoutSignup(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    // State management
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isTermsAccepted by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val uiState by userViewModel.uiState.collectAsState()

    // Form validation
    val isFormValid = fullName.isNotBlank() &&
                      email.isNotBlank() &&
                      password.length >= 6 &&
                      isTermsAccepted

    // Clear error when user types
    LaunchedEffect(fullName, email, password) {
        errorMessage = null
        userViewModel.clearError()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(LeftStart),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(top=10.dp)
            ){
                HeadingTextComponent(heading = "Selamat Datang")
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Silahkan mendaftar untuk membuat akun baru",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ImageCenter(image = R.drawable.signup)
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.height(20.dp))

            Column {
                // Full Name Input
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleDark,
                        unfocusedBorderColor = Color.Gray
                    ),
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleDark,
                        unfocusedBorderColor = Color.Gray
                    ),
                    enabled = !uiState.isLoading,
                    isError = errorMessage?.contains("email", ignoreCase = true) == true
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurpleDark,
                        unfocusedBorderColor = Color.Gray
                    ),
                    enabled = !uiState.isLoading,
                    supportingText = {
                        if (password.isNotEmpty() && password.length < 6) {
                            Text(
                                text = "Password minimal 6 karakter",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Text(
                                text = if (isPasswordVisible) "👁" else "👁‍🗨",
                                fontSize = 20.sp
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Terms and conditions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isTermsAccepted,
                        onCheckedChange = { isTermsAccepted = it },
                        enabled = !uiState.isLoading
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "By creating an account you agree\n" +
                                "to our Term and Conditions",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            if (!uiState.isLoading) {
                                isTermsAccepted = !isTermsAccepted
                            }
                        }
                    )
                }

                // Error message
                if (errorMessage != null || uiState.error != null) {
                    Text(
                        text = errorMessage ?: uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sign up button
                Button(
                    onClick = {
                        if (isFormValid) {
                            userViewModel.signUpWithEmail(
                                email = email.trim(),
                                password = password,
                                fullName = fullName.trim(),
                                onSuccess = {
                                    navController.navigate("HomeScreen") {
                                        popUpTo("LoginScreen") { inclusive = true }
                                    }
                                },
                                onError = { error ->
                                    errorMessage = error
                                }
                            )
                        } else {
                            when {
                                fullName.isBlank() -> errorMessage = "Nama tidak boleh kosong"
                                email.isBlank() -> errorMessage = "Email tidak boleh kosong"
                                password.length < 6 -> errorMessage = "Password minimal 6 karakter"
                                !isTermsAccepted -> errorMessage = "Anda harus menyetujui syarat dan ketentuan"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PurpleDark,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (uiState.isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mendaftar...", fontSize = 16.sp, color = White)
                        }
                    } else {
                        Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Already have account
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sudah punya akun? ",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Login",
                        color = PurpleDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            if (!uiState.isLoading) {
                                navController.navigate("LoginScreen") {
                                    popUpTo("SignupScreen") { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SignupScreen(navController: NavHostController) {
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
                    text = "Sign Up",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            RoundedLayoutSignup(navController)
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SignUpScreenPreview() {
//    SignupScreen(navController = rememberNavController())
//}