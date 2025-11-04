package com.siagajiwa.siagajiwa.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwa.ui.theme.DarkLight
import com.siagajiwa.siagajiwa.ui.theme.PurpleDark
import com.siagajiwa.siagajiwa.ui.theme.White
import com.siagajiwa.siagajiwa.viewmodel.UserViewModel


@Composable
fun SignInBottom(
    navController: NavHostController,
    email: String = "",
    password: String = "",
    userViewModel: UserViewModel = viewModel()
) {
    val uiState by userViewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column (){
        // Sign In Button with authentication
        Button(
            onClick = {
                Log.d("LoginScreen", "Sign In button clicked")
                Log.d("LoginScreen", "  Email: $email")
                Log.d("LoginScreen", "  Password length: ${password.length}")

                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email dan password tidak boleh kosong"
                    Log.e("LoginScreen", "  Validation failed: empty fields")
                    return@Button
                }

                Log.d("LoginScreen", "  Attempting authentication...")
                userViewModel.signInWithEmail(
                    email = email.trim(),
                    password = password,
                    onSuccess = {
                        Log.d("LoginScreen", "  ✅ Sign in successful - navigating to HomeScreen")
                        val currentUser = com.siagajiwa.siagajiwa.data.SupabaseClient.auth.currentUserOrNull()
                        Log.d("LoginScreen", "  Post-login user check: ${currentUser?.email}")
                        navController.navigate("HomeScreen") {
                            popUpTo("LoginScreen") { inclusive = true }
                        }
                    },
                    onError = { error ->
                        errorMessage = error
                        Log.e("LoginScreen", "  ❌ Sign in failed: $error")
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = PurpleDark
            ),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(top = 40.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Memproses...",
                    color = White,
                    fontSize = 20.sp
                )
            } else {
                Text(
                    text = "Sign In",
                    color = White,
                    fontSize = 20.sp
                )
            }
        }

        // Show error message if any
        if (errorMessage != null || uiState.error != null) {
            Text(
                text = errorMessage ?: uiState.error ?: "",
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Belum Memiliki Akun?",
                modifier = Modifier.padding(top = 10.dp, start = 85.dp),
                color = DarkLight,
                fontSize = 12.sp
            )
            Text(
                text = "Daftar",
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                    .clickable {
                        Log.d("LoginScreen", "Navigate to SignupScreen")
                        navController.navigate("SignupScreen")
                    },
                color = PurpleDark,
                fontSize = 14.sp
            )

        }
    }
}


@Composable
fun DefaultButton(labelVal: String, navController: NavHostController) {
    Button(
        onClick = {
            when (labelVal) {
                "Sign In" -> navController.navigate("HomeScreen")
                "Submit" -> navController.navigate("ResetPassword")
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = PurpleDark
        ),
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 40.dp)

    ) {
        Text(
            text = labelVal,
            color = White,
            fontSize = 20.sp
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SignInBottomPreview() {
    SignInBottom(navController = rememberNavController())
}


@Composable
fun SignUpBottom(navController: NavHostController) {
    Column (){
        DefaultButton(labelVal = "Sign Up", navController = navController)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Sudah Memiliki Akun?",
                modifier = Modifier.padding(top = 10.dp, start = 85.dp),
                color = DarkLight,
                fontSize = 16.sp
            )
            Text(
                text = "Daftar",
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
                    .clickable {
                        navController.navigate("LoginScreen")
                    },
                color = PurpleDark,
                fontSize = 16.sp
            )

        }
    }
}
