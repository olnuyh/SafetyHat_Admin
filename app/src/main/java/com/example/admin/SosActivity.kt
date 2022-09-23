package com.example.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.databinding.ActivitySosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


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

                var readCheckMap: MutableMap<String, Any> = HashMap()

                for(msg in snapshot.children){
                    val key = msg.key
                    val message_before = msg.getValue(SosMessage::class.java)
                    val message_after = msg.getValue(SosMessage::class.java)
                    message_after!!.isRead = true

                    readCheckMap.put(key!!, message_after!!)

                    messageList.add(message_before!!)
                }

                if(!messageList.get(messageList.size - 1).isRead){
                    ref.updateChildren(readCheckMap).addOnCompleteListener {
                        adapter.notifyDataSetChanged()
                        binding.sosRecyclerView.scrollToPosition(messageList.size - 1)
                    }
                }else{
                    adapter.notifyDataSetChanged()
                    binding.sosRecyclerView.scrollToPosition(messageList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        adapter = SosAdapter(this, messageList)
        binding.sosRecyclerView.adapter = adapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}