package com.example.streamitv1

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.streamitv1.ui.theme.rosarioFamily
import kotlinx.coroutines.delay
import org.json.JSONObject


@SuppressLint("SourceLockedOrientationActivity")
@OptIn(UnstableApi::class)
@Composable
fun VideoView(
    navController: NavController,
    vM: MyViewModel,
    video: VideoDetail,
    onclick: (type: Int) -> Unit
) {
    val context = LocalContext.current
    vM.exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(
                MediaItem.fromUri(
                    video.videoURL2
                )
            )
            prepare()
            playWhenReady = vM.isPlaying.value
        }
    }

    LaunchedEffect(vM.exoPlayer) {
        vM.exoPlayer!!.seekTo(vM.currentPosition.longValue)
    }

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
    val activity = LocalContext.current as Activity

    BackHandler(
        enabled = (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
    ) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    val lifecycleObserver = remember {
        object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            fun onPause() {
                vM.isPlaying.value = false
                vM.exoPlayer!!.playWhenReady = false
            }
        }
    }

    val activity2 = (context as? ComponentActivity)
    activity2?.lifecycle?.addObserver(lifecycleObserver)


    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        onclick(2)
    } else {
        onclick(1)
    }

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
                DisposableEffect(key1 = Unit) {
                    onDispose {
                        println("release test")
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
                Column {
                    AnimatedVisibility(
                        visible = vM.videoFocused.value,
                        enter = EnterTransition.None,
                        exit = ExitTransition.None
                    ) {
                        VideoControls(
                            exoPlayer = vM.exoPlayer!!,
                            vM = vM,
                            run = {
                                vM.videoFocused.value = !vM.videoFocused.value
                            },
                            configuration = configuration,
                            settingControl = settingState,
                            type = "Fullscreen"
                        )
                    }
                }
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
                        DisposableEffect(key1 = Unit) {
                            onDispose {
                                println("release test")
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
                        Column {
                            AnimatedVisibility(
                                visible = vM.videoFocused.value,
                                enter = EnterTransition.None,
                                exit = ExitTransition.None
                            ) {
                                VideoControls(
                                    exoPlayer = vM.exoPlayer!!,
                                    vM = vM,
                                    run = {
                                        vM.videoFocused.value = !vM.videoFocused.value
                                    },
                                    configuration = configuration,
                                    settingControl = settingState,
                                    type = "Normal"
                                )
                            }
                        }
                    }
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F),
                        verticalArrangement = Arrangement.Top,
                        horizontalArrangement = Arrangement.Center,
                        columns = GridCells.Adaptive(minSize = 370.dp)
                    ) {
                        item{
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Spacer(modifier = Modifier.width(20.dp))
                                Column(
                                    modifier = Modifier
                                        .weight(1F),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(60.dp),
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
                                                        vM.descriptionExtended.value =
                                                            !vM.descriptionExtended.value
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
                                        enter = expandVertically(
                                            animationSpec = tween(durationMillis = 200),
                                            expandFrom = Alignment.Top
                                        ) { 60 } + fadeIn(),
                                        exit = shrinkVertically(
                                            animationSpec = tween(durationMillis = 200),
                                            shrinkTowards = Alignment.Bottom
                                        ) { 0 } + fadeOut()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalArrangement = Arrangement.Bottom,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .animateContentSize()
                                                    .fillMaxWidth(),
                                                verticalArrangement = Arrangement.Top,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = video.description,
                                                    fontFamily = rosarioFamily,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                            Spacer(modifier =Modifier.height(10.dp))
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .height(60.dp)
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
                                                        vM.filterVideo(video.author.username)
                                                        navController.navigate("ProfilePage/${video.author.username}")
                                                    },
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
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
                                                            ).clickable{
                                                                vM.filterVideo(video.author.username)
                                                                navController.navigate("ProfilePage/${video.author.username}")
                                                            }
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
                                    }
                                    Row(
                                        modifier = Modifier
                                            .height(60.dp)
                                            .fillMaxWidth(),
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
                                                image = if (vM.likedVideoList.value.contains(video.id)) R.drawable.filled_like_icon else R.drawable.like_icon,
                                                text = "${video.likes}",
                                                onclick = {
                                                    val data = JSONObject()
                                                    data.put("user_id", vM.userName.value)
                                                    data.put("video_id", video.id)
                                                    if (vM.likedVideoList.value.contains(video.id)) {
                                                        vM.mSocket.emit("unlike", data)
                                                        vM.unlikeVideo(video.id)
                                                    } else {
                                                        vM.mSocket.emit("like", data)
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
                                                image = -1,
                                                text = if (vM.followingList.value.contains(video.author)) "Unsubscribe" else "Subscribe",
                                                onclick = {
                                                    val data = JSONObject()
                                                    data.put("follower_id", vM.userName.value)
                                                    data.put("following_id", video.author.username)
                                                    if (vM.followingList.value.contains(video.author)) {
                                                        vM.mSocket.emit("unfollow", data)
                                                        vM.removeFollowing(video.author)
                                                    } else {
                                                        vM.mSocket.emit("follow", data)
                                                        vM.addFollowing(video.author)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                        }
                        item{
                            Spacer(modifier = Modifier.height(18.dp))
                        }
                        item{
                            Row(
                                modifier = Modifier.fillMaxWidth().height(2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ){
                                Divider(
                                    modifier = Modifier.fillMaxWidth(0.9F),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                        item{
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(vM.videoList.size) {
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
        ) {
            when (settingType.value) {
                "Main" -> {
                    MainSettingOptions(settingType)
                }

                "Quality" -> {
                    QualitySettingOptions(vM , video)
                }

                else -> {
                    SpeedSettingOptions(vM)
                }
            }
        }
    }
}

@Composable
fun SpeedSettingOptions(vM:MyViewModel) {
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
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
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            SettingOption(
                text = "0.25x",
                isSelected = (vM.speedChoice.value == 0),
                onclick = {
                    vM.speedChoice.value = 0
                    vM.exoPlayer?.setPlaybackSpeed(0.25f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "0.5x",
                isSelected = (vM.speedChoice.value == 1),
                onclick = {
                    vM.speedChoice.value = 1
                    vM.exoPlayer?.setPlaybackSpeed(0.5f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "0.75x",
                isSelected = (vM.speedChoice.value == 2),
                onclick = {
                    vM.speedChoice.value = 2
                    vM.exoPlayer?.setPlaybackSpeed(0.75f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "Normal",
                isSelected = (vM.speedChoice.value == 3),
                onclick = {
                    vM.speedChoice.value = 3
                    vM.exoPlayer?.setPlaybackSpeed(1f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "1.25x",
                isSelected = (vM.speedChoice.value == 4),
                onclick = {
                    vM.speedChoice.value = 4
                    vM.exoPlayer?.setPlaybackSpeed(1.25f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "1.5x",
                isSelected = (vM.speedChoice.value == 5),
                onclick = {
                    vM.speedChoice.value = 5
                    vM.exoPlayer?.setPlaybackSpeed(1.5f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "1.75x",
                isSelected = (vM.speedChoice.value == 6),
                onclick = {
                    vM.speedChoice.value = 6
                    vM.exoPlayer?.setPlaybackSpeed(1.75f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "2.0x",
                isSelected = (vM.speedChoice.value == 7),
                onclick = {
                    vM.speedChoice.value = 7
                    vM.exoPlayer?.setPlaybackSpeed(2f)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun QualitySettingOptions(vM:MyViewModel , video:VideoDetail) {
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
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
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            SettingOption(
                text = "Auto",
                isSelected = (vM.qualityChoice.intValue==0),
                onclick = {
                    vM.qualityChoice.intValue = 0
                    vM.currentVideoURL.value = "${video.videoURL1}manifest.m3u8"
                    vM.exoPlayer?.apply {
                        setMediaItem(
                            MediaItem.fromUri(
                                "${video.videoURL1}manifest.m3u8"
                            )
                        )
                        playWhenReady = vM.isPlaying.value
                    }
//                    vM.exoPlayer?.seekTo(vM.currentPosition.longValue)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "480p",
                isSelected = (vM.qualityChoice.intValue==1),
                onclick = {
                    vM.qualityChoice.intValue = 1
                    vM.currentVideoURL.value = "${video.videoURL1}q1.mp4"
                    vM.exoPlayer?.apply {
                        setMediaItem(
                            MediaItem.fromUri(
                                "${video.videoURL1}q1.mp4"
                            )
                        )
                        playWhenReady = vM.isPlaying.value
                    }
//                    vM.exoPlayer?.seekTo(vM.currentPosition.longValue)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "720p",
                isSelected = (vM.qualityChoice.intValue==2),
                onclick = {
                    vM.qualityChoice.intValue = 2
                    vM.currentVideoURL.value = "${video.videoURL1}q2.mp4"
                    vM.exoPlayer?.apply {
                        setMediaItem(
                            MediaItem.fromUri(
                                "${video.videoURL1}q2.mp4"
                            )
                        )
                        playWhenReady = vM.isPlaying.value
                    }
//                    vM.exoPlayer?.seekTo(vM.currentPosition.longValue)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "1080p",
                isSelected = (vM.qualityChoice.intValue==3),
                onclick = {
                    vM.qualityChoice.intValue = 3
                    vM.exoPlayer?.apply {
                        setMediaItem(
                            MediaItem.fromUri(
                                "${video.videoURL1}q3.mp4"
                            )
                        )
                        playWhenReady = vM.isPlaying.value
                    }
//                    vM.exoPlayer?.seekTo(vM.currentPosition.longValue)
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}

@Composable
fun MainSettingOptions(
    settingType: MutableState<String>
) {
    LazyColumn(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
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
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            SettingOption(
                text = "Playback speed",
                isSelected = false,
                onclick = {
                    settingType.value = "Speed"
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
        }
        item {
            SettingOption(
                text = "Quality",
                isSelected = false,
                onclick = {
                    settingType.value = "Quality"
                }
            )
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SettingOption(
    text: String,
    isSelected: Boolean,
    onclick: () -> Unit
) {
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
        Spacer(modifier = Modifier.width(15.dp))
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1F),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (isSelected) {
                Icon(
                    modifier = Modifier
                        .height(25.dp)
                        .aspectRatio(1F),
                    painter = painterResource(R.drawable.tick_icon),
                    contentDescription = "back_arrow_icon_light",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Text(
            text = text,
            fontFamily = rosarioFamily,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 15.sp
        )
    }
}

@Composable
fun VideoViewButton(
    image: Int,
    text: String,
    onclick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxHeight(0.95F)
            .fillMaxWidth(0.9F)
            .clickable { onclick() }
            .background(MaterialTheme.colorScheme.secondary)
            .clip(shape = RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (image != -1) {
            Icon(
                modifier = Modifier
                    .height(20.dp)
                    .aspectRatio(1F),
                painter = painterResource(image),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            textAlign = TextAlign.Center,
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = rosarioFamily,
            color = MaterialTheme.colorScheme.primary
        )
    }

}

@SuppressLint("SourceLockedOrientationActivity")
@OptIn(UnstableApi::class)
@Composable
fun VideoControls(
    exoPlayer: ExoPlayer,
    vM: MyViewModel,
    run: () -> Unit,
    configuration: Configuration,
    settingControl: MutableState<Boolean>,
    type: String
) {
    val activity = LocalContext.current as Activity

    DisposableEffect(key1 = exoPlayer) {
        val listener = object : Player.Listener {
            @Deprecated(
                "Deprecated in Java",
                ReplaceWith("currentPosition.value = exoPlayer.currentPosition")
            )
            override fun onPositionDiscontinuity(reason: Int) {
                vM.currentPosition.longValue = exoPlayer.currentPosition
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(exoPlayer) {
        while (true) {
            vM.currentPosition.longValue = exoPlayer.currentPosition
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable {
                run()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
                .background(Color.Black.copy(alpha = 0.2f))
                .clickable {
                    run()
                },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    sz = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        35
                    } else {
                        25
                    }
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
                    sz = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        40
                    } else {
                        30
                    }
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
                            vM.isPlaying.value = !vM.isPlaying.value
                            exoPlayer.play()
                        }
                    },
                    sz = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        40
                    } else {
                        30
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                ControlIcon(
                    draw = R.drawable.forward_10_icon,
                    run = {
                        exoPlayer.seekTo(exoPlayer.currentPosition + 10_000)
                    },
                    sz = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        40
                    } else {
                        30
                    }
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
                        val progress =
                            (vM.currentPosition.longValue.toFloat() / exoPlayer.duration).coerceIn(
                                0f,
                                1f
                            )
                        LinearProgressIndicator(
                            progress = progress,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = { offset ->
                                            val position =
                                                (offset.x / size.width) * exoPlayer.duration
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
                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        } else {
                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//                            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                    },
                    sz = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        35
                    } else {
                        25
                    }
                )
                Spacer(modifier = Modifier.width(30.dp))
            }
        }
        if (type == "Fullscreen") Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun ControlIcon(
    draw: Int,
    run: () -> Unit,
    sz: Int
) {
    Icon(
        modifier = Modifier
            .height(sz.dp)
            .aspectRatio(1F)
            .clickable { run() },
        painter = painterResource(draw),
        contentDescription = "",
        tint = MaterialTheme.colorScheme.secondary
    )
}