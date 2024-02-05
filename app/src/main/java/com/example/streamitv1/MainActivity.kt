package com.example.streamitv1

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Video
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.streamitv1.ui.theme.StreamitTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                        composable("VideoPlayer") {
                            VideoView(
                                navController = navController,
                                vM = vM,
                                video = vM.videoList[0]
                            )
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

