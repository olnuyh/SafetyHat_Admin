package com.example.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.databinding.ActivityReadCalendarBinding
import com.example.admin.databinding.ActivityWriteCalendarBinding


class ReadCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarRegisterBtn.setOnClickListener {
            startActivity(Intent(this,WriteCalendarActivity::class.java))
        }
    }
}