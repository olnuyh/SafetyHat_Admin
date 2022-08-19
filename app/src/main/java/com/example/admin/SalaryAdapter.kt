package com.example.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemSalaryBinding
import org.json.JSONArray
import org.json.JSONObject


class SalaryViewHolder(val binding : ItemSalaryBinding) : RecyclerView.ViewHolder(binding.root)
class SalaryAdapter (val context : Context, val arr : JSONArray) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return arr.length()?:0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SalaryViewHolder(ItemSalaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as SalaryViewHolder).binding

        val salary = arr[position] as JSONObject
        binding.itemName.text = salary.getString("worker_start")
        binding.itemEmplId.text = salary.getString("worker_end")
        binding.itemWorkertime.text = salary.getString("salary")
        //binding.itemTotal.text = salary.getString("")


//        val worker_start=salary.getString("worker_start")
//        val worker_end=salary.getString("worker_end")
//        val worekr_salary=salary.getString("salary")
//        val insert_date=salary.getString("insert_date")


//        val insert_date = notification.getString("insert_date")
//        val date = insert_date.substring(0, insert_date.indexOf(" "))
//        binding.itemDate.text = date
    }
}