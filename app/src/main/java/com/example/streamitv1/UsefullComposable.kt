package com.example.streamitv1

import android.graphics.BlurMaskFilter
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.streamitv1.ui.theme.HeavyLoadColor
import com.example.streamitv1.ui.theme.LightLoadColor
import com.example.streamitv1.ui.theme.rosarioFamily

@Composable
fun VideoPreview(
    navController: NavController, vM: MyViewModel, video: VideoDetail
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .height(195.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 25.dp, start = 25.dp, end = 25.dp
                    )
                    .clip(
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(MaterialTheme.colorScheme.secondary),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(1.dp)
                        .clip(
                            shape = RoundedCornerShape(7.dp)
                        )
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            vM.exoPlayer?.release()
                            Log.d("armaan op" ,"VideoPlayer/${video.str}" )
                            vM.currentPosition.longValue = 0
                            vM.currentVideoURL.value = video.videoURL2
                            vM.qualityChoice.intValue = 0
                            vM.speedChoice.intValue = 3

                            vM.mSocket.emit("increment-views", video.id)
                            navController.navigate("VideoPlayer/${video.str}")
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (video.videoThumbnail != null) {
                        AsyncImage(
                            model = video.videoThumbnail,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = video.title,
                        )
                    } else {
                        GradientBox(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .height(90.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Spacer(modifier = Modifier.width(30.dp))
            Column(
                modifier = Modifier
                    .height(55.dp)
                    .width(55.dp)
                    .clickable {
                        vM.filterVideo(video.author.username)
                        navController.navigate("ProfilePage/${video.author.username}")
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier
                        .height(45.dp)
                        .width(45.dp)
                        .shadow(
                            color = MaterialTheme.colorScheme.onTertiary,
                            borderRadius = 32.dp,
                            blurRadius = 3.dp,
                            offsetY = 5.dp,
                            offsetX = 0.dp,
                            spread = 1.dp,
                        )
                        .clickable {
                            vM.filterVideo(video.author.username)
                            navController.navigate("ProfilePage/${video.author.username}")
                        })
                    AsyncImage(
                        model = video.author.dpURL,
                        modifier = Modifier
                            .size(39.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.user_icon),
                        error = painterResource(id = R.drawable.user_icon),
                        contentDescription = "User Dp",
                    )
                }
            }

            Spacer(modifier = Modifier.width(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = video.title,
                        fontFamily = rosarioFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = video.author.username,
                        fontFamily = rosarioFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        Divider(
            modifier = Modifier.fillMaxWidth(0.90F),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
fun TopBar(
    text: String, vM: MyViewModel
) {
    Row(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1F)
                .clickable {
                    vM.isOffsetEnabled.value = !vM.isOffsetEnabled.value

                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .height(25.dp)
                    .aspectRatio(1F),
                painter = painterResource(R.drawable.menu_icon),
                contentDescription = "back_arrow_icon_light",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.secondary
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1F),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {}
    }
}

fun Modifier.shadow(
    color: Color = Color.Black,
    borderRadius: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Dp = 0f.dp,
    modifier: Modifier = Modifier
) = this.then(modifier.drawBehind {
    this.drawIntoCanvas {
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        val spreadPixel = spread.toPx()
        val leftPixel = (0f - spreadPixel) + offsetX.toPx()
        val topPixel = (0f - spreadPixel) + offsetY.toPx()
        val rightPixel = (this.size.width + spreadPixel)
        val bottomPixel = (this.size.height + spreadPixel)

        if (blurRadius != 0.dp) {
            frameworkPaint.maskFilter =
                (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
        }

        frameworkPaint.color = color.toArgb()
        it.drawRoundRect(
            left = leftPixel,
            top = topPixel,
            right = rightPixel,
            bottom = bottomPixel,
            radiusX = borderRadius.toPx(),
            radiusY = borderRadius.toPx(),
            paint
        )
    }
})


@Composable
fun LoginNSignupIntroText(
    text: String
) {
    AnimatedContent(
        targetState = text, transitionSpec = {
            (fadeIn()).togetherWith(fadeOut())
        }, label = ""
    ) { targetCount ->
        Text(
            modifier = Modifier.fillMaxWidth(0.78F),
            text = targetCount,
            color = MaterialTheme.colorScheme.secondary,
            lineHeight = 20.sp,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginNSignupInputField(
    type: MutableState<String>, text: String, error: Boolean
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

@Composable
fun LoginNSignupIconButton(
    reset: () -> Unit
) {
    Column(
        modifier = Modifier
            .height(55.dp)
            .width(50.dp)
            .clip(
                shape = RoundedCornerShape(
                    topEnd = 50.dp, bottomEnd = 50.dp
                )
            )
            .background(MaterialTheme.colorScheme.secondary)
            .clickable {
                reset()
            }, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End
    ) {
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
                    .aspectRatio(1F),
                painter = painterResource(R.drawable.back_arrow_icon),
                contentDescription = "back_arrow_icon_light",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LoginNSignupButton(
    text: String, onclick: () -> Unit, modifier: Modifier, isEnabled: Boolean = true
) {
    Button(
        modifier = modifier, colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.primary
        ), onClick = { onclick() }, shape = RoundedCornerShape(4.dp), enabled = isEnabled
    ) {
        Text(
            text = text, fontSize = 11.sp, fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun SideOptions(
    navController: NavController, vM: MyViewModel
) {
    val w = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .height(60.dp)
                .width(((5 * w) / 8)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1F),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .height(50.dp)
                        .aspectRatio(1F)
                        .clip(
                            shape = RoundedCornerShape(30.dp)
                        )
                        .background(MaterialTheme.colorScheme.primary).clickable{

                        }.clickable{
                            vM.filterVideo(vM.userName.value)
                            navController.navigate("ProfilePage/${vM.userName.value}")
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    //Put square user icon here with 40.dp or 39.dp height
                    AsyncImage(
                        model = "https://storage.googleapis.com/user-streamit/${vM.userName.value}.png",
                        modifier = Modifier
                            .size(39.dp)
                            .clip(CircleShape),
                        placeholder = painterResource(id = R.drawable.user_icon),
                        error = painterResource(id = R.drawable.user_icon),
                        contentDescription = "User Dp",
                    )
                }
            }
            Spacer(Modifier.width(15.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = vM.userName.value,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(((5 * w) / 8)),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1F)
                    .width(((5 * w) / 8)),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    SideBarOption(image = R.drawable.homepage_icon, text = "Homepage", onclick = {
                        vM.mSocket.emit("give-video-list", "nothing")
                        if (!vM.isOffsetEnabled.value) {

                            vM.isOffsetEnabled.value = !vM.isOffsetEnabled.value
                            navController.navigate("HomePage")
                        }
                    })
                }
                item {
                    SideBarOption(image = R.drawable.follow_icon, text = "Following", onclick = {
                        vM.mSocket.emit("give-following-video-list", vM.userName.value)
                        if (!vM.isOffsetEnabled.value) {

                            vM.isOffsetEnabled.value = !vM.isOffsetEnabled.value
                            navController.navigate("Following")
                        }
                    })
                }
                item {
                    SideBarOption(image = R.drawable.upload_icon, text = "Upload", onclick = {

                        if (!vM.isOffsetEnabled.value) {
                            Log.d("debug my-app", "Upload")

                            vM.isOffsetEnabled.value = !vM.isOffsetEnabled.value
                            navController.navigate("Upload")
                        }
                    })
                }
                item {
                    SideBarOption(image = R.drawable.search_icon, text = "Search", onclick = {

                        if (!vM.isOffsetEnabled.value) {
                            vM.isOffsetEnabled.value = !vM.isOffsetEnabled.value
                            navController.navigate("Search")
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.height(35.dp))
            SideBarOption(image = R.drawable.logout_icon, text = "Log out", onclick = {
                vM.reset()
                if (!vM.isOffsetEnabled.value) {
                    vM.isOffsetEnabled.value = !vM.isOffsetEnabled.value
                    navController.navigate("Login")
                }
            })
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SideBarOption(
    image: Int, text: String, onclick: () -> Unit
) {
    val w = LocalConfiguration.current.screenWidthDp.dp
    Row(
        modifier = Modifier
            .height(50.dp)
            .width(((5 * w) / 8))
            .clickable { onclick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.width(20.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1F),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .height(28.dp)
                    .aspectRatio(1F),
                painter = painterResource(image),
                contentDescription = "back_arrow_icon_light",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun GradientBox(
    modifier :Modifier
){
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val color by infiniteTransition.animateColor(
        initialValue = LightLoadColor,
        targetValue = HeavyLoadColor,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )
    Box(
        modifier = modifier.drawBehind {
            drawRect(color)
        }
    )
}