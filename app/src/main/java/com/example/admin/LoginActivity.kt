package com.example.admin


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.admin.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException

import org.json.JSONObject




class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getFCMToken()

        val auto_loginPkey = MyApplication.prefs.getString("admin_pkey", "")


        if(!auto_loginPkey.isEmpty()){ // 자동 로그인
            Toast.makeText(this, "자동 로그인 성공하였습니다.", Toast.LENGTH_LONG).show()
            finish()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        else{

            binding.loginBtn.setOnClickListener {
                val integrator = IntentIntegrator(this)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // 여러가지 바코드중에 특정 바코드 설정 가능
                integrator.setPrompt("QR 코드를 스캔해주세요") // 스캔할 때 하단의 문구
                integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
                integrator.setBeepEnabled(true) // 바코드를 인식했을 때 삑 소리유무
                integrator.setBarcodeImageEnabled(false) // 스캔 했을 때 스캔한 이미지 사용여부
                integrator.initiateScan() // 스캔
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // QR 코드를 찍은 결과를 변수에 담는다.
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) { // 뒤로가기 선택 시
                Toast.makeText(this, "QR코드 인증이 취소되었습니다.", Toast.LENGTH_LONG).show()
            }
            else { // QR코드가 스캔된 경우
                val admin_result = result.contents.split(" ")

                // Volley를 이용한 http 통신
                val loginRequest = object : StringRequest(
                    Request.Method.POST,
                    "http://ec2-15-165-242-180.ap-northeast-2.compute.amazonaws.com/admin_login.php",
                    Response.Listener<String>{ response ->
                        if(response.toString().equals("-1")){ // 로그인 실패
                            Toast.makeText(this, "등록된 관리자 QR코드가 아닙니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
                        }
                        else{ // 로그인 성공
                            MyApplication.prefs.setString("admin_pkey", response.toString())
                            Toast.makeText(this, "로그인 성공하였습니다.", Toast.LENGTH_LONG).show()
                            finish()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                    },
                    Response.ErrorListener { error ->
                        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                    }){
                    override fun getParams(): MutableMap<String, String>? { // API로 전달할 데이터
                        val params : MutableMap<String, String> = HashMap()
                        params["id"] = admin_result[0]
                        params["name"] = admin_result[1]
                        return params
                    }
                }
                val queue = Volley.newRequestQueue(this)
                queue.add(loginRequest)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getFCMToken(): String?{
        var token: String? = null
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

            // Log and toast
            Log.d("mobileApp", "FCM Token is ${token}")
        })

        return token
    }
}