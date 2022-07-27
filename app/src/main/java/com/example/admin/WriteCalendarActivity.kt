package com.example.admin

import android.app.DatePickerDialog
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
import java.util.*


class WriteCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityWriteCalendarBinding

    var dateString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.calendarStartBtn.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateString = "${year}-${month + 1}-${dayOfMonth}"
                    binding.calendarStartTv.text = dateString
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
                    dateString = "${year}-${month + 1}-${dayOfMonth}"
                    binding.calendarEndTv.text = dateString
                }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.calendarOkBtn.setOnClickListener {
            val startdate = binding.calendarStartTv.toString()
            val enddate = binding.calendarEndTv.toString()
            val contents = binding.calendarContentEt.text.toString()

            // Volley를 이용한 http 통신
            val calendaruploadRequest = object : StringRequest(
                Request.Method.POST,
                BuildConfig.API_KEY+"calendar_upload.php",
                Response.Listener<String>{ response ->
//                    if(response.toString().equals("1")){ // 성공
//                        Toast.makeText(this, "성공.", Toast.LENGTH_LONG).show()
//                    }
                    Log.d("reponse:", response)
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

