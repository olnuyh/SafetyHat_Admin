package com.example.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityCctvBinding

class CctvActivity : AppCompatActivity() {
    lateinit var binding: ActivityCctvBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCctvBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}