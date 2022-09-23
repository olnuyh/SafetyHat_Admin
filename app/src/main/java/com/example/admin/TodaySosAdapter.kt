package com.example.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemTodaySosBinding
import java.text.SimpleDateFormat

class TodaySosViewHolder(val binding : ItemTodaySosBinding) : RecyclerView.ViewHolder(binding.root)
class TodaySosAdapter(val context : Context, val arr : ArrayList<SosMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TodaySosViewHolder(ItemTodaySosBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as TodaySosViewHolder).binding

        val message = arr[position]

        val timestamp = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(message.timeStamp)

        binding.mainSosMessageTime.text = SimpleDateFormat("a hh:mm").format(timestamp)
        binding.mainSosMessageName.text = message.name
        binding.mainSosMessageContents.text = message.content
    }
}