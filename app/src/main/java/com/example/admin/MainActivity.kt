package com.example.admin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.admin.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var binding: ActivityMainBinding
    val database = Firebase.database("https://safetyhat-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val ref = database.getReference("SosMessages")
    val messageList = ArrayList<SosMessage>()
    lateinit var adapter : TodaySosAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 소켓 서비스 실행
        val intent = Intent(this, ReceiverService::class.java)
        baseContext.startService(intent)

        setSupportActionBar(binding.toolBar)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toggle.syncState()


        binding.mainDrawerView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.menuSos -> {
                    val intent = Intent(this, SosActivity::class.java)
                    startActivity(intent)
                }
                R.id.menuCctv -> {
                    val intent = Intent(this, CctvAreaActivity::class.java)
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
            }
            true
        }

        binding.logout.setOnClickListener {
            MyApplication.prefs.clear()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.sosNumber.setOnClickListener {
            val intent = Intent(this, SosActivity::class.java)
            startActivity(intent)
        }

        val readAreaRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/read_area_list.php",
            null,
            Response.Listener<JSONArray> { response ->
                MyApplication.areaList.clear()
                MyApplication.areaList.add("-")

                for (i in 0 until response.length()){
                    val obj = response[i] as JSONObject
                    val name = obj.getString("area_name") + "구역"
                    MyApplication.areaList.add(name)

                    val areaCctvBtn = Button(this)
                    val width = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        120f,
                        resources.displayMetrics
                    ).toInt()
                    val height = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        70f,
                        resources.displayMetrics
                    ).toInt()

                    val layoutParams = LinearLayout.LayoutParams(width, height)
                    layoutParams.setMargins(0, 0, 45, 0)
                    areaCctvBtn.layoutParams = layoutParams
                    areaCctvBtn.setBackgroundResource(R.drawable.area_btn)
                    areaCctvBtn.text = name
                    areaCctvBtn.setTextAppearance(R.style.area_btn_text)
                    areaCctvBtn.setOnClickListener {
                        val intent = Intent(this, CctvActivity::class.java)
                        intent.putExtra("name", name)
                        startActivity(intent)
                    }
                    binding.areaLayout.addView(areaCctvBtn)
                }

                MyApplication.areaList.add("+ 추가")
                MyApplication.areaList.add("구역")
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
            Request.Method.POST, "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/admin_name.php",
            Response.Listener<String>{ response ->
                if(response.toString().equals("-1")){
                    Toast.makeText(this, "이름 가져오기 실패", Toast.LENGTH_LONG).show()
                }
                else {
                    MyApplication.prefs.setString("admin_name", response)
                    val navName : TextView = headerView.findViewById(R.id.navigationName)
                    navName.text=MyApplication.prefs.getString("admin_name", "") + " 관리자"
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

        headerView.findViewById<ImageButton>(R.id.navigationCancel).setOnClickListener {
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
        }

        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)

        ref.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for(msg in snapshot.children){
                    val message = msg.getValue(SosMessage::class.java)

                    val today = LocalDateTime.now()
                    val today_date = today.format(DateTimeFormatter.ISO_DATE)

                    val timeStamp = SimpleDateFormat("yyyy-MM-dd").format(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(message!!.timeStamp))

                    if(timeStamp.equals(today_date) && !message!!.isRead){
                        messageList.add(message!!)
                    }
                }

                adapter.notifyDataSetChanged()
                binding.recentMessageNum.text = messageList.size.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        adapter = TodaySosAdapter(this, messageList)
        binding.mainRecyclerView.adapter = adapter

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

        binding.mainWorker.setOnClickListener {
            val intent = Intent(this, AreaActivity::class.java)
            startActivity(intent)
        }

        binding.mainSalary.setOnClickListener {
            val intent = Intent(this, SalaryActivity::class.java)
            startActivity(intent)
        }

        binding.mainCalendar.setOnClickListener {
            val intent = Intent(this, ReadCalendarActivity::class.java)
            startActivity(intent)
        }

        binding.mainNotification.setOnClickListener {
            val intent = Intent(this, ReadNotificationActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, ReceiverService::class.java)
        baseContext.stopService(intent)

        MyApplication.socket = null
        Log.d("mobileApp", MyApplication.socket.toString())
    }
}