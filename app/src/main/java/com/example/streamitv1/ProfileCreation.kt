package com.example.streamitv1

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.json.JSONObject


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileCreation(
    navController: NavController,
    vM: ViewModel,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    /*val cropImage: ActivityResultLauncher<CropImageContractOptions> =
        registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful()) {
                val cropped =
                    BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true))
            }
        }*/

    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                vM.dpChunkList.clear()
                startChunking(
                    vM, uri, vM.dpSize, vM.dpChunkSize, vM.dpTotalChunks, scope, context, "dp"
                )
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .background(color = MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Top,
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
                    text = "Complete your account"
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
                    type = vM.channelName, text = "Enter Channel Name", error = false
                )
            }
            Spacer(modifier = Modifier.height(13.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginNSignupInputField(
                    type = vM.channelDescription,
                    text = "Enter Channel Description",
                    error = false,
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
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
                    text = "Select Profile Picture",
                    onclick = {
                        pickImage.launch("image/*")
                    })
            }
        }
        Column(
            modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    text = "Create Account",
                    onclick = {
                        val data = JSONObject()

                        data.put("username", vM.userName.value)
                        data.put("channelName", vM.channelName.value)
                        data.put("channelDescription", vM.channelDescription.value)
                        data.put("publicKey", vM.publicKeyString.value)
                        data.put("encryptedPrivateKey", vM.encryptedPrivateKey.value)
                        data.put("encryptedPassword", vM.encryptedPassword.value)

                        Log.d("streamit", data.toString())

                        vM.mSocket.emit("signup-complete", data)

                        navController.navigate("Main")
                    })
            }
            Spacer(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
            )
        }
    }
}