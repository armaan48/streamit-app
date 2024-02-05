package com.example.streamitv1

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.streamitv1.ui.theme.rosarioFamily

@Composable
fun VideoView(
    navController: NavController,
    vM: ViewModel,
    video: VideoDetail
) {
    val context = LocalContext.current


    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setMediaItem(
            MediaItem.fromUri(
                video.videoURL
            )
        )
        prepare()
        playWhenReady = false
    }

    val w = LocalConfiguration.current.screenWidthDp.dp

    val offsetX by animateDpAsState(
        targetValue = if (!vM.isOffsetEnabled.value) (5 * w) / 8 else 0.dp,
        animationSpec = tween(250, easing = LinearEasing),
        label = ""
    )

    SideOptions(
        navController = navController,
        vM = vM
    )
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
            Spacer(Modifier.height(40.dp))
            TopBar(
                text = "Stream it",
                vM = vM
            )
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16F / 9F)
            ) {
                DisposableEffect(key1 = Unit) { onDispose { exoPlayer.release() } }
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1F),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1F),
                            text = video.title,
                            fontFamily = rosarioFamily,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 18.sp
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1F),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "1K Views ",
                                fontFamily = rosarioFamily,
                                fontWeight = FontWeight.Light,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                modifier = Modifier
                                    .clickable {
                                        vM.descriptionExtended.value = !vM.descriptionExtended.value
                                               },
                                text = "...more",
                                fontFamily = rosarioFamily,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 17.sp
                            )
                        }
                    }
                    AnimatedVisibility(
                        vM.descriptionExtended.value,
                        enter = expandVertically(animationSpec = tween(durationMillis = 200), expandFrom = Alignment.Top) { 60 } + fadeIn(),
                        exit = shrinkVertically(animationSpec = tween(durationMillis = 200), shrinkTowards = Alignment.Bottom) { 0 } + fadeOut()
                    ){
                        Column(
                            modifier = Modifier
                                .height(73.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            Column(
                                modifier = Modifier
                                    .animateContentSize()
                                    .height(60.dp)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(video.description)
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1F),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1F)
                                    .clickable {
                                        navController.navigate("ProfilePage")
                                    },
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ){
                                    Box(
                                        modifier = Modifier
                                            .height(40.dp)
                                            .width(40.dp)
                                            .shadow(
                                                color = MaterialTheme.colorScheme.onTertiary,
                                                borderRadius = 32.dp,
                                                blurRadius = 3.dp,
                                                offsetY = 5.dp,
                                                offsetX = 0.dp,
                                                spread = 1.dp,
                                                )
                                    )
                                    AsyncImage(
                                        model = video.author.thumbnailURL,
                                        modifier = Modifier
                                            .size(39.dp)
                                            .clip(CircleShape),
                                        placeholder = painterResource(id = R.drawable.user_icon),
                                        error = painterResource(id = R.drawable.user_icon),
                                        contentDescription = "User Dp",
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = video.author.username,
                                fontFamily = rosarioFamily,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 17.sp
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.6F)
                                .width(150.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            VideoViewButton(
                                image = -1,
                                text = "Subscribe",
                                onclick = {

                                }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.6F)
                                .weight(1F),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            VideoViewButton(
                                image = R.drawable.like_icon,
                                text = "like",
                                onclick = {
                                }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.6F)
                                .weight(1F),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            VideoViewButton(
                                image = R.drawable.share_icon,
                                text = "share",
                                onclick = {
                                }
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.6F)
                                .weight(1F),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            VideoViewButton(
                                image = R.drawable.save_icon,
                                text = "save",
                                onclick = {
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(6){
                    VideoPreview(navController , vM = vM , vM.videoList[it])
                }
            }
        }
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun VideoViewButton(
    image: Int,
    text: String,
    onclick: () -> Unit
){
    Button(
        modifier = Modifier
            .fillMaxHeight(0.95F)
            .fillMaxWidth(0.9F),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = { onclick() },
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if(image!=-1){
                Icon(
                    modifier = Modifier
                        .height(40.dp)
                        .aspectRatio(1F),
                    painter = painterResource(image),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
                    .padding(
                        start = 15.dp
                    ),
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = rosarioFamily,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
