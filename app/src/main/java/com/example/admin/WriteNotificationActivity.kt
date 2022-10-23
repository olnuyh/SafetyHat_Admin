package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityWriteNotificationBinding

class WriteNotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityWriteNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerNotificationBtn.setOnClickListener {
            val notificationTitle = binding.writeNotificationTitle.text.toString() // 관리자가 입력한 공지 제목
            val notificationContents = binding.writeNotificationContents.text.toString() // 관리자가 입력한 공지 내용

            if(notificationTitle.equals("")) { // 제목을 입력하지 않은 경우
                Toast.makeText(this, "제목을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            else if(notificationContents.equals("")){ // 내용을 입력하지 않은 경우
                Toast.makeText(this, "내용을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            else { // 모든 정보 입력 시
                val registerNotificationRequest = object : StringRequest( // Volley를 이용한 http 통신
                    Request.Method.POST,
                     "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/write_notification.php",
                    Response.Listener<String> { response ->
                        if (response.toString().equals("1")) { // 공지사항 등록 성공
                            Toast.makeText(this, "공지사항이 등록되었습니다.", Toast.LENGTH_LONG).show()
                            finish()
                            val intent = Intent(this, ReadNotificationActivity::class.java)
                            startActivity(intent)
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                    }) {
                    override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                        val params: MutableMap<String, String> = HashMap()
                        params["title"] = notificationTitle
                        params["contents"] = notificationContents
                        params["writer"] = MyApplication.prefs.getString("admin_pkey", "")
                        return params
                    }
                }
                val queue = Volley.newRequestQueue(this)
                queue.add(registerNotificationRequest)
            }
        }
    }
}