package com.example.streamitv1

import android.util.Log
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.json.JSONArray

class ViewModel :ViewModel(){
    private val _userName =  mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _confirmPassword = mutableStateOf("")
    private val _errorType = mutableStateOf("")
    private val _publicKeyString = mutableStateOf("")
    private val _encryptedPrivateKey =  mutableStateOf("")
    private val _encryptedPassword = mutableStateOf("")


    private val _isOffsetEnabled = mutableStateOf(true)

    private val _title = mutableStateOf("")
    private val _description = mutableStateOf("")
    private val _tags = mutableStateOf("")
    private val _uploadType = mutableStateOf("")

    private val _videoSize = mutableLongStateOf(0L)
    private val _videoTotalChunks = mutableLongStateOf(0)
    private val _videoChunkSent = mutableLongStateOf(0)
    private val _videoPercentageUploaded = mutableDoubleStateOf(0.0)

    private val _thumbnailSize = mutableLongStateOf(0L)
    private val _thumbnailTotalChunks = mutableLongStateOf(0)
    private val _thumbnailChunkSent = mutableLongStateOf(0)
    private val _thumbnailPercentageUploaded = mutableDoubleStateOf(0.0)

    private val _dpSize = mutableLongStateOf(0L)
    private val _dpTotalChunks = mutableLongStateOf(0)
    private val _dpChunkSent = mutableLongStateOf(0)
    private val _dpPercentageUploaded = mutableDoubleStateOf(0.0)


    val videoSize: MutableLongState =  _videoSize
    val videoTotalChunks: MutableLongState = _videoTotalChunks
    val videoChunkSent: MutableLongState = _videoChunkSent
    val videoChunkSize: Long = 1024*1024*1
    val videoPercentageUploaded: MutableDoubleState = _videoPercentageUploaded
    val videoChunkList = mutableStateListOf<String>()

    val thumbnailSize: MutableLongState =  _thumbnailSize
    val thumbnailTotalChunks: MutableLongState = _thumbnailTotalChunks
    val thumbnailChunkSent: MutableLongState = _thumbnailChunkSent
    val thumbnailChunkSize: Long = 1024*1024*1
    private val thumbnailPercentageUploaded: MutableDoubleState = _thumbnailPercentageUploaded
    val thumbnailChunkList = mutableStateListOf<String>()


    val dpSize: MutableLongState =  _dpSize
    val dpTotalChunks: MutableLongState = _dpTotalChunks
    val dpChunkSent: MutableLongState = _dpChunkSent
    val dpChunkSize: Long = 1024*1024*1
    private val dpPercentageUploaded: MutableDoubleState = _dpPercentageUploaded
    private val dpChunkList = mutableStateListOf<String>()




    val videoList = mutableStateListOf<VideoDetail>()

    private val _searchInput = mutableStateOf("")

    private val _channelName = mutableStateOf("")
    private val _channelDescription = mutableStateOf("")

    private val _descriptionExtended = mutableStateOf(false)

    val userName: MutableState<String> = _userName
    val password: MutableState<String> = _password
    val confirmPassword: MutableState<String> = _confirmPassword

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
    var mSocket = sockethandler.getSocket()




    fun addChunk(type:String, data: String ){
        when(type){
            "video" -> { videoChunkList.add(data) }
            "dp" -> { dpChunkList.add(data) }
            "thumbnail" -> { thumbnailChunkList.add(data) }
        }
    }

    private val videoChunkHandler: (Array<Any>) -> Unit = {
        Log.d("upload" , "trying to send video" )
        if (videoTotalChunks.longValue.toInt() != 0){
            if (videoChunkSent.longValue == videoTotalChunks.longValue) {
                Log.d("upload", "video CHUNKS TRANSFERRED ${videoChunkSent.longValue}")
                mSocket.emit("video-uploaded", "nothing")
            } else {
                uploadChunk( this , videoSize , videoChunkSize , videoChunkSent , videoPercentageUploaded , videoTotalChunks , videoChunkList,  "send-video")
            }
        }
    }

    private val thumbnailChunkHandler: (Array<Any>) -> Unit = {
        Log.d("upload" , "trying to send video" )
        if (thumbnailTotalChunks.longValue.toInt() != 0){
            if (thumbnailChunkSent.longValue == thumbnailTotalChunks.longValue) {
                Log.d("CHUNK", "thumbnail CHUNKS TRANSFERRED ${thumbnailChunkSent.longValue}")
                mSocket.emit("thumbnail-uploaded", "nothing")
            } else {
                uploadChunk( this , thumbnailSize , thumbnailChunkSize , thumbnailChunkSent , thumbnailPercentageUploaded , thumbnailTotalChunks , thumbnailChunkList,  "send-thumbnail")
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
                "https://storage.googleapis.com/video-streamit/${video.getString("id")}/${video.getString("id")}.png",
            )
            Log.d("VIDEO: $i" , videoFormatted.toString() )
            videoList.add(videoFormatted)
        }

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
        mSocket.on("give-thumbnail" , thumbnailChunkHandler)
        mSocket.on("give-dp" , dpChunkHandler)
        mSocket.on("send-videos", videoListHandler)
    }




}
