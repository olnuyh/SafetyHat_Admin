package com.example.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityCalendarBinding

class CalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}