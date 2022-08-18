package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityMainBinding
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //val TAG = "MainActivity.class"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val readWorkersRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_workers.php",
            null,
            Response.Listener<JSONArray> { response ->
                MyApplication.workers = response
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(readWorkersRequest)

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