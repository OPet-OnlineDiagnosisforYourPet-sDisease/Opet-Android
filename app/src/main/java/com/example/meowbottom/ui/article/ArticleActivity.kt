package com.example.meowbottom.ui.article

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.example.meowbottom.R
import com.example.meowbottom.data.ItemArticle
import com.example.meowbottom.data.StoryItem
import com.example.meowbottom.databinding.ActivityArticleBinding
import com.example.meowbottom.response.ListStoryItem

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding

    companion object {
        const val EXTRA_ARTICLE = "EXTRA_ARTICLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupArticle()

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupArticle() {
        val article = intent.getParcelableExtra<ItemArticle>(EXTRA_ARTICLE) as ItemArticle
        Glide.with(this)
            .load(article.photo)
            .into(binding.ivArticle)
        binding.tvTitle.text = article.title
        binding.tvDescription.text = article.description
        binding.tvAuthor.text = getString(R.string.author, article.author)
        binding.tvDate.text = article.date
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}