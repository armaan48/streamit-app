package com.example.streamitv1

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.analytics.PlaybackStats
import androidx.media3.exoplayer.analytics.PlaybackStatsListener
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.streamitv1.ui.theme.rosarioFamily
import org.json.JSONObject


@OptIn(UnstableApi::class) @Composable
fun VideoView(
    navController: NavController,
    vM: ViewModel,
    video: VideoDetail
) {
    val context = LocalContext.current
    vM.exoPlayer = remember{
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.fromUri(
                    video.videoURL2
                )
            )
            prepare()
            playWhenReady = true
        }
    }
    vM.isPlaying.value = true

    val w = LocalConfiguration.current.screenWidthDp.dp
    val h = LocalConfiguration.current.screenHeightDp.dp

    val settingState = remember { mutableStateOf(false) }
    val settingType = remember { mutableStateOf("Main") }

    val offsetX by animateDpAsState(
        targetValue = if (!vM.isOffsetEnabled.value) (5 * w) / 8 else 0.dp,
        animationSpec = tween(250, easing = LinearEasing),
        label = ""
    )

    val offsetForSlider by animateDpAsState(
        targetValue = if (!settingState.value) h else 0.dp,
        animationSpec = tween(250, easing = LinearEasing),
        label = ""
    )

    val configuration = LocalConfiguration.current

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable {
                        vM.videoFocused.value = !vM.videoFocused.value
                    }
            ) {
                DisposableEffect(key1 = Unit) { onDispose {
                    vM.exoPlayer?.release()
                }
                }
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = vM.exoPlayer
                            useController = false
                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    }
                )
                VideoControls(
                    exoPlayer = vM.exoPlayer!!,
                    vM = vM,
                    run = {
                        vM.videoFocused.value = !vM.videoFocused.value
                    },
                    configuration = configuration,
                    settingControl = settingState
                )
            }
        }
        else -> {
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16F / 9F)
                            .clickable {
                                vM.videoFocused.value = !vM.videoFocused.value
                            }

                    ) {
                        DisposableEffect(key1 = Unit) { onDispose {
                            vM.exoPlayer?.release() }
                        }
                        AndroidView(
                            factory = {
                                PlayerView(context).apply {
                                    player = vM.exoPlayer
                                    useController = false
                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            }
                        )
                        Column {
                            AnimatedVisibility(
                                visible = vM.videoFocused.value,
                                enter = EnterTransition.None,
                                exit = ExitTransition.None
                            ){
                                VideoControls(
                                    exoPlayer = vM.exoPlayer!!,
                                    vM = vM,
                                    run = {
                                        vM.videoFocused.value = !vM.videoFocused.value
                                    },
                                    configuration = configuration,
                                    settingControl = settingState
                                )
                            }
                        }
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
                                        text = "${video.views} Views ",
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
                                        image = R.drawable.like_icon,
                                        text =  if (vM.followingList.value.contains(video.author)) "Unsubscribe" else "Subscribe",
                                        onclick = {
                                            val data = JSONObject();
                                            data.put("follower_id" , vM.userName.value)
                                            data.put("following_id",video.author.username)
                                            if (vM.followingList.value.contains(video.author)){
                                                vM.mSocket.emit("unfollow" , data)
                                                vM.removeFollowing(video.author)
                                            }else{
                                                vM.mSocket.emit("follow" , data)
                                                vM.addFollowing(video.author)
                                            }
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
                                        text =  if (vM.likedVideoList.value.contains(video.id)) "Unlike" else "Like",
                                        onclick = {
                                            val data = JSONObject();
                                            data.put("user_id" , vM.userName.value)
                                            data.put("video_id" ,video.id)
                                            if (vM.likedVideoList.value.contains(video.id)){
                                                vM.mSocket.emit("unlike" , data)
                                                vM.unlikeVideo(video.id)
                                            }else{
                                                vM.mSocket.emit("like" , data)
                                                vM.likeVideo(video.id)
                                            }
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
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalArrangement = Arrangement.Top,
                        horizontalArrangement = Arrangement.Center,
                        columns = GridCells.Adaptive(minSize = 370.dp)
                    ) {
                        items(vM.videoList.size){
                            if (vM.videoList[it].id != video.id) {
                                VideoPreview(
                                    navController = navController,
                                    vM = vM,
                                    vM.videoList[it],
                                )
                            }
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
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .offset(x = 0.dp, y = offsetForSlider),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .clickable {
                    settingState.value = false
                    settingType.value = "Main"
                },
            contentAlignment = Alignment.BottomCenter
        ){
            when (settingType.value) {
                "Main" -> {
                    MainSettingOptions(settingType)
                }
                "Quality" -> {
                    QualitySettingOptions()
                }
                else -> {
                    SpeedSettingOptions()
                }
            }
        }
    }
}

@Composable
fun SpeedSettingOptions(){
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(0.95F)
            .background(MaterialTheme.colorScheme.onPrimary)
            .clip(
                shape = RoundedCornerShape(
                    topEnd = 20.dp,
                    topStart = 20.dp
                )
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "0.25x",
                isSelected = true,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "0.5x",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "0.75x",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "Normal",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "1.25x",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "1.5x",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "1.75x",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "2x",
                isSelected = false,
                onclick = {}
            )
        }
    }
}

