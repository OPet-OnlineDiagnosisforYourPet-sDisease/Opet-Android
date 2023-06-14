package com.example.meowbottom.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meowbottom.R
import com.example.meowbottom.data.CommunityItem
import com.example.meowbottom.data.StoryItem
import com.example.meowbottom.databinding.ItemCommunityBinding
import com.example.meowbottom.ui.camera.getRelativeTime
import com.example.meowbottom.ui.camera.withDateFormat
import com.example.meowbottom.ui.detailCommunity.DetailCommunityActivity

class CommunityAdapter(private val listStory: ArrayList<CommunityItem>) : RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {


    class ViewHolder (var binding: ItemCommunityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = listStory[position]
        holder.binding.apply {
            tvName.text = story.name
            tvCaption.text = story.description
            tvDate.text = story.createdAt.getRelativeTime(root.context)
            Glide.with(root.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivImage)
            Glide.with(root.context)
                .load(story.photoUser)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivProfile)

            ivShare.setOnClickListener {
                Toast.makeText(root.context, root.context.getString(R.string.share_story), Toast.LENGTH_SHORT).show()
                val url = "https://opet.com/community/${story.id}"
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                intent.putExtra(Intent.EXTRA_TEXT, url)
                root.context.startActivity(Intent.createChooser(intent, "Share using ...."))
            }
            root.setOnClickListener {
                val intentDetail = Intent(root.context, DetailCommunityActivity::class.java)
                intentDetail.putExtra("ID", story.id.toString())
                root.context.startActivity(intentDetail)
            }
        }



    }
}