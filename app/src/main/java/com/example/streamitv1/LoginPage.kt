package com.example.streamitv1

import LoadingPage
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginPage(
    navController: NavController, mainActivity: MainActivity, vM: ViewModel
) {
    val context = mainActivity.applicationContext

    val username by UserPreferences.getUserName(context).collectAsState(initial = "")
    val password by UserPreferences.getPassword(context).collectAsState(initial = "")

    LaunchedEffect(username) { // Use username as a key
        if (username != "" && password != "" && username!=null && password!=null) {
            vM.userName.value = username.toString()
            vM.password.value = password.toString()
            login(vM.userName.value, vM.password.value, navController, mainActivity, vM)
        }
    }

    if (username != "" || username!=null) {
        LoadingPage()
    }
    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primary),
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
                    text = "Welcome back"
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
                    type = vM.userName,
                    text = "Enter Username",
                    error = (vM.errorType.value == "UserNameTaken")
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
                    error = (vM.errorType.value == "IncorrectInfo"),
                )
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
                            "UserNameNotFound" -> "Username not found"
                            "IncorrectInfo" -> "Incorrect username or password"
                            "PasswordMisMatch" -> "Password mismatch"
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
                    text = "Log in",
                    onclick = {
                        login(vM.userName.value, vM.password.value, navController, mainActivity, vM)
                    })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't have an account? ",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        modifier = Modifier.clickable {
                            vM.reset()
                            navController.navigate("Signup")
                        },
                        text = "Sign up",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}