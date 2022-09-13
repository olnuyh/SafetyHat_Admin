package com.example.admin

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        binding.mainDrawerView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menuSos -> {

                }
                R.id.menuCctv -> {
                    val intent = Intent(this, CctvActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuArea -> {
                    val intent = Intent(this, AreaActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuCalendar -> {
                    val intent = Intent(this, ReadCalendarActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuNotification -> {
                    val intent = Intent(this, ReadNotificationActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuSalary -> {
                    val intent = Intent(this, SalaryActivity::class.java)
                    startActivity(intent)
                }
                R.id.logout -> {
                    MyApplication.prefs.clear()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

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

        val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_area_list.php",
            null,
            Response.Listener<JSONArray> { response ->
                MyApplication.areaList.add("-")

                for (i in 0 until response.length()){
                    val obj = response[i] as JSONObject
                    val name = obj.getString("area_name")
                    MyApplication.areaList.add(name)
                }

                MyApplication.areaList.add("+ 추가")
                MyApplication.areaList.add("구역을 선택하세요.")
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue2 = Volley.newRequestQueue(this)
        queue2.add(readAreaRequest)

        val navView = binding.mainDrawerView
        val headerView = navView.getHeaderView(0)

        // 관리자 이름 가져오기
        val mainRequest = object : StringRequest(
            Request.Method.POST,
            BuildConfig.API_KEY + "admin_name.php",
            Response.Listener<String>{ response ->
                if(response.toString().equals("-1")){
                    Toast.makeText(this, "이름 가져오기 실패", Toast.LENGTH_LONG).show()
                }
                else {
                    val navName : TextView = headerView.findViewById(R.id.navigationName)
                    navName.text=response.toString()
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                val params : MutableMap<String, String> = HashMap()
                params["pkey"] = MyApplication.prefs.getString("admin_pkey", "")

                return params
            }
        }
        val queue3 = Volley.newRequestQueue(this)
        queue3.add(mainRequest)

        //날씨
        val weatherRequest= object : StringRequest(
            Method.GET,  BuildConfig.WEATHER_API_KEY,
            Response.Listener<String>{ response ->
                val jsonObject = JSONObject(response)
                val city = jsonObject.getString("name")
                //binding.cityView.text=city

                val weatherJson = jsonObject.getJSONArray("weather")
                val weatherObj = weatherJson.getJSONObject(0)
                val weather = weatherObj.getString("description")

                val icon = weatherObj.getString("icon")

                val imageView = findViewById<ImageView>(R.id.weathericon)
                val imageUrl01d = "http://openweathermap.org/img/wn/01d@2x.png"
                val imageUrl02d = "http://openweathermap.org/img/wn/02d@2x.png"
                val imageUrl03d = "http://openweathermap.org/img/wn/03d@2x.png"
                val imageUrl04d = "http://openweathermap.org/img/wn/04d@2x.png"
                val imageUrl09d = "http://openweathermap.org/img/wn/09d@2x.png"
                val imageUrl10d = "http://openweathermap.org/img/wn/10d@2x.png"
                val imageUrl11d = "http://openweathermap.org/img/wn/11d@2x.png"
                val imageUrl13d = "http://openweathermap.org/img/wn/13d@2x.png"
                val imageUrl50d = "http://openweathermap.org/img/wn/50d@2x.png"
                val imageUrl01n = "http://openweathermap.org/img/wn/01n@2x.png"
                val imageUrl02n = "http://openweathermap.org/img/wn/02n@2x.png"
                val imageUrl03n = "http://openweathermap.org/img/wn/03n@2x.png"
                val imageUrl04n = "http://openweathermap.org/img/wn/04n@2x.png"
                val imageUrl09n = "http://openweathermap.org/img/wn/09n@2x.png"
                val imageUrl10n = "http://openweathermap.org/img/wn/10n@2x.png"
                val imageUrl11n = "http://openweathermap.org/img/wn/11n@2x.png"
                val imageUrl13n = "http://openweathermap.org/img/wn/13n@2x.png"
                val imageUrl50n = "http://openweathermap.org/img/wn/50n@2x.png"


                when (icon) {
                    "01d" -> Glide.with(this).load(imageUrl01d).into(imageView)
                    "02d" -> Glide.with(this).load(imageUrl02d).into(imageView)
                    "03d" -> Glide.with(this).load(imageUrl03d).into(imageView)
                    "04d" -> Glide.with(this).load(imageUrl04d).into(imageView)
                    "09d" -> Glide.with(this).load(imageUrl09d).into(imageView)
                    "10d" -> Glide.with(this).load(imageUrl10d).into(imageView)
                    "11d" -> Glide.with(this).load(imageUrl11d).into(imageView)
                    "13d" -> Glide.with(this).load(imageUrl13d).into(imageView)
                    "50d" -> Glide.with(this).load(imageUrl50d).into(imageView)
                    "01n" -> Glide.with(this).load(imageUrl01n).into(imageView)
                    "02n" -> Glide.with(this).load(imageUrl02n).into(imageView)
                    "03n" -> Glide.with(this).load(imageUrl03n).into(imageView)
                    "04n" -> Glide.with(this).load(imageUrl04n).into(imageView)
                    "09n" -> Glide.with(this).load(imageUrl09n).into(imageView)
                    "10n" -> Glide.with(this).load(imageUrl10n).into(imageView)
                    "11n" -> Glide.with(this).load(imageUrl11n).into(imageView)
                    "13n" -> Glide.with(this).load(imageUrl13n).into(imageView)
                    "50n" -> Glide.with(this).load(imageUrl50n).into(imageView)
                    else -> println("null")
                }


                val tempK = JSONObject(jsonObject.getString("main"))
                val tempDo = Math.round((tempK.getDouble("temp") - 273.15) * 100) / 100.0
                binding.tempView.text="$tempDo°C"


            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }){
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                return HashMap()
            }
        }

        val queue4 = Volley.newRequestQueue(this)
        queue4.add(weatherRequest)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }


//     getFCMToken()
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