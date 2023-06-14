package com.example.meowbottom.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meowbottom.R
import com.example.meowbottom.data.HistoryItem
import com.example.meowbottom.databinding.ItemHistoryBinding
import com.example.meowbottom.ui.detailResult.DetailResultActivity

class HistoryAdapter(private val listHistory: ArrayList<HistoryItem>): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listHistory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = listHistory[position]
        holder.binding.apply {
            tvItemName.text = history.disease
            tvItemDate.text = history.date
            imgItemPhoto.setImageResource(R.drawable.result)
        }
        holder.itemView.setOnClickListener {
            val intentDetailResult = Intent(holder.itemView.context, DetailResultActivity::class.java)
            intentDetailResult.putExtra("DISEASE", history.disease)
            holder.itemView.context.startActivity(intentDetailResult)
        }
    }


}