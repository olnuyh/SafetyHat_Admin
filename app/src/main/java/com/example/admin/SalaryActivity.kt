package com.example.admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.example.admin.databinding.ActivitySalaryBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class SalaryActivity : AppCompatActivity() {
    lateinit var binding: ActivitySalaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val month = SimpleDateFormat("M", Locale.KOREA).format(Date())
        requestSalary(month)
        binding.salaryRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        var month_number = month.toInt()

        binding.salaryTitle.text = "근무자 " + month + "월 급여"

        binding.titleLeftbtn.setOnClickListener {
            month_number = month_number - 1
            if(month_number == 0){
                month_number = 12
            }
            requestSalary(month_number.toString())
            binding.salaryTitle.text = "근무자 " + month_number.toString()+ "월 급여"
        }

        binding.titleRightbtn.setOnClickListener {
            month_number = month_number + 1
            if(month_number == 13){
                month_number = 1
            }
            requestSalary(month_number.toString())
            binding.salaryTitle.text = "근무자 " + month_number.toString() + "월 급여"
        }
    }

    fun requestSalary(month : String){
        val salaryRequest = object : StringRequest( // Volley를 이용한 http 통신
            Request.Method.POST,
            BuildConfig.API_KEY + "workers_salary.php",
            Response.Listener<String> { response ->
                val jsonObject : JSONObject = JSONObject(response)
                val array = jsonObject.getJSONArray("response")

                binding.salaryRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.salaryRecyclerView.adapter = SalaryAdapter(this, array)

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                val params : MutableMap<String, String> = HashMap()
                params["month"] = month
                return params
            }
        }

        val queue = Volley.newRequestQueue(this)
        queue.add(salaryRequest)
    }
}