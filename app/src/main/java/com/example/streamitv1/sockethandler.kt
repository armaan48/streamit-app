package com.example.streamitv1

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object sockethandler {

    @get:Synchronized
    lateinit var mSocket: Socket
    fun setSocket(){
        try{
            mSocket = IO.socket("http://10.0.2.2:8080")
        }catch(e : URISyntaxException){
            println(e)
        }
    }

    @Synchronized
    fun getSocket():Socket{
        return mSocket
    }
}