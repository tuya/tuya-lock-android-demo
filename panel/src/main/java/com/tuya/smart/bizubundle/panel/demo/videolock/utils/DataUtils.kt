package com.tuya.smart.bizubundle.panel.demo.videolock.utils

import android.net.Uri
import android.text.TextUtils
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Create by blitzfeng on 5/19/22
 */
object DataUtils {

    @Throws(
        IOException::class,
        NoSuchPaddingException::class,
        NoSuchAlgorithmException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class
    )
    fun decryptStream(body:String,key :ByteArray,algorithm:String,fullAlgorithm:String): InputStream? {
        val inputStream = body.byteInputStream()
        val buffer = ByteArray(16)
        inputStream.skip(4L)
        val read = inputStream.read(buffer)
        return if (read != 16) {
            throw IOException("iv length error")
        } else {
            inputStream.skip(44L)
            val keySpec =
                SecretKeySpec(key, algorithm)
            val cipher = Cipher.getInstance(fullAlgorithm)
            cipher.init(2, keySpec, IvParameterSpec(buffer))
//            return cipher.doFinal(body.toByteArray())
            CipherInputStream(inputStream, cipher)
        }
    }

    fun decryptUrl(fileUrl:String?,key:String?):String?{
        if(TextUtils.isEmpty(fileUrl)|| TextUtils.isEmpty(key))
            return null
        val byteArray = decryptStream(Uri.parse(fileUrl!!).toString(),key!!.toByteArray(),"AES","AES/CBC/PKCS5Padding")
            ?: return null
        return String(byteArray.readBytes())
    }
}