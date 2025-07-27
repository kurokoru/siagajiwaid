package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.components.BottomSignupTextComponent
import com.siagajiwa.siagajiwaid.components.HeadingTextComponent
import com.siagajiwa.siagajiwaid.components.ImageComponent
import com.siagajiwa.siagajiwaid.components.MyButton
import com.siagajiwa.siagajiwaid.components.MyTextField
import com.siagajiwa.siagajiwaid.components.SignupTermsAndPrivacyText
import com.siagajiwa.siagajiwaid.ui.theme.LeftStart
import com.siagajiwa.siagajiwaid.ui.theme.PurpleDark
import com.siagajiwa.siagajiwaid.ui.theme.White

@Composable
fun RoundedLayoutSignup(navController: NavHostController){
    Surface(
        modifier = Modifier.fillMaxSize(),//.padding(top = 10.dp),
        color = White,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                InputText(labelVal = "Nama")
                Spacer(modifier = Modifier.height(15.dp))
                InputText(labelVal = "Email/No Handphone")
                Spacer(modifier = Modifier.height(15.dp))
                PasswordInputComponent(labelVal = "Password")
                Spacer(modifier = Modifier.height(15.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isChecked by remember { mutableStateOf(false) }

                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isChecked = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "By creating an account your aggree\n" +
                                "to our  Term and Condtions",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { isChecked = !isChecked }
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column {
                        SignUpBottom(navController)
                    }
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

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignupScreen(navController = rememberNavController())
}