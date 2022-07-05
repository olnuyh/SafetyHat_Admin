package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.admin.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val TAG = "MainActivity.class"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getFCMToken()

        binding.mainAreaBtn.setOnClickListener {
            startActivity(Intent(this,AreaActivity::class.java))
        }

        binding.mainCalendarBtn.setOnClickListener {
            startActivity(Intent(this,CalendarActivity::class.java))
        }

        binding.mainCctvBtn.setOnClickListener {
            startActivity(Intent(this,CctvActivity::class.java))
        }

        binding.mainSalaryBtn.setOnClickListener {
            startActivity(Intent(this,SalaryActivity::class.java))
        }

        binding.mainNotificationBtn.setOnClickListener {
            startActivity(Intent(this,NotificationActivity::class.java))
        }
    }

    private fun getFCMToken(): String?{
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            Log.d(TAG, "FCM Token is ${token}")
        })

        return token
    }
}