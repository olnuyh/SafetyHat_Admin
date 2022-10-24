package com.example.admin

import android.R
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.ImageView
import com.example.admin.MyApplication.Companion.socket
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.Socket

var bis : BufferedInputStream? = null

class ReceiverService : Service() {
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()

        val thread = ServerThread()
        thread.start()
    }

    class ServerThread : Thread() {
        override fun run() {
            try {
                socket = Socket("192.168.10.181", 6667)
                bis = BufferedInputStream(socket?.getInputStream())

                while(true){
                    var imageBuffer: ByteArray? = null
                    var size = 0

                    val buffer = ByteArray(10000)

                    var data: Int

                    while((bis!!.read(buffer).also { data = it }) != -1){
                        if(imageBuffer == null){
                            val sizeBuffer = ByteArray(4)
                            System.arraycopy(buffer, 0, sizeBuffer, 0, sizeBuffer.size)
                            size = bytesToInt(buffer)
                            Log.d("rh", "1: " + size.toString())
                            data -= sizeBuffer.size

                            //나머지는 이미지버퍼 배열에 저장한다
                            imageBuffer = ByteArray(data)
                            System.arraycopy(buffer, sizeBuffer.size, imageBuffer, 0, data)
                        }
                        else{
                            //이미지버퍼 배열에 계속 이어서 저장한다
                            val preImageBuffer = imageBuffer.clone()
                            imageBuffer = ByteArray(data + preImageBuffer.size)
                            System.arraycopy(preImageBuffer, 0, imageBuffer, 0, preImageBuffer.size)
                            System.arraycopy(buffer, 0, imageBuffer, imageBuffer.size - data, data)
                        }

                        //이미지버퍼 배열에 총크기만큼 다 받아졌다면 이미지를 저장하고 끝낸다
                        if (imageBuffer.size >= size) {
                            val file = File("/data/data/com.example.admin/files/img1.jpg")

                            if (file.exists()) {
                                file.delete()
                            }

                            val fos = FileOutputStream(file)
                            fos.write(imageBuffer)

                            imageBuffer = null
                            size = 0
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                socket?.close()
            }
        }

        fun bytesToInt(byteArray: ByteArray): Int {
            var result = byteArray[3].toInt() and 0xFF
            result = result or (byteArray[2].toInt() shl 8 and 0xFF00)
            result = result or (byteArray[1].toInt() shl 16 and 0xFF0000)
            result = result or (byteArray[0].toInt() shl 24)

            return result
        }
    }
}

