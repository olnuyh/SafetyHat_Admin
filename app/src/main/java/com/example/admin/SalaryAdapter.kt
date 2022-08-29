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
        return arr.length()?:0
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SalaryViewHolder(ItemSalaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as SalaryViewHolder).binding

        val salary = arr[position] as JSONObject

        val worker_start=salary.getString("worker_start")
        val start_format=SimpleDateFormat("H:mm:ss").parse(worker_start)
        val start: Long = start_format.getTime()
        val worker_end=salary.getString("worker_end")
        val end_format=SimpleDateFormat("H:mm:ss").parse(worker_end)
        val end: Long = end_format.getTime()
        val diff: Long = (end - start) / 60000
        val hour:Long=diff/60
        val min:Long=diff%60

        binding.itemWorkertime.text = hour.toString()+"시간"+min.toString()+"분"


        val worekr_salary=salary.getString("salary")
        val insert_date=salary.getString("insert_date")


        //binding.itemName.text = salary.getString("")
        //binding.itemEmplId.text = salary.getString("")
        //binding.itemTotal.text = salary.getString("")


    }
}