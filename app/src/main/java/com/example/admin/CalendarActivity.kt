package com.example.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityCalendarBinding


class CalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.calendarOkBtn.setOnClickListener {
            val startdate = binding.calendarStartdate.text.toString()// 사용자가 입력한 작업
            val enddate = binding.calendarEnddate.toString()
            val contents = binding.calendarContents.text.toString()

            // Volley를 이용한 http 통신
            val calendaruploadRequest = object : StringRequest(
                Request.Method.POST,
                "http://172.20.10.4/calendar_upload.php",
                Response.Listener<String>{ response ->
                    if(response.toInt() == 1){ // 성공
                        Toast.makeText(this, "성공.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                }){
                override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                    val params : MutableMap<String, String> = HashMap()
                    params["startdate"] = startdate
                    params["enddate"] = enddate
                    params["contents"] = contents
                    return params
                }
            }

            val queue = Volley.newRequestQueue(this)
            queue.add(calendaruploadRequest)


        }
    }
}