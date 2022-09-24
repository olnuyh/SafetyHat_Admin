package com.example.admin

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityReadCalendarBinding
import com.example.admin.databinding.ActivityWriteCalendarBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ReadCalendarActivity : AppCompatActivity() {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var binding: ActivityReadCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val date = year.toString()+"-"+(month + 1).toString()+"-"+dayOfMonth.toString()

            // Volley를 이용한 http 통신
            val calendaruploadRequest = object : StringRequest(
                Request.Method.POST,
                BuildConfig.API_KEY + "read_calendar.php",
                Response.Listener<String>{ response ->

                    val jsonObject : JSONObject = JSONObject(response)
                    val array = jsonObject.getJSONArray("response")

                    if(array.length() == 0){
                        binding.scheduleLayout.visibility = View.GONE
                    }
                    else{
                        binding.scheduleLayout.removeAllViews()

                        for (i in 0 until array.length()) {
                            val textView = TextView(this)
                            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams.setMargins(130, 40, 0, 0)
                            textView.layoutParams = layoutParams
                            textView.text = array.getJSONObject(i).getString("calendar_contents")
                            binding.scheduleLayout.addView(textView)


                            val drawable = resources.getDrawable(R.drawable.calendar_point)
                            val imageView= ImageView(this)
                            val layoutParams2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            layoutParams2.setMargins(50, -45, 0, 0)
                            imageView.layoutParams = layoutParams2
                            imageView.setImageDrawable(drawable)
                            binding.scheduleLayout.addView(imageView)
                        }
                        binding.scheduleLayout.visibility = View.VISIBLE
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                }){
                override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                    val params : MutableMap<String, String> = HashMap()
                    params["date"] = date

                    return params
                }
            }

            val queue = Volley.newRequestQueue(this)
            queue.add(calendaruploadRequest)
        }


        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_write_calendar)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)

        val calendarStartBtn = dialog.findViewById<ImageButton>(R.id.calendarStartBtn)
        val calendarStart = dialog.findViewById<TextView>(R.id.calendarStart)
        val calendarEndBtn = dialog.findViewById<ImageButton>(R.id.calendarEndBtn)
        val calendarEnd = dialog.findViewById<TextView>(R.id.calendarEnd)
        val calendarContents = dialog.findViewById<EditText>(R.id.calendarContents)
        val calendarRegisterBtn = dialog.findViewById<ImageButton>(R.id.calendarRegisterBtn)


        binding.calendarRegisterBtn.setOnClickListener {
//            finish()
//            startActivity(Intent(this,WriteCalendarActivity::class.java))
            dialog.show()

            calendarStartBtn.setOnClickListener {
                val cal = Calendar.getInstance()    //캘린더뷰 만들기
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        calendarStart.text = year.toString()+"-"+(month + 1).toString()+"-"+dayOfMonth.toString()
                    }
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

           calendarEndBtn.setOnClickListener {
                val cal = Calendar.getInstance()    //캘린더뷰 만들기
                val dateSetListener =
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        calendarEnd.text = year.toString()+"-"+(month + 1).toString()+"-"+dayOfMonth.toString()
                    }
                DatePickerDialog(
                    this,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            calendarRegisterBtn.setOnClickListener {
                val startdate = calendarStart.text.toString()
                val enddate = calendarEnd.text.toString()
                val contents = calendarContents.text.toString()

                val startdate_date = SimpleDateFormat("yyyy-MM-dd").parse(startdate)
                val enddate_date = SimpleDateFormat("yyyy-MM-dd").parse(enddate)
                if(enddate_date.before(startdate_date)){ // 끝 날짜가 시작 날짜보다 빠른 경우
                    Toast.makeText(this, "선택 날짜가 올바르지 않습니다.", Toast.LENGTH_LONG).show()
                }
                else if(contents.equals("")){
                    Toast.makeText(this, "내용을 입력해주세요.", Toast.LENGTH_LONG).show()
                }
                else{
                    // Volley를 이용한 http 통신
                    val writecalendarRequest = object : StringRequest(
                        Request.Method.POST,
                        BuildConfig.API_KEY+"write_calendar.php",
                        Response.Listener<String>{ response ->
                            if(response.toString().equals("1")) { // 성공
                                Toast.makeText(this, "일정이 등록되었습니다.", Toast.LENGTH_LONG).show()
                                finish()
                                val intent = Intent(this, ReadCalendarActivity::class.java)
                                startActivity(intent)
                            }
                        },
                        Response.ErrorListener { error ->
                            Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                        }){
                        override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                            val params : MutableMap<String, String> = java.util.HashMap()
                            params["startdate"] = startdate
                            params["enddate"] = enddate
                            params["contents"] = contents
                            params["writer"] = MyApplication.prefs.getString("admin_pkey", "")
                            return params
                        }
                    }

                    val queue = Volley.newRequestQueue(this)
                    queue.add(writecalendarRequest)
                }
            }

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true

        return when (item.itemId) {
            R.id.action_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.home,menu)
        return true
    }

}