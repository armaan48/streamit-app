package com.example.streamitv1

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun MainScreen(
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
                text = "Stream it",
                vM = vM
            )
            Divider(
                modifier = Modifier.fillMaxWidth(0.95F),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
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
            Spacer(
                modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
            )
        }
    }
}