@Composable
fun QualitySettingOptions(){
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(0.95F)
            .background(MaterialTheme.colorScheme.onPrimary)
            .clip(
                shape = RoundedCornerShape(
                    topEnd = 20.dp,
                    topStart = 20.dp
                )
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "Auto",
                isSelected = true,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "360P",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "480P",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "720P",
                isSelected = false,
                onclick = {}
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "1080P",
                isSelected = false,
                onclick = {}
            )
        }
    }
}

@Composable
fun MainSettingOptions(
    settingType: MutableState<String>
){
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(0.95F)
            .background(MaterialTheme.colorScheme.onPrimary)
            .clip(
                shape = RoundedCornerShape(
                    topEnd = 20.dp,
                    topStart = 20.dp
                )
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "Playback speed",
                isSelected = false,
                onclick = {
                    settingType.value = "Speed";
                }
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            SettingOption(
                text = "Quality",
                isSelected = false,
                onclick = {
                    settingType.value = "Quality";
                }
            )
        }
        item{
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun SettingOption(
    text : String,
    isSelected : Boolean,
    onclick : ()->Unit
){
    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .clickable {
                onclick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(5.dp))
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if(isSelected){
                Icon(
                    modifier = Modifier
                        .height(25.dp)
                        .aspectRatio(1F),
                    painter = painterResource(R.drawable.tick_icon), contentDescription = "back_arrow_icon_light",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Text(
            text = text ,
            fontFamily = rosarioFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
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

@OptIn(UnstableApi::class) @Composable
fun VideoControls(
    exoPlayer : ExoPlayer,
    vM : ViewModel,
    run: () -> Unit,
    configuration : Configuration,
    settingControl : MutableState<Boolean>
) {
    val activity = LocalContext.current as Activity

    val currentPosition = remember { mutableLongStateOf(0L) }

    DisposableEffect(key1 = exoPlayer) {
        val listener = object : Player.Listener {
            @Deprecated("Deprecated in Java",
                ReplaceWith("currentPosition.value = exoPlayer.currentPosition")
            )
            override fun onPositionDiscontinuity(reason: Int) {
                currentPosition.longValue = exoPlayer.currentPosition
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable {
                run()
            },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            ControlIcon(
                draw = R.drawable.settings_icon,
                run = {
                    settingControl.value = true
                },
                sz = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                { 35 } else{ 25 }
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ControlIcon(
                draw = R.drawable.replay_10_icon,
                run = {
                    exoPlayer.seekTo(exoPlayer.currentPosition - 10_000)
                },
                sz = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                { 40 } else{ 30 }
            )
            Spacer(modifier = Modifier.width(20.dp))
            ControlIcon(
                draw = if (vM.isPlaying.value) {
                    R.drawable.pause_icon
                } else {
                    R.drawable.play_icon
                },
                run = {
                    if (exoPlayer.isPlaying) {
                        vM.isPlaying.value = !vM.isPlaying.value
                        exoPlayer.pause()
                    } else {
                        vM.isPlaying.value =!vM.isPlaying.value
                        exoPlayer.play()
                    }
                },
                sz = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                { 40 } else{ 30 }
            )
            Spacer(modifier = Modifier.width(20.dp))
            ControlIcon(
                draw = R.drawable.forward_10_icon,
                run = {
                    exoPlayer.seekTo(exoPlayer.currentPosition + 10_000)
                },
                sz = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                { 40 } else{ 30 }
            )
        }
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
                    .clickable {
                    },
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    val progress = (currentPosition.longValue.toFloat() / exoPlayer.duration).coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = progress,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { offset ->
                                        val position = (offset.x / size.width) * exoPlayer.duration
                                        exoPlayer.seekTo(position.toLong())
                                    }
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            ControlIcon(
                draw = R.drawable.fullscreen_icon,
                run = {
                    if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                    else{
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                },
                sz = if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                { 30 } else{ 20 }
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
fun ControlIcon(
    draw : Int,
    run : () -> Unit,
    sz : Int
) {
    Icon(
        modifier = Modifier
            .height(sz.dp)
            .aspectRatio(1F)
            .clickable { run() },
        painter = painterResource( draw ),
        contentDescription = "",
        tint = MaterialTheme.colorScheme.secondary
    )
}