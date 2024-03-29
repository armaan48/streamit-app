package com.example.streamitv1

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import org.json.JSONObject
import java.security.KeyPairGenerator
import java.util.Base64


@RequiresApi(Build.VERSION_CODES.O)
fun signup(
    username: String,
    password: String,
    navController: NavController,
    vM: MyViewModel,
    mainActivity: MainActivity,
    onSuccess: ()->Unit
) {

    val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyPairGenerator.initialize(3072)
    val keyPair = keyPairGenerator.generateKeyPair()

    val privateKey = keyPair.private
    val privateKeyString = Base64.getEncoder().encodeToString(privateKey.encoded)
    val publicKey = keyPair.public
    vM.publicKeyString.value = Base64.getEncoder().encodeToString(publicKey.encoded)

    val finalPasswordString = "$username$$password"
    val salt = "1b0c4f19941c073e87cfa55323a6db6d"
    val pdkdf2key = generateHash(finalPasswordString, salt)
    vM.encryptedPrivateKey.value = encrypt(privateKeyString, pdkdf2key) as String
    vM.encryptedPassword.value = encryptMessage(finalPasswordString, vM.publicKeyString.value)

    val mSocket = SocketHandler.getSocket()
    val data = JSONObject()
    data.put("username", vM.userName.value)
    data.put("publicKey", vM.publicKeyString.value)
    data.put("encryptedPrivateKey", vM.encryptedPrivateKey.value)
    data.put("encryptedPassword", vM.encryptedPassword.value)

    Log.d("login-signup-process", data.toString())

    mSocket.emit("sign-up", data)


    // Handle the "passSignup" event
    mSocket.on("passSignup") { _ ->
        println("login-signup-process passed")
        vM.errorType.value = ""
        vM.mSocket.emit("give-video-list", "nothing")
        mainActivity.runOnUiThread(onSuccess)
    }

    mSocket.on("failSignUp") { args ->
        //val dataReceived = args[0] as JSONObject
        vM.errorType.value = args[0] as String
        println("login-signup-process ${vM.errorType.value}")

    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun login (
    username: String,
    password: String,
    navController: NavController,
    vM: MyViewModel,
    mainActivity: MainActivity,
    onSuccess: () -> Unit
) {

    val mSocket = SocketHandler.getSocket()
    val data = JSONObject()
    data.put("username", username)

    mSocket.emit("login", data)

    // Handle the "passSignup" event
    mSocket.on("failLogin") { args ->

        //val dataReceived = args[0] as JSONObject
        vM.errorType.value = "UserNameNotFound"
        println("login-signup-process ${args[0] as String}")

    }

    // Handle the "failSignUp" event

    mSocket.on("passLogin") { args ->
        try {

            val dataReceived = args[0] as JSONObject

            val finalPasswordString = "$username$$password"
            println(finalPasswordString)
            val salt = "1b0c4f19941c073e87cfa55323a6db6d"
            val pdkdf2key = generateHash(finalPasswordString, salt)

            val decryptedPvtKey = decrypt(dataReceived.getString("encryptedPrivateKey"), pdkdf2key)
            if (decryptedPvtKey != null) {
                val decryptedPassword = decryptMessage(
                    dataReceived.getString("encryptedPassword"),
                    decryptedPvtKey.toString()
                )
                println("passed $decryptedPassword")
                vM.errorType.value = ""

                // this line is the problem
                vM.mSocket.emit("give-liked-video-list", vM.userName.value)
                vM.mSocket.emit("give-following-list", vM.userName.value)
                vM.mSocket.emit("give-video-list", "nothing")
                vM.mSocket.emit("give-following-video-list", vM.userName.value)
                mainActivity.runOnUiThread(onSuccess)
            } else {
                vM.errorType.value = "IncorrectInfo"
                println("login-signup-process ${vM.errorType.value}")

            }
        } catch (e: Error) {
            vM.errorType.value = "IncorrectInfo"
            println("login-signup-process ${vM.errorType.value}")

        }
    }

}
