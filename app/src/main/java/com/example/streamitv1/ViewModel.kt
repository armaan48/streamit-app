package com.example.streamitv1

import android.util.Log
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import org.json.JSONArray

class ViewModel :ViewModel(){
    private val _userName =  mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _confirmPassword = mutableStateOf("")
    private val _errorType = mutableStateOf("")
    private val _publicKeyString = mutableStateOf("")
    private val _encryptedPrivateKey =  mutableStateOf("")
    private val _encryptedPassword = mutableStateOf("")
    private val _followingList = mutableStateOf(setOf<String>())


    private val _isOffsetEnabled = mutableStateOf(true)

    private val _isPlaying = mutableStateOf(true)
    private val _videoFocused = mutableStateOf(false)
    private val _speedOpen = mutableStateOf(false)
    private val _qualityOptionOpen = mutableStateOf(false)

    private val _title = mutableStateOf("")
    private val _description = mutableStateOf("")
    private val _tags = mutableStateOf("")
    private val _uploadType = mutableStateOf("")

    private val _videoSize = mutableLongStateOf(0L)
    private val _videoTotalChunks = mutableLongStateOf(0)
    private val _videoChunkSent = mutableLongStateOf(0)
    private val _videoPercentageUploaded = mutableDoubleStateOf(0.0)

    private val _videoThumbnailSize = mutableLongStateOf(0L)
    private val _videoThumbnailTotalChunks = mutableLongStateOf(0)
    private val _videoThumbnailChunkSent = mutableLongStateOf(0)
    private val _videoThumbnailPercentageUploaded = mutableDoubleStateOf(0.0)

    private val _dpSize = mutableLongStateOf(0L)
    private val _dpTotalChunks = mutableLongStateOf(0)
    private val _dpChunkSent = mutableLongStateOf(0)
    private val _dpPercentageUploaded = mutableDoubleStateOf(0.0)


    private val _liveThumbnailSize = mutableLongStateOf(0L)
    private val _liveThumbnailTotalChunks = mutableLongStateOf(0)
    private val _liveThumbnailChunkSent = mutableLongStateOf(0)
    private val _liveThumbnailPercentageUploaded = mutableDoubleStateOf(0.0)




    private val _uploadStatus = mutableIntStateOf(0)

    val uploadStatus: MutableIntState = _uploadStatus


    val videoSize: MutableLongState =  _videoSize
    val videoTotalChunks: MutableLongState = _videoTotalChunks
    val videoChunkSent: MutableLongState = _videoChunkSent
    val videoChunkSize: Long = 1024*1024*1
    val videoPercentageUploaded: MutableDoubleState = _videoPercentageUploaded
    val videoChunkList = mutableStateListOf<String>()


    val videoThumbnailSize: MutableLongState =  _videoThumbnailSize
    val videoThumbnailTotalChunks: MutableLongState = _videoThumbnailTotalChunks
    val videoThumbnailChunkSent: MutableLongState = _videoThumbnailChunkSent
    val videoThumbnailChunkSize: Long = 1024*1024*1
    private val videoThumbnailPercentageUploaded: MutableDoubleState = _videoThumbnailPercentageUploaded
    val videoThumbnailChunkList = mutableStateListOf<String>()



    val liveThumbnailSize: MutableLongState =  _liveThumbnailSize
    val liveThumbnailTotalChunks: MutableLongState = _liveThumbnailTotalChunks
    val liveThumbnailChunkSent: MutableLongState = _liveThumbnailChunkSent
    val liveThumbnailChunkSize: Long = 1024*1024*1
    private val liveThumbnailPercentageUploaded: MutableDoubleState = _liveThumbnailPercentageUploaded
    val liveThumbnailChunkList = mutableStateListOf<String>()


    val dpSize: MutableLongState =  _dpSize
    val dpTotalChunks: MutableLongState = _dpTotalChunks
    val dpChunkSent: MutableLongState = _dpChunkSent
    val dpChunkSize: Long = 1024*1024*1
    private val dpPercentageUploaded: MutableDoubleState = _dpPercentageUploaded
    val dpChunkList = mutableStateListOf<String>()




