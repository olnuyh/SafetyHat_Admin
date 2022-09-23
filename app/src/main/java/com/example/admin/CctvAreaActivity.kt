package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.admin.databinding.ActivityCctvAreaBinding
import com.example.admin.databinding.ActivityCctvBinding
import com.example.admin.databinding.ActivityLoginBinding
import com.example.admin.databinding.ActivityMainBinding

class CctvAreaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCctvAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aArea.setOnClickListener{
            val intent = Intent(this, CctvActivity::class.java)
            startActivity(intent)
        }



    }
}