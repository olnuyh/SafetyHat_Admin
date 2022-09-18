package com.example.admin

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivitySosBinding


class SosActivity : AppCompatActivity(){
    lateinit var binding: ActivitySosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}