    val videoList = mutableStateListOf<VideoDetail>()

    private val _searchInput = mutableStateOf("")

    private val _channelName = mutableStateOf("")
    private val _channelDescription = mutableStateOf("")

    private val _descriptionExtended = mutableStateOf(false)

    val userName: MutableState<String> = _userName
    val password: MutableState<String> = _password
    val confirmPassword: MutableState<String> = _confirmPassword
    var followingList: MutableState<Set<String>> = _followingList

    var publicKeyString: MutableState<String> = _publicKeyString
    var encryptedPrivateKey: MutableState<String> = _encryptedPrivateKey
    var encryptedPassword: MutableState<String> = _encryptedPassword
    val errorType: MutableState<String> = _errorType
    val isOffsetEnabled: MutableState<Boolean> = _isOffsetEnabled
    val title: MutableState<String> = _title
    val description: MutableState<String> = _description
    val tags: MutableState<String> = _tags
    val uploadType: MutableState<String> = _uploadType
    val searchInput: MutableState<String> = _searchInput
    val descriptionExtended: MutableState<Boolean> = _descriptionExtended
    val channelName: MutableState<String> = _channelName
    val channelDescription: MutableState<String> = _channelDescription
    val isPlaying: MutableState<Boolean> = _isPlaying
    val videoFocused: MutableState<Boolean> = _videoFocused
    val speedOptionOpen: MutableState<Boolean> = _speedOpen
    val qualityOptionOpen: MutableState<Boolean> = _qualityOptionOpen
    var mSocket = sockethandler.getSocket()

    fun addChunk(type:String, data: String ){
        when(type){
            "video" -> { videoChunkList.add(data) }
            "dp" -> { dpChunkList.add(data) }
            "video-thumbnail" -> { videoThumbnailChunkList.add(data) }
            "live-thumbnail" -> {liveThumbnailChunkList.add(data)}
        }
    }
    var exoPlayer: ExoPlayer? =  null

    fun clearChunks(type: String){
        when(type){
            "video" -> {
                videoChunkList.clear()
                videoChunkSent.longValue = 0
                videoSize.longValue = 0
                videoTotalChunks.longValue = 0
                videoPercentageUploaded.doubleValue = 0.0
            }
            "dp" -> {
                dpChunkList.clear()
                dpChunkSent.longValue = 0
                dpSize.longValue = 0
                dpTotalChunks.longValue = 0
                dpPercentageUploaded.doubleValue = 0.0
            }
            "video-thumbnail" -> {
                videoThumbnailChunkList.clear()
                videoThumbnailChunkSent.longValue = 0
                videoThumbnailSize.longValue = 0
                videoThumbnailTotalChunks.longValue = 0
                videoThumbnailPercentageUploaded.doubleValue = 0.0
            }
            "live-thumbnail" -> {
                liveThumbnailChunkList.clear()
                liveThumbnailChunkSent.longValue = 0
                liveThumbnailSize.longValue = 0
                liveThumbnailTotalChunks.longValue = 0
                liveThumbnailPercentageUploaded.doubleValue = 0.0
            }
        }
    }

    private val videoChunkHandler: (Array<Any>) -> Unit = {
        Log.d("upload" , "trying to send video" )
        if (videoTotalChunks.longValue.toInt() != 0){
            if (videoChunkSent.longValue == videoTotalChunks.longValue) {
                Log.d("upload", "video CHUNKS TRANSFERRED ${videoChunkSent.longValue}")
                uploadStatus.intValue = 2
                mSocket.emit("video-uploaded", "nothing")
            } else {
                uploadChunk( this , videoSize , videoChunkSize , videoChunkSent , videoPercentageUploaded , videoTotalChunks , videoChunkList,  "send-video")
            }
        }
    }

