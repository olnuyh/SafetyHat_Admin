package com.example.admin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemAreaBinding
import org.json.JSONArray
import org.json.JSONObject

class AreaViewHolder(val binding : ItemAreaBinding) : RecyclerView.ViewHolder(binding.root)
class AreaAdapter (val context : Context, val arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return arr.length() ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AreaViewHolder(
            ItemAreaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AreaViewHolder).binding

        val workers = arr[position] as JSONObject
        binding.areaItemEmplId.text = workers.getString("id")
        binding.areaItemName.text = workers.getString("name")

        val status = workers.getString("status").toInt()

        if (status == 0) {
            binding.areaItemAttendance.text = "미출근"
            binding.areaItemAttendance.setTextColor(Color.BLACK)
        } else if (status == 1) {
            binding.areaItemAttendance.text = "근무중"
            binding.areaItemAttendance.setTextColor(Color.GREEN)
        }

        if(workers.getString("work").toInt() == 1){ // 이미 근무 일정이 있는 근무자는 선택할 수 없게 하기
            binding.areaItemCheck.alpha = 0.4f
            binding.areaItemCheck.isEnabled = false
        }
        else{
            binding.areaItemCheck.alpha = 1.0f
            binding.areaItemCheck.isEnabled = true
        }

        if(position >= MyApplication.workerList.size)
            MyApplication.workerList.add(position, WorkerCheckStatus(workers.getString("id"), false))

        binding.areaItemCheck.isChecked = MyApplication.workerList[position].isChecked

        binding.areaItemCheck.setOnClickListener{
            MyApplication.workerList[position].isChecked = binding.areaItemCheck.isChecked
        }
    }
}
