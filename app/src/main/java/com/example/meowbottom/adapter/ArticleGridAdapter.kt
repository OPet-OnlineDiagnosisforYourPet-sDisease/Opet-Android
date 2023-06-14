package com.example.meowbottom.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meowbottom.R
import com.example.meowbottom.data.ItemArticle
import com.example.meowbottom.databinding.ItemArticleBinding
import com.example.meowbottom.databinding.ItemArticleGridBinding
import com.example.meowbottom.ui.article.ArticleActivity

class ArticleGridAdapter(private val listArticle: ArrayList<ItemArticle>) : RecyclerView.Adapter<ArticleGridAdapter.ViewHolder>() {


    class ViewHolder (var binding: ItemArticleGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemArticleGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listArticle.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = listArticle[position]
        holder.binding.apply {
            itemTitle.text = article.title
            //tvPublished.text = root.context.getString(R.string.published, story.createdAt.withDateFormat())
            tvPublished.text = root.context.getString(R.string.published, article.date)
            Glide.with(root.context)
                .load(article.photo)
                .placeholder(R.drawable.placeholder)
                .into(ivArticle)
        }

        holder.itemView.setOnClickListener {
            val intentDetail = Intent(holder.itemView.context, ArticleActivity::class.java)
            intentDetail.putExtra(ArticleActivity.EXTRA_ARTICLE, listArticle[position])
            val optionsCompact: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                Pair(holder.binding.ivArticle, "photo"),
                Pair(holder.binding.itemTitle, "name"),
            )
            holder.itemView.context.startActivity(intentDetail, optionsCompact.toBundle())

        }
    }
}