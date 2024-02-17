package com.example.streamitv1

import LoadingPage
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.streamitv1.ui.theme.StreamitTheme
import kotlinx.coroutines.delay
import org.json.JSONObject

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {




    @OptIn(UnstableApi::class)
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

            Log.d("armaan is very sad", "wow\n");


            val vM :MyViewModel = viewModel()

            val navController = rememberNavController()



//            LaunchedEffect(vM.userName.value) {
//
//                delay(1000)
//                if (vM.userName.value != "" && vM.password.value != "" ) {
//                    login(
//                        vM.userName.value,
//                        vM.password.value,
//                        navController as NavController,
//                        vM,
//                        this@MainActivity,
//
//                    ) {
//                        navController.navigate("Main")
//                    }
//                } else {
//                    (navController as NavController).navigate("Login")
//                }
//            }
            StreamitTheme {
                NavHost(
                    navController = (navController as NavHostController),
                    startDestination = "Login",
                    route = "LoginSection"
                ) {
                    composable(
                        route = "Load"
                    ) {
                        LoadingPage()
                    }
                    composable(
                        route = "Login"
                    ) {
                        LoginPage(
                            navController = (navController as NavController),
                            vM = vM,
                            this@MainActivity
                        ) {
                            print("armaan is very sad at navigator")
                            navController.navigate("Main")
                        }
                    }
                    composable("ProfileCreation") {
                        ProfileCreation(
                            navController = (navController as NavController),
                            vM = vM
                        )
                    }
                    composable(
                        route = "Signup"
                    ) {
                        SignupPage(
                            navController = (navController as NavController),
                            vM = vM,
                            mainActivity = this@MainActivity
                        ) {

                            navController.navigate("ProfileCreation")
                        }
                    }
                    navigation(startDestination = "HomePage", route = "Main") {
                        composable("HomePage") {
                            MainScreen(
                                navController = (navController as NavController),
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

                                    navController = (navController as NavController),
                                    vM = vM,
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
                                        }/output/",
                                        if (video.getInt("is_live") == 0)
                                            "https://storage.googleapis.com/video-streamit/${
                                                video.getString(
                                                    "id"
                                                )
                                            }/output/manifest.m3u8"
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
                        composable(
                            "ProfilePage/{user}",
                            arguments = listOf(
                                navArgument(name = "user") {
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val userArgument = it.arguments?.getString("user")
                            if (userArgument != null) {
                                ProfilePage(
                                    (navController as NavController),
                                    vM,
                                    UserDetail(
                                        userArgument,
                                        dpURL = "https://storage.googleapis.com/user-streamit/${userArgument}.png"

                                    )
                                )
                            }
                        }


                        composable("Upload") {
                            UploadScreen(
                                navController = (navController as NavController),
                                vM = vM
                            )
                        }
                        navigation(startDestination = "FollowingMain", route = "Following") {
                            composable("FollowingMain") {
                                MainFollow(
                                    navController = (navController as NavController),
                                    vM = vM
                                )
                            }
                            composable("FollowerList") {
                                FollowingListView(
                                    navController = (navController as NavController),
                                    vM = vM
                                )
                            }
                        }
                        composable("Search") {
                            SearchScreen(
                                navController = (navController as NavController),
                                vM = vM
                            )
                        }

                    }

                }
            }
        }
    }


}