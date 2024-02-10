package com.example.streamitv1

import android.os.Build
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sockethandler.setSocket()
        val mSocket = sockethandler.getSocket()
        mSocket.connect()
        setContent {
            val navController = rememberNavController()
            val vM: ViewModel = viewModel()


            StreamitTheme {
                NavHost(
                    navController = navController,
                    startDestination = "Login" ,
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
                    navigation(startDestination = "HomePage", route = "Main"){
                        composable("HomePage") {
                            MainScreen(
                                navController = navController,
                                vM =  vM
                            )
                        }
                        composable(
                            "VideoPlayer/{video}",
                            arguments = listOf(
                                navArgument( name="video"){
                                    type = NavType.StringType
                                }
                            )
                        ) {
                            val videoJson  = it.arguments?.getString("video")
                            if (videoJson!=null){
                                val video = JSONObject(videoJson)
                                VideoView(

                                    navController = navController,
                                    vM = vM,
                                    video = VideoDetail(
                                        video.toString(),
                                        video.getString("id"),
                                        UserDetail(
                                            video.getString("author"),
                                            "https://storage.googleapis.com/user-streamit/${video.getString("author")}.png"
                                        ),
                                        video.getString("title"),
                                        video.getString("tags"),
                                        video.getString("description"),
                                        "https://storage.googleapis.com/video-streamit/${video.getString("id")}/output/manifest.m3u8",
                                        "https://storage.googleapis.com/video-streamit/${video.getString("id")}/${video.getString("id")}.mp4",
                                        "https://storage.googleapis.com/video-streamit/${video.getString("id")}/${video.getString("id")}.png",
                                        video.getInt("likes"),
                                        video.getInt("views")
                                    )
                                )
                            }


                        }
                        navigation(startDestination = "UploadMain", route = "Upload"){
                            composable("UploadMain") {
                                UploadScreen(
                                    navController = navController,
                                    vM = vM
                                )
                            }
                            composable("VideoUpload") {
                                VideoView(
                                    navController = navController,
                                    vM = vM,
                                    video = vM.videoList[0]
                                )
                            }
                            composable("StartLive") {
                                VideoView(
                                    navController = navController,
                                    vM = vM,
                                    video = vM.videoList[0]
                                )
                            }
                        }
                        navigation(startDestination = "FollowingMain", route = "Following"){
                            composable("FollowingMain") {
                                MainFollow(
                                    navController = navController,
                                    vM = vM
                                )
                            }
                            composable("FollowerList") {
                                FollowingListView(
                                    navController = navController,
                                    vM = vM
                                )
                            }
                        }
                        composable("Search") {
                            SearchScreen(
                                navController = navController,
                                vM = vM
                            )
                        }
                        composable("ProfilePage") {
                            ProfilePage(
                                navController = navController,
                                vM = vM
                            )
                        }
                    }
                }
            }
        }
    }
}

