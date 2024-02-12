package com.example.streamitv1

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.streamitv1.ui.theme.StreamitTheme
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        mSocket.connect()
        fun enterFullscreen() {
            println("type:::enter")
            window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE
            }
        }

        fun exitFullscreen() {
            println("type:::exit")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
        setContent {
            val navController = rememberNavController()
            val vM: ViewModel = viewModel()
            StreamitTheme {
                NavHost(
                    navController = navController,
                    startDestination = "Login",
                    route = "LoginSection"
                ) {
                    composable(
                        route = "Login"
                    ) {
                        LoginPage(
                            navController = navController,
                            mainActivity = this@MainActivity,
                            vM = vM
                        )
                    }
                    composable("ProfileCreation") {
                        ProfileCreation(
                            navController = navController,
                            vM = vM
                        )
                    }
                    composable(
                        route = "Signup"
                    ) {
                        SignupPage(
                            navController = navController,
                            mainActivity = this@MainActivity,
                            vM = vM
                        )
                    }
                    navigation(startDestination = "HomePage", route = "Main") {
                        composable("HomePage") {
                            MainScreen(
                                navController = navController,
                                mainActivity = this@MainActivity,
                                vM = vM
                            )
                        }
                        composable(
                            "VideoPlayer/{video}",
                            arguments = listOf(
                                navArgument(name = "video") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val videoJson = it.arguments?.getString("video")
                            if (videoJson != null) {
                                val video = JSONObject(videoJson)
                                VideoView(

                                    navController = navController,
                                    vM = vM,
                                    mainActivity = this@MainActivity,
                                    video = VideoDetail(
                                        video.toString(),
                                        video.getString("id"),
                                        UserDetail(
                                            video.getString("author"),
                                            "https://storage.googleapis.com/user-streamit/${
                                                video.getString(
                                                    "author"
                                                )
                                            }.png"
                                        ),
                                        video.getString("title"),
                                        video.getString("tags"),
                                        video.getString("description"),
                                        "https://storage.googleapis.com/video-streamit/${
                                            video.getString(
                                                "id"
                                            )
                                        }/output/manifest.m3u8",
                                        if (video.getInt("is_live") == 0)
                                            "https://storage.googleapis.com/video-streamit/${
                                                video.getString(
                                                    "id"
                                                )
                                            }/${
                                                video.getString(
                                                    "id"
                                                )
                                            }.mp4"
                                        else
                                            "https://storage.googleapis.com/video-streamit/streamit-server-channel-${
                                                video.getInt(
                                                    "is_live"
                                                )
                                            }/manifest.m3u8",
                                        "https://storage.googleapis.com/video-streamit/${
                                            video.getString(
                                                "id"
                                            )
                                        }/${video.getString("id")}.png",
                                        video.getInt("likes"),
                                        video.getInt("views"),
                                        video.getInt("is_live")
                                    )
                                ) { type ->
                                    when (type) {
                                        (1) -> {
                                            exitFullscreen()
                                        }

                                        (2) -> {
                                            enterFullscreen()
                                        }
                                    }
                                }
                            }

                        }
                        composable("Upload") {
                            UploadScreen(
                                navController = navController,
                                mainActivity = this@MainActivity,
                                vM = vM
                            )
                        }
                        navigation(startDestination = "FollowingMain", route = "Following") {
                            composable("FollowingMain") {
                                MainFollow(
                                    navController = navController,
                                    mainActivity = this@MainActivity,
                                    vM = vM
                                )
                            }
                            composable("FollowerList") {
                                FollowingListView(
                                    navController = navController,
                                    mainActivity = this@MainActivity,
                                    vM = vM
                                )
                            }
                        }
                        composable("Search") {
                            SearchScreen(
                                navController = navController,
                                mainActivity = this@MainActivity,
                                vM = vM
                            )
                        }
                        composable("ProfilePage") {
                            ProfilePage(
                                navController = navController,
                                mainActivity = this@MainActivity,
                                vM = vM
                            )
                        }
                    }
                }
            }
        }
    }
}