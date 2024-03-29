package com.example.streamitv1

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.streamitv1.ui.theme.rosarioFamily
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UploadScreen(
    navController: NavController, vM: MyViewModel
) {
    val w = LocalConfiguration.current.screenWidthDp.dp

    val offsetX by animateDpAsState(
        targetValue = if (!vM.isOffsetEnabled.value) (5 * w) / 8 else 0.dp,
        animationSpec = tween(250, easing = LinearEasing),
        label = ""
    )

    SideOptions(navController, vM)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .offset(x = offsetX, y = 0.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(
                text = "Upload", vM = vM
            )
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            when (vM.uploadType.value) {
                "" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UploadButton(type = "Initiate Live", onclick = {
                                vM.uploadType.value = "Live"
                            })
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UploadButton(type = "Upload Video", onclick = {
                                vM.uploadType.value = "Upload"
                            })
                        }
                    }
                }

                "Live" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Live(vM)
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Upload(
                            vM = vM
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Live(
    vM: MyViewModel
) {

    val clipboardManager = LocalClipboardManager.current


    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                vM.liveThumbnailChunkList.clear()
                startChunking(
                    vM,
                    uri,
                    vM.liveThumbnailSize,
                    vM.liveThumbnailChunkSize,
                    vM.liveThumbnailTotalChunks,
                    scope,
                    context,
                    "live-thumbnail"
                )
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth(0.11F))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1F),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier
                                .height(30.dp)
                                .aspectRatio(1F)
                                .clickable { vM.uploadType.value = "" },
                            painter = painterResource(R.drawable.back_arrow_icon),
                            contentDescription = "back_arrow_icon_light",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1F),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Live",
                            fontFamily = rosarioFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 25.sp
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.22F))
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginInputField(
                    type = vM.title, error = false, text = "title"
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginInputField(
                    type = vM.description, error = false, text = "description"
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginInputField(
                    type = vM.tags, error = false, text = "tags"
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                UploadButton(type = "Live Thumbnail", onclick = {
                    pickImage.launch("image/*")
                })
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (vM.liveStatus.value == 2) 135.dp else 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            when (vM.liveStatus.value) {
                0 -> UploadButton(type = "Initiate Live", onclick = {
                    val data = JSONObject()
                    data.put("author", vM.userName.value)
                    data.put("title", vM.title.value)
                    data.put("tags", vM.tags.value)
                    data.put("description", vM.description.value)
                    Log.d("uploadScreen", data.toString())
                    vM.mSocket.emit("send-live-details", data)
                    vM.liveStatus.value = 1
                })

                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginInputField(type = vM.liveServer,
                            error = false,
                            text = "server id",
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(vM.liveServer.value))
                                }) {
                                    Icon(
                                        painterResource(id = androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
                                        "server id"
                                    )
                                }
                            })
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LoginInputField(type = vM.livePassword,
                            error = false,
                            text = "server password",
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(vM.livePassword.value))
                                }) {
                                    Icon(
                                        painterResource(id = androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
                                        "server password"
                                    )
                                }
                            })
                    }
                }

                else -> {
                    UploadButton(type = "Starting Live", onclick = {})
                }
            }


        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Upload(
    vM: MyViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pickVideo =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                vM.videoChunkList.clear()
                startChunking(
                    vM,
                    uri,
                    vM.videoSize,
                    vM.videoChunkSize,
                    vM.videoTotalChunks,
                    scope,
                    context,
                    "video"
                )
            }
        }
    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                vM.videoThumbnailChunkList.clear()
                startChunking(
                    vM,
                    uri,
                    vM.videoThumbnailSize,
                    vM.videoThumbnailChunkSize,
                    vM.videoThumbnailTotalChunks,
                    scope,
                    context,
                    "video-thumbnail"
                )
            }
        }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.5F),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.fillMaxWidth(0.11F))
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1F),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier
                                .height(30.dp)
                                .aspectRatio(1F)
                                .clickable { vM.uploadType.value = "" },
                            painter = painterResource(R.drawable.back_arrow_icon),
                            contentDescription = "back_arrow_icon_light",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1F),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Upload",
                            fontFamily = rosarioFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 25.sp
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.22F))
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginInputField(
                    type = vM.title, error = false, text = "title"
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginInputField(
                    type = vM.description, error = false, text = "description"
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginInputField(
                    type = vM.tags, error = false, text = "tags"
                )
            }
            Spacer(modifier = Modifier.height(25.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                UploadButton(type = "Select Video", onclick = {
                    pickVideo.launch("video/*")
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                UploadButton(type = "Thumbnail", onclick = {
                    pickImage.launch("image/*")
                })
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            var info = "Upload"
            if (vM.uploadStatus.intValue == 1) {
                info = "Uploading ${vM.videoPercentageUploaded.doubleValue}%"
            } else if (vM.uploadStatus.intValue == 2) {
                info = "Video is Uploaded Successfully, Start new Upload?"
            }
            UploadButton(

                type = info, onclick = {
                    if (vM.uploadStatus.intValue == 2) {
                        vM.title.value = ""
                        vM.tags.value = ""
                        vM.description.value = ""
                        vM.uploadStatus.intValue = 0
                        vM.clearChunks("video")
                        vM.clearChunks("video-thumbnail")
                    } else {
                        vM.uploadStatus.intValue = 1
                        val data = JSONObject()
                        data.put("author", vM.userName.value)
                        data.put("title", vM.title.value)
                        data.put("tags", vM.tags.value)
                        data.put("description", vM.description.value)
                        Log.d("uploadScreen", data.toString())
                        vM.mSocket.emit("send-video-details", data)

                    }
                }, isEnabled = (vM.uploadStatus.intValue != 1)

            )
        }
    }
}

@Composable
fun UploadButton(
    type: String, onclick: () -> Unit, isEnabled: Boolean = true
) {
    Button(
        modifier = Modifier
            .fillMaxHeight(0.95F)
            .fillMaxWidth(0.78F),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = { onclick() },
        shape = RoundedCornerShape(4.dp),
        enabled = isEnabled
    ) {
        Text(
            text = type, fontSize = 11.sp, fontWeight = FontWeight.Normal
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginInputField(
    type: MutableState<String>,
    text: String,
    error: Boolean,
    readOnly: Boolean = false,
    trailingIcon: @Composable () -> Unit = {}
) {
    val color = MaterialTheme.colorScheme.tertiary
    val color2 = MaterialTheme.colorScheme.onTertiary
    OutlinedTextField(
        modifier = Modifier
            .fillMaxHeight(0.95F)
            .fillMaxWidth(0.78F),
        value = type.value,
        onValueChange = { type.value = it },
        isError = error,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = color,
            unfocusedBorderColor = color,
            focusedLabelColor = Color.Red,
            unfocusedLabelColor = Color.Red
        ),
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        textStyle = TextStyle(
            fontSize = 12.sp
        ),
        label = {
            Text(
                modifier = Modifier.padding(2.dp), text = text, color = color2
            )
        },
        shape = RoundedCornerShape(4.dp)
    )
}



