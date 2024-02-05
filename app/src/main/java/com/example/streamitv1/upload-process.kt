package com.example.streamitv1


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min


@RequiresApi(Build.VERSION_CODES.O)
fun startChunking(
    vM: ViewModel,
    uri: Uri,
    size: MutableLongState,
    chunkSize: Long,
    chunkSent: MutableLongState,
    totalChunks: MutableLongState,
    scope: CoroutineScope,
    context: Context,
    type: String,
){
    scope.launch {
        withContext(Dispatchers.IO) {

            val cursor = context.contentResolver.query(
                uri,
                arrayOf(
                    MediaStore.Video.Media.SIZE
                ),
                null,
                null
            )
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    size.value = cursor.getLong(0)
                }
                cursor.close()
            }
            val inputStream = context.contentResolver.openInputStream(uri)
            totalChunks.longValue = (size.longValue  + chunkSize - 1).toInt() / chunkSize;
            Log.d("CHUNK" , "TOTAL RQ:${totalChunks.longValue.toString()}");
            inputStream.use { inputStream ->
                val buffer = ByteArray(chunkSize.toInt())
                var byteRead = inputStream?.read(buffer)
                while (byteRead != -1) {
                    if (byteRead != null) {
                        val base64Data = Base64.encodeToString(buffer, 0);
                        vM.addChunk(type,base64Data);
                    }
                    byteRead = inputStream?.read(buffer)
                }
                Log.d("CHUNK" ,"${totalChunks.value} CHUNKS is made" )
            }
        }
    }
}







fun uploadChunk(
    vM: ViewModel,
    size: MutableLongState,
    chunkSize: Long,
    chunkSent: MutableLongState,
    percentageUploaded: MutableDoubleState,
    totalChunks: MutableLongState,
    chunkList: SnapshotStateList<String>,
    event: String,
) {
    if (totalChunks.longValue > chunkSent.longValue) {
        Log.d("CHUNK", "${chunkSent.longValue} sending")
        vM.mSocket.emit(event, chunkList[chunkSent.longValue.toInt()])
        chunkSent.longValue++

        percentageUploaded.doubleValue =
            min(100.0, (( ( chunkSent.value*chunkSize) * 100) / (size.longValue)).toDouble())
    } else {
        Log.d("CHUNK", "fucked")
    }

}