package com.example.streamitv1

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SignupPage(
    navController: NavController, mainActivity: MainActivity, vM: ViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        LoginNSignupIconButton(reset = {
            vM.reset()
            navController.popBackStack()
        })
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginNSignupIntroText(
                text = "Create your account"
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginNSignupInputField(
                type = vM.userName, text = "Enter Username", error = false
            )
        }
        Spacer(modifier = Modifier.height(13.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginNSignupInputField(
                type = vM.password,
                text = "Enter Password",
                error = vM.errorType.value == "PasswordMisMatch"
            )
        }
        Column(
            modifier = Modifier
                .height(73.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .height(60.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginNSignupInputField(
                    type = vM.confirmPassword,
                    text = "Confirm Password",
                    error = vM.errorType.value == "PasswordMisMatch"
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.78F),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(13.dp))
                Text(
                    text = when (vM.errorType.value) {
                        "UserNameTaken" -> "Username Taken"
                        "IncorrectInfo" -> "Incorrect username or password"
                        "PasswordMisMatch" -> "Password mismatch"
                        "InvalidEntry" -> "Invalid Entry"
                        else -> ""
                    },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onError,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.1.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginNSignupButton(modifier = Modifier
                .fillMaxHeight(0.95F)
                .fillMaxWidth(0.78F),
                text = "Signup",
                onclick = {
                    if (vM.confirmPassword.value != vM.password.value) {
                        vM.errorType.value = "PasswordMisMatch"
                    }
                    else if(vM.userName.value=="" || vM.password.value=="" || vM.userName.value=="null" || vM.password.value=="null"){
                        vM.errorType.value = "InvalidEntry"
                    }else {
                        vM.errorType.value = ""
                        signup(
                            vM.userName.value, vM.password.value, navController, mainActivity, vM
                        )
                    }
                }
            )
        }
    }
}




