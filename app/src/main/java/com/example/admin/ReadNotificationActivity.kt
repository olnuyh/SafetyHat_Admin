package com.example.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityReadNotificationBinding
import org.json.JSONArray

class ReadNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityReadNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val readNotificationRequest = JsonArrayRequest( // Volley를 이용한 http 통신
            Request.Method.GET,
            BuildConfig.API_KEY + "read_notification.php",
            null,
            Response.Listener<JSONArray> { response ->
                binding.notificationRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.notificationRecyclerView.adapter = NotificationAdapter(this, response)
                binding.notificationRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            }
        )

        val queue = Volley.newRequestQueue(this)
        queue.add(readNotificationRequest)

        binding.goToRegisterNotificationBtn.setOnClickListener {
            finish()
            val intent = Intent(this, WriteNotificationActivity::class.java)
            startActivity(intent)
        }
    }
}