    private val videoThumbnailChunkHandler: (Array<Any>) -> Unit = {
        Log.d("upload" , "trying to send video-thumbnail" )
        if (videoThumbnailTotalChunks.longValue.toInt() != 0){
            if (videoThumbnailChunkSent.longValue == videoThumbnailTotalChunks.longValue) {
                Log.d("CHUNK", "thumbnail CHUNKS TRANSFERRED ${videoThumbnailChunkSent.longValue}")
                mSocket.emit("video-thumbnail-uploaded", "nothing")
            } else {
                uploadChunk( this , videoThumbnailSize , videoThumbnailChunkSize , videoThumbnailChunkSent , videoThumbnailPercentageUploaded , videoThumbnailTotalChunks , videoThumbnailChunkList,  "send-video-thumbnail")
            }
        }
    }
    fun addFollowing(username:String){
        _followingList.value = _followingList.value.plus(username)
    }
    fun removeFollowing(username: String){
        _followingList.value = _followingList.value.minus(username)
    }
    private val liveThumbnailChunkHandler: (Array<Any>) -> Unit = {
        Log.d("upload" , "trying to live-thumbnail")
        if (liveThumbnailTotalChunks.longValue.toInt() != 0){
            if (liveThumbnailChunkSent.longValue == liveThumbnailTotalChunks.longValue) {
                Log.d("CHUNK", "thumbnail CHUNKS TRANSFERRED ${liveThumbnailChunkSent.longValue}")
                mSocket.emit("live-thumbnail-uploaded", "nothing")
            } else {
                uploadChunk( this , liveThumbnailSize , liveThumbnailChunkSize , liveThumbnailChunkSent , liveThumbnailPercentageUploaded , liveThumbnailTotalChunks , liveThumbnailChunkList,  "send-live-thumbnail")
            }
        }
    }

    private val dpChunkHandler: (Array<Any>) -> Unit = {
        if (dpTotalChunks.longValue.toInt() != 0){
            if (dpChunkSent.longValue == dpTotalChunks.longValue) {
                Log.d("CHUNK", "dp CHUNKS TRANSFERRED ${dpChunkSent.longValue}")
                mSocket.emit("dp-uploaded", "nothing")
            } else {
                uploadChunk( this , dpSize , dpChunkSize , dpChunkSent , dpPercentageUploaded , dpTotalChunks , dpChunkList,  "send-dp")
            }
        }
    }

    private val videoListHandler: (Array<Any>) -> Unit = {
        println("I GOT THE VIDEOS ${it[0].toString()}")
        videoList.clear()
        val newVideoList = it[0] as JSONArray
        for (i in 0 until newVideoList.length()){
            val video = newVideoList.getJSONObject(i)
            val videoFormatted = VideoDetail(
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
            )
            Log.d("VIDEO: $i" , videoFormatted.toString() )
            videoList.add(videoFormatted)
        }

    }

    private val followingListHandler: (Array<Any>) -> Unit = {
        _followingList.value = setOf()
        val newFollowingList = it[0] as JSONArray
        for (i in 0 until newFollowingList.length()){
            val user = newFollowingList.getJSONObject(i)
            _followingList.value = _followingList.value.plus(user.getString("following_id"))
        }
        Log.d("FOLLOWING LIST" ,_followingList.value.toString() )
    }

    fun reset(){
        userName.value=""
        password.value=""
        confirmPassword.value=""
        errorType.value=""
    }
    init{
        mSocket = sockethandler.getSocket()
        mSocket.on("give-video" , videoChunkHandler)
        mSocket.on("give-video-thumbnail" , videoThumbnailChunkHandler)
        mSocket.on("give-live-video-thumbnail" , liveThumbnailChunkHandler)
        mSocket.on("give-dp" , dpChunkHandler)



        mSocket.on("send-videos", videoListHandler)
        mSocket.on("send-liked-video-list" ){}
        mSocket.on("send-following-list" , followingListHandler)
    }

}
