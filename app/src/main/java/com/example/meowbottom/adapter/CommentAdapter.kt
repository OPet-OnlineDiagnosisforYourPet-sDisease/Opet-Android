package com.example.meowbottom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meowbottom.R
import com.example.meowbottom.databinding.ItemCommentBinding
import com.example.meowbottom.response.CommentsItem
import com.example.meowbottom.ui.camera.getRelativeTime

class CommentAdapter(private val listComment: ArrayList<CommentsItem>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder (var binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listComment.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = listComment[position]
        holder.binding.apply {
            tvName.text = comment.commenterName
            tvComment.text = comment.comment
            tvDate.text = comment.commentTime?.getRelativeTime(root.context)
            ivProfile.setImageResource(R.drawable.placeholder)
            Glide.with(root.context)
                .load(comment.commenterProfil)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(ivProfile)
        }
    }
}