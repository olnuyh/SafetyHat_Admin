package com.example.admin

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.MyApplication.Companion.socket
import com.example.admin.databinding.ActivityCctvBinding
import java.io.DataOutputStream
import java.io.File
import java.util.Date

class CctvActivity : AppCompatActivity() {
    lateinit var binding: ActivityCctvBinding
    var lastUpdated : Date ?= null
    val file = File("/data/data/com.example.admin/files/img1.jpg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCctvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(file.exists()){
            imageChange()
        }

        binding.cctvBtn.setOnClickListener(View.OnClickListener {
            Thread{
                val dataOutput = DataOutputStream(socket!!.getOutputStream())
                dataOutput.writeUTF("connect")
            }.start()
        })

        Thread{
            while (true){
                if(file.exists()){
                    if(lastUpdated != Date(file.lastModified())){
                        break
                    }
                }
            }

            finish()
            overridePendingTransition(0, 0)
            val intent = intent
            startActivity(intent)
            overridePendingTransition(0, 0)
        }.start()
    }

    private fun imageChange(){
        val op = BitmapFactory.Options()
        val bm = BitmapFactory.decodeFile(file.absolutePath, op)
        binding.getImage.setImageBitmap(bm)

        lastUpdated = Date(file.lastModified())
    }
}