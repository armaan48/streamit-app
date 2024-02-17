package com.example.streamitv1

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.streamitv1.ui.theme.rosarioFamily
import org.json.JSONObject

@Composable
fun ProfilePage(
    navController: NavController, vM: MyViewModel, user:UserDetail
) {
    val w = LocalConfiguration.current.screenWidthDp.dp


    LaunchedEffect(true){
        vM.mSocket.emit("give-user-data" , user.username)
    }
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
                text = if (vM.channelNameMap.containsKey(user.username)) {
                    vM.channelNameMap[user.username] as String
                } else {
                    "Channel Name"
                },
                vM = vM

            )
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Row(
                modifier = Modifier
                    .height(90.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(modifier = Modifier.width(25.dp))
                Column(
                    modifier = Modifier
                        .height(65.dp)
                        .width(65.dp)
                        .clickable {
                            // Add your click handling logic here
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .height(55.dp)
                                .width(55.dp)
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
                            model = "https://storage.googleapis.com/user-streamit/${user.username}.png",
                            modifier = Modifier
                                .size(55.dp)
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
                            text = user.username,
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
                            text = if (vM.subscriberCount.containsKey(user.username) ) "${vM.subscriberCount[user.username]} subscribers" else "0 subscribers",
                            fontFamily = rosarioFamily,
                            fontWeight = FontWeight.Light,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                if (vM.userName.value!=user.username){
                    LoginNSignupButton(
                        text = if (vM.followingList.value.contains(user)) "Unsubscribe" else "Subscribe",
                        onclick = {
                            val data = JSONObject()
                            data.put("follower_id", vM.userName.value)
                            data.put("following_id", user.username)
                            if (vM.followingList.value.contains(user)) {
                                vM.mSocket.emit("unfollow", data)
                                vM.removeFollowing(user)
                            } else {
                                vM.mSocket.emit("follow", data)
                                vM.addFollowing(user)
                            }
                        }, modifier = Modifier
                            .fillMaxHeight(0.5F)
                            .width(110.dp)
                    )
                }
                Spacer(modifier = Modifier.width(25.dp))
            }
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(5.dp))
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Center,
                columns = GridCells.Adaptive(minSize = 370.dp)
            ) {
                items(vM.profileVideoList.size) {
                    VideoPreview(
                        navController = navController,
                        vM = vM,
                        vM.profileVideoList[it],
                    )
                }
            }
        }
    }
}