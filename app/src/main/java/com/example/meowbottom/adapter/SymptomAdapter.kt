package com.example.meowbottom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.meowbottom.R
import com.example.meowbottom.data.SymptomItem
import com.example.meowbottom.databinding.ItemSymptomBinding

class SymptomAdapter(
    //private val listStory: ArrayList<StoryItem>
    private val listSymptom: ArrayList<SymptomItem>
    ) : RecyclerView.Adapter<SymptomAdapter.ViewHolder>() {

    //var selectedItems: MutableSet<String> = mutableSetOf()
    var selectedItems: MutableList<String> = mutableListOf()

    class ViewHolder (var binding: ItemSymptomBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSymptomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listSymptom.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val symptom = listSymptom[position]
        holder.binding.apply {
           itemName.text = symptom.name
        }

        selectedItems.add(symptom.value.toString())
        holder.itemView.setOnClickListener {
            if (symptom.isSelected) {
                symptom.value = 0
                symptom.isSelected = false
                val unselectedColor = ContextCompat.getColor(holder.binding.root.context, R.color.unselected_item)
                holder.binding.cardView.setCardBackgroundColor(unselectedColor)
                selectedItems.remove(symptom.value.toString())
            } else {
                symptom.value = 1
                val selectedColor = ContextCompat.getColor(holder.binding.root.context, R.color.selected_item)
                holder.binding.cardView.setCardBackgroundColor(selectedColor)
                symptom.isSelected = true
                selectedItems.add(symptom.value.toString())
            }
        }
    }
}
