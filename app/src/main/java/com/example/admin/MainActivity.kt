package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.admin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //val TAG = "MainActivity.class"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //getFCMToken()

        binding.mainListBtn.setOnClickListener {
            startActivity(Intent(this,AreaActivity::class.java))
        }

        binding.mainCalendarBtn.setOnClickListener {
            startActivity(Intent(this,WriteCalendarActivity::class.java))
        }

        binding.mainCctvBtn.setOnClickListener {
            startActivity(Intent(this,CctvActivity::class.java))
        }

        binding.mainSalaryBtn.setOnClickListener {
            startActivity(Intent(this,SalaryActivity::class.java))
        }

        binding.mainNotificationBtn.setOnClickListener {
            startActivity(Intent(this,ReadNotificationActivity::class.java))
        }

        binding.logoutBtn.setOnClickListener {
            MyApplication.prefs.clear()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    private fun getFCMToken(): String?{
//        var token: String? = null
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            token = task.result
//
//            // Log and toast
//            Log.d(TAG, "FCM Token is ${token}")
//        })
//
//        return token
//    }
}