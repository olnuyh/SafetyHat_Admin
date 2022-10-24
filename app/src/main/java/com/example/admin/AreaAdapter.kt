package com.example.admin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemAreaBinding
import org.json.JSONArray
import org.json.JSONObject


class AreaViewHolder(val binding : ItemAreaBinding) : RecyclerView.ViewHolder(binding.root)
class AreaAdapter (val context : Context, val arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var workerList = JSONArray()

    init {
        workerList = arr
    }

    override fun getItemCount(): Int {
        return workerList.length() ?: 0
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

        val workers = workerList[position] as JSONObject
        binding.areaItemEmplId.text = workers.getString("id")
        binding.areaItemName.text = workers.getString("name")

        val status = workers.getString("status").toInt()

        val area = workers.getString("area")

        if(status == 0){
            binding.areaItemAttendance.text = "미출근"
            binding.areaItemAttendance.setTextColor(Color.parseColor("#939393"))
        }
        else if(status == 1){
            binding.areaItemAttendance.text = "근무중"
            binding.areaItemAttendance.setTextColor(Color.parseColor("#019839"))
        }
        else if(status == 2){
            binding.areaItemAttendance.text = "퇴근"
            binding.areaItemAttendance.setTextColor(Color.BLACK)
        }
        else if(status == 3){
            binding.areaItemAttendance.text = "근무중"
            binding.areaItemAttendance.setTextColor(Color.parseColor("#F58D34"))
        }
        else if(status == 4){
            binding.areaItemAttendance.text = "결근"
            binding.areaItemAttendance.setTextColor(Color.parseColor("#E44747"))
        }

        if(area.equals("0")){
            binding.areaItemArea.text = "-"
        }
        else{
            binding.areaItemArea.text = area
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

    fun filter(searchText: String) {
        workerList = JSONArray()
        if (searchText.length == 0) {
            workerList = arr
        } else {
            try {
                val searchEmplId = searchText.toInt()
                for (i in 0 until arr.length()) {
                    val worker = arr.getJSONObject(i)
                    val id : String = worker.getString("id")
                    if (id.contains(searchEmplId.toString())) {
                        workerList.put(worker)
                    }
                }
            } catch (e: NumberFormatException) {
                for (i in 0 until arr.length()) {
                    val worker = arr.getJSONObject(i)
                    val name: String = worker.getString("name")
                    if (name.contains(searchText)) {
                        workerList.put(worker)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }
}

