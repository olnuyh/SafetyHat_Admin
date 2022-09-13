package com.example.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemSalaryBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class SalaryViewHolder(val binding : ItemSalaryBinding) : RecyclerView.ViewHolder(binding.root)
class SalaryAdapter (val context : Context, val arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return arr.length() ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SalaryViewHolder(
            ItemSalaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as SalaryViewHolder).binding

        val salary = arr[position] as JSONObject

        binding.itemName.text = salary.getString("worker_name")
        binding.itemEmplId.text = salary.getString("worker_id")
        binding.itemWorkertime.text = (salary.getString("work_time").toInt() / 60).toString() + "시간"
        binding.itemTotal.text = salary.getString("work_salary") + "원"
    }
}