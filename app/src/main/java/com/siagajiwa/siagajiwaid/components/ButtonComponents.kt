package com.siagajiwa.siagajiwaid.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.siagajiwa.siagajiwaid.R
import com.siagajiwa.siagajiwaid.ui.theme.BgSocial
import com.siagajiwa.siagajiwaid.ui.theme.Tertirary
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.siagajiwa.siagajiwaid.ui.theme.DarkLight
import com.siagajiwa.siagajiwaid.ui.theme.PurpleDark
import com.siagajiwa.siagajiwaid.ui.theme.PurpleLight
import com.siagajiwa.siagajiwaid.ui.theme.PurpleNetral
import com.siagajiwa.siagajiwaid.ui.theme.White


@Composable
fun SignInBottom(navController: NavHostController) {
    Column (){
        DefaultButton(labelVal = "Sign In", navController = navController)
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
