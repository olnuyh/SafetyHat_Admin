package com.example.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ItemCardviewBinding
import org.json.JSONArray

class AreaCheckViewHolder(val binding : ItemCardviewBinding, val detailClicked : (String) -> Unit) : RecyclerView.ViewHolder(binding.root)
class AreaCheckAdapter(val context : Context, val arr : ArrayList<String>, val detailClicked : (String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return arr.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AreaCheckViewHolder(ItemCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false), detailClicked)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as AreaCheckViewHolder).binding

        binding.areaCardName.text = arr[position]
        binding.detailBtn.setOnClickListener { detailClicked(arr[position]) }
    }
}