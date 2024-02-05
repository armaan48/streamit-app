package com.example.streamitv1

import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.spec.KeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = "SomeRandomSecret"

fun generateHash(password: String, salt: String): String {
    val combinedSalt = "$salt$SECRET".toByteArray()
    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded
    return hash.toHex()
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }

private var secretKey: SecretKeySpec? = null
private lateinit var key: ByteArray

// set Key
fun setKey(myKey: String) {
    var sha: MessageDigest? = null
    try {
        key = myKey.toByteArray(charset("UTF-8"))
        sha = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = key.copyOf(16)
        secretKey = SecretKeySpec(key, "AES")
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    } catch (e: UnsupportedEncodingException) {
        e.printStackTrace()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun encrypt(strToEncrypt: String, secret: String): String? {
    try {
        setKey(secret)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal
            (strToEncrypt.toByteArray(charset("UTF-8"))))
    } catch (e: Exception) {

        println("Error while encrypting: $e")
    }
    return null
}

// method to encrypt the secret text using key
@RequiresApi(Build.VERSION_CODES.O)
fun decrypt(strToDecrypt: String?, secret: String): String? {
    try {
        setKey(secret)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return String(cipher.doFinal(Base64.getDecoder().
        decode(strToDecrypt)))
    } catch (e: Exception) {
        println(e)
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.O)
@Throws(GeneralSecurityException::class, IOException::class)
fun loadPublicKey(stored: String): Key {
    val data: ByteArray = Base64.getDecoder().
    decode(stored.toByteArray())
    val spec = X509EncodedKeySpec(data)
    val fact = KeyFactory.getInstance("RSA")
    return fact.generatePublic(spec)
}

@RequiresApi(Build.VERSION_CODES.O)
@Throws(GeneralSecurityException::class)


fun loadPrivateKey(key64: String): PrivateKey {
    val clear: ByteArray = Base64.getDecoder().
    decode(key64.toByteArray())
    val keySpec = PKCS8EncodedKeySpec(clear)
    val fact = KeyFactory.getInstance("RSA")
    val priv = fact.generatePrivate(keySpec)
    Arrays.fill(clear, 0.toByte())
    return priv
}

// Encrypt using publickey
@RequiresApi(Build.VERSION_CODES.O)
@Throws(Exception::class)
fun encryptMessage(plainText: String, publickey: String): String {
    val cipher = Cipher.
    getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
    return Base64.getEncoder().encodeToString(cipher.doFinal

        (plainText.toByteArray()))
}

// Decrypt using privatekey
@RequiresApi(Build.VERSION_CODES.O)
@Throws(Exception::class)
fun decryptMessage(encryptedText: String?, privatekey: String): String {
    val cipher = Cipher.
    getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
    return String(cipher.
    doFinal(Base64.getDecoder().decode(encryptedText)))
}