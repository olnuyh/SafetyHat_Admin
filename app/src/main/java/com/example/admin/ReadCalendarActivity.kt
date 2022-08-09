package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityReadCalendarBinding
import com.example.admin.databinding.ActivityWriteCalendarBinding
import org.json.JSONObject


class ReadCalendarActivity : AppCompatActivity() {
    lateinit var binding: ActivityReadCalendarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                            layoutParams.setMargins(100, 40, 0, 0)
                            textView.layoutParams = layoutParams
                            textView.text = array.getJSONObject(i).getString("calendar_contents")
                            binding.scheduleLayout.addView(textView)
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


        binding.calendarRegisterBtn.setOnClickListener {
            finish()
            startActivity(Intent(this,WriteCalendarActivity::class.java))
        }

    }
}