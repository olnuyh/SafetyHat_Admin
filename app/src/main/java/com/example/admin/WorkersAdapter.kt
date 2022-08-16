package com.example.admin

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemWorkersBinding
import org.json.JSONArray
import org.json.JSONObject

class WorkersViewHolder(val binding : ItemWorkersBinding) : RecyclerView.ViewHolder(binding.root)
class WorkersAdapter(val context : Context, val arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return arr.length()?:0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WorkersViewHolder(ItemWorkersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as WorkersViewHolder).binding

        val workers = arr[position] as JSONObject
        binding.itemEmplId.text = workers.getString("id")
        binding.itemName.text = workers.getString("name")

        val status = workers.getString("status").toInt()

        if(status == 0){
            binding.itemAttendance.text = "미출근"
            binding.itemAttendance.setTextColor(Color.BLACK)
        }
        else if(status == 1){
            binding.itemAttendance.text = "근무중"
            binding.itemAttendance.setTextColor(Color.GREEN)
        }
    }
}