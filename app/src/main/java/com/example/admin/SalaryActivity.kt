package com.example.admin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonArrayRequest
import com.example.admin.databinding.ActivitySalaryBinding
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class SalaryActivity : AppCompatActivity() {
    lateinit var binding: ActivitySalaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val salaryRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + ".workers_salary.php",
            null,
            Response.Listener<JSONArray> { response ->
                binding.salaryRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.salaryRecyclerView.adapter = SalaryAdapter(this, response)
                binding.salaryRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(salaryRequest)
    }
}