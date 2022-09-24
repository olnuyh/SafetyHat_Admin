package com.example.admin

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.Response
import com.example.admin.databinding.ActivityWriteCalendarBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class WriteCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarStartBtn.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    binding.calendarStart.text = year.toString()+"-"+(month + 1).toString()+"-"+dayOfMonth.toString()
                }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        binding.calendarEndBtn.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    binding.calendarEnd.text = year.toString()+"-"+(month + 1).toString()+"-"+dayOfMonth.toString()
                }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.calendarRegisterBtn.setOnClickListener {
            val startdate = binding.calendarStart.text.toString()
            val enddate = binding.calendarEnd.text.toString()
            val contents = binding.calendarContents.text.toString()

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
                        val params : MutableMap<String, String> = HashMap()
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

