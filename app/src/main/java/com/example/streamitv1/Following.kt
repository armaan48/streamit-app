package com.example.streamitv1

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.streamitv1.ui.theme.rosarioFamily

@Composable
fun FollowingListView(
    navController: NavController,
    vM: ViewModel
){
    val w = LocalConfiguration.current.screenWidthDp.dp

    val offsetX by animateDpAsState(
        targetValue = if (!vM.isOffsetEnabled.value) (5 * w) / 8 else 0.dp,
        animationSpec = tween(250, easing = LinearEasing),
        label = ""
    )

    SideOptions(navController,vM)
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
                text = "Following",
                vM = vM
            )
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(vM.followingList.value.toList()){
                    FollowerList(username = it, dp = "https://storage.googleapis.com/user-streamit/${it}.png")
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

@Composable
fun FollowerList(
    username : String,
    dp : String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(25.dp))
        Column(
            modifier = Modifier
                .height(55.dp)
                .width(55.dp)
                .clickable {
                    // Add your click handling logic here
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
                )
                AsyncImage(
                    model = dp,
                    modifier = Modifier
                        .size(39.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.user_icon),
                    error = painterResource(id = R.drawable.user_icon),
                    contentDescription = "Channel Dp",
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = username,
            color = MaterialTheme.colorScheme.secondary,
            fontFamily = rosarioFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MainFollow(
    navController: NavController,
    vM: ViewModel
){
    val w = LocalConfiguration.current.screenWidthDp.dp

    val offsetX by animateDpAsState(
        targetValue = if (!vM.isOffsetEnabled.value) (5 * w) / 8 else 0.dp,
        animationSpec = tween(250, easing = LinearEasing),
        label = ""
    )

    SideOptions(navController,vM)
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
                text = "Following",
                vM = vM
            )
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Spacer(modifier = Modifier.width(20.dp))
                    Row(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight(),
                    ){
                        FollowList(vM)
                    }
                    Column(
                        modifier = Modifier
                            .width(40.dp)
                            .fillMaxHeight()
                            .clickable {

                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("FollowerList")
                                },
                            text = "All",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Divider(
                    modifier = Modifier.fillMaxWidth(0.95F),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.tertiary
                )
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalArrangement = Arrangement.Center,
                    columns = GridCells.Adaptive(minSize = 370.dp)
                ) {
                    items(vM.videoList.size){
                        VideoPreview(
                            navController = navController,
                            vM = vM,
                            vM.videoList[it],
                        )
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

@Composable
fun FollowList(
    vM:ViewModel
) {
    LazyRow(
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        items(vM.followingList.value.toList()){
            FollowListIcon(username = it, dp = "https://storage.googleapis.com/user-streamit/${it}.png")
        }
    }
}

@Composable
fun FollowListIcon(
    username: String,
    dp : String,
){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(0.9F),
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
                model = dp,
                modifier = Modifier
                    .size(39.dp)
                    .clip(CircleShape),
                placeholder = painterResource(id = R.drawable.user_icon),
                error = painterResource(id = R.drawable.user_icon),
                contentDescription = "User Dp",
            )
        }
    }
}

