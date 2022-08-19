package com.example.admin

import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityCctvBinding
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException


class CctvActivity : AppCompatActivity() {
    lateinit var binding: ActivityCctvBinding

    private var client: Socket? = null
    private var dataOutput: DataOutputStream? = null
    private var dataInput: DataInputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCctvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cctvBtn.setOnClickListener(View.OnClickListener {
            val connect: Connect = Connect()
            connect.execute(CONNECT_MSG)
        })
    }

    private inner class Connect : AsyncTask<String?, String?, Void?>() {
        private var output_message: String? = null
        private val input_message: String? = null

        override fun doInBackground(vararg p0: String?): Void? {
            try {
                client = Socket(SERVER_IP, 6699)
                dataOutput = DataOutputStream(client!!.getOutputStream())
                dataInput = DataInputStream(client!!.getInputStream())
                output_message = strings[0]
                dataOutput!!.writeUTF(output_message)
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

    companion object {
        private const val SERVER_IP = "172.20.10.2"
        private const val CONNECT_MSG = "connect"
        private const val STOP_MSG = "stop"
        private const val BUF_SIZE = 100
    }
}

