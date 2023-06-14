package com.example.meowbottom.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meowbottom.R
import com.example.meowbottom.databinding.ItemCommunityBinding
import com.example.meowbottom.response.StoriesItem
import com.example.meowbottom.ui.camera.formatLikesCount
import com.example.meowbottom.ui.camera.getRelativeTime
import com.example.meowbottom.ui.detailCommunity.DetailCommunityActivity

class CommunityPageAdapter : PagingDataAdapter<StoriesItem, CommunityPageAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemCommunityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoriesItem) {
            binding.tvName.text = data.senderName
            binding.tvCaption.text = data.description
            binding.tvDate.text = data.createdAt?.getRelativeTime(binding.root.context)
            binding.tvComment.text = formatLikesCount(data.commentCount ?: 0)
            Glide.with(binding.root)
                .load(data.photo)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.ivImage)
            Glide.with(binding.root)
                .load(data.senderProfil)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.ivProfile)
            binding.ivShare.setOnClickListener {
                Toast.makeText(binding.root.context, binding.root.context.getString(R.string.share_story), Toast.LENGTH_SHORT).show()
                val url = "https://opet.com/community/${data.id}"
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
                intent.putExtra(Intent.EXTRA_TEXT, url)
                binding.root.context.startActivity(Intent.createChooser(intent, "Share using ...."))
            }
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailCommunityActivity::class.java)
                intentDetail.putExtra("ID", data.id.toString())
                /*val optionsCompact: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.ivImage, "photo"),
                    Pair(binding.tvName, "name"),
                    Pair(binding.tvCaption, "description"),
                )
                binding.root.context.startActivity(intentDetail, optionsCompact.toBundle())*/
                binding.root.context.startActivity(intentDetail)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoriesItem>() {
            override fun areItemsTheSame(oldItem: StoriesItem, newItem: StoriesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoriesItem, newItem: StoriesItem): Boolean {
                return oldItem.senderName == newItem.senderName
            }
        }
    }
}