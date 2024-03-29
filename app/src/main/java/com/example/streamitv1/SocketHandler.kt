package com.example.streamitv1

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException

object SocketHandler {

    @get:Synchronized
    lateinit var mSocket: Socket
    fun setSocket() {
        try {
            mSocket = IO.socket("http://34.131.75.81")
        } catch (e: URISyntaxException) {
            println(e)
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }
}