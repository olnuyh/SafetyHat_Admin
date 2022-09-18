package com.example.admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.databinding.ActivitySosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList


class SosActivity : AppCompatActivity(){
    lateinit var binding : ActivitySosBinding
    val database = Firebase.database("https://safetyhat-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val ref = database.getReference("SosMessages")
    val messageList = ArrayList<SosMessage>()
    lateinit var adapter : SosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sosRecyclerView.layoutManager = LinearLayoutManager(this)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for(msg in snapshot.children){
                    val message = msg.getValue(SosMessage::class.java)
                    messageList.add(message!!)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        adapter = SosAdapter(this, messageList)
        binding.sosRecyclerView.adapter = adapter
    }
}