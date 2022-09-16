package com.example.admin

import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityCctvBinding
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException


class CctvActivity : AppCompatActivity() {
    lateinit var binding: ActivityCctvBinding

    private var s: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCctvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connect: Connect = Connect()
        connect.execute(CONNECT_MSG)

        binding.cctvBtn.setOnClickListener(View.OnClickListener {

        })

        val openfile = File("$filesDir/img0.png")
        val op = BitmapFactory.Options()
        val Bm = BitmapFactory.decodeFile(openfile.absolutePath, op)
        binding.getImage.setImageBitmap(Bm)


    }

    private inner class Connect : AsyncTask<String?, String?, Void?>() {

        override fun doInBackground(vararg p0: String?): Void? {
            try {
                s= Socket(SERVER_IP, 6667)
                saveFile(s!!)

                finish() //인텐트 종료
                overridePendingTransition(0, 0) //인텐트 효과 없애기
                val intent = intent //인텐트
                startActivity(intent) //액티비티 열기
                overridePendingTransition(0, 0) //인텐트 효과 없애기


            } catch (e: UnknownHostException) {
                val str = e.message.toString()
                Log.w("discnt", "$str 1")
            } catch (e: IOException) {
                val str = e.message.toString()
                Log.w("discnt", "$str 2")
            }
            return null
        }
    }


    @Throws(IOException::class)
    private fun saveFile(clientSock: Socket) {
        val `in` = clientSock.getInputStream()
        //바이트 단위로 데이터를 읽는다, 외부로 부터 읽어들이는 역할을 담당
        val bis = BufferedInputStream(`in`)

        val file = File("/data/data/com.example.admin/files/img0.png")

        val fos = FileOutputStream(file)
        //파일을 열어서 어떤식으로 저장할지 알려준다. FileOutputStream을 쓰면 들어오는 파일과 일치하게 파일을 작성해줄 수 있는 장점이 있다.
        var ch: Int
        while (bis.read().also { ch = it } != -1) {
            fos.write(ch)
            //열린 파일시스템에 BufferedInputStream으로 외부로 부터 읽어들여온 파일을 FileOutputStream에 바로 써준다.
        }
        fos.close()
        `in`.close()
    }

    companion object {
        private const val SERVER_IP = ""
        private const val CONNECT_MSG = "connect"
        private const val STOP_MSG = "stop"
        private const val BUF_SIZE = 100
    }

}


