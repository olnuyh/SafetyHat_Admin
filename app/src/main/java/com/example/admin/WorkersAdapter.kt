package com.example.admin

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemWorkersBinding
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class WorkersViewHolder(val binding : ItemWorkersBinding) : RecyclerView.ViewHolder(binding.root)
class WorkersAdapter(val context : Context, var arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    var workerList = JSONArray()

    init {
        workerList = arr
    }

    override fun getItemCount(): Int {
        return workerList.length()?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WorkersViewHolder(ItemWorkersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as WorkersViewHolder).binding

        val workers = workerList[position] as JSONObject
        binding.itemEmplId.text = workers.getString("id")
        binding.itemName.text = workers.getString("name")

        val status = workers.getString("status").toInt()
        val area = workers.getString("area")

        if(status == 0){
            binding.itemAttendance.text = "미출근"
            binding.itemAttendance.setTextColor(Color.parseColor("#939393"))
        }
        else if(status == 1){
            binding.itemAttendance.text = "근무중"
            binding.itemAttendance.setTextColor(Color.parseColor("#019839"))
        }
        else if(status == 2){
            binding.itemAttendance.text = "퇴근"
            binding.itemAttendance.setTextColor(Color.BLACK)
        }
        else if(status == 3){
            binding.itemAttendance.text = "근무중"
            binding.itemAttendance.setTextColor(Color.parseColor("#F58D34"))
        }
        else if(status == 4){
            binding.itemAttendance.text = "결근"
            binding.itemAttendance.setTextColor(Color.parseColor("#E44747"))
        }

        if(area.equals("0")){
            binding.itemArea.text = "-"
        }
        else{
            binding.itemArea.text = area
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

    fun spinnerFilter(searchText: String){
        workerList = JSONArray()
        if (searchText.length == 0) {
            workerList = arr
        } else {
            for (i in 0 until arr.length()) {
                val worker = arr.getJSONObject(i)
                var area : String = worker.getString("area")
                if(area.equals("0")){
                    area = "-"
                }

                if (area.trim().equals(searchText[0].toString())) {
                    workerList.put(worker)
                }
            }
        }
        notifyDataSetChanged()
    }
}