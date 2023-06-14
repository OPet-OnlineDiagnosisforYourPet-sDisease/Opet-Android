package com.example.meowbottom.ui.post

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meowbottom.adapter.CommunityAdapter
import com.example.meowbottom.data.CommunityItem
import com.example.meowbottom.databinding.ActivityPostBinding
import com.example.meowbottom.response.StoriesItem

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private val postViewModel by viewModels<PostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val email = intent.getStringExtra("EMAIL")
        postViewModel.listYourStory(email!!)
        postViewModel.listStory.observe(this, { lisStory ->
            setStoryListData(lisStory)
        })
        postViewModel.isLoading.observe(this, {
            showLoading(it)
        })
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setStoryListData(lisStory: List<StoriesItem?>?) {
        val listStory = ArrayList<CommunityItem>()
        for (item in lisStory!!) {
            listStory.add(
                CommunityItem(
                    id = item?.id!!,
                    name = """
                        ${item?.senderName}
                    """.trimIndent(),
                    description = """
                        ${item?.description}
                    """.trimIndent(),
                    photoUrl = """
                        ${item?.photo}
                    """.trimIndent(),
                    createdAt = """
                        ${item?.createdAt}
                    """.trimIndent(),
                    photoUser = """
                        ${item?.senderProfil}
                    """.trimIndent()
                )
            )
        }
        setAdapter(listStory)
    }
    private fun setAdapter(listStory: ArrayList<CommunityItem>) {

        val layoutManager = LinearLayoutManager(this)
        binding.rvPost.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvPost.addItemDecoration(itemDecoration)

        val adapter = CommunityAdapter(listStory)
        binding.rvPost.adapter = adapter
        binding.rvPost.setHasFixedSize(true)

        if (adapter.itemCount == 0) {
            binding.imgEmpty.visibility = View.VISIBLE
        } else {
            binding.rvPost.adapter = adapter
            binding.rvPost.setHasFixedSize(true)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
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