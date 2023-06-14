package com.example.meowbottom.ui.detailCommunity

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.meowbottom.MainActivity
import com.example.meowbottom.R
import com.example.meowbottom.adapter.CommentAdapter
import com.example.meowbottom.adapter.CommunityAdapter
import com.example.meowbottom.databinding.ActivityDetailCommunityBinding
import com.example.meowbottom.response.CommentsItem
import com.example.meowbottom.response.Story
import com.example.meowbottom.ui.camera.formatLikesCount
import com.example.meowbottom.ui.camera.getRelativeTime
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DetailCommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCommunityBinding
    private val detailCommunityViewModel by viewModels<DetailCommunityViewModel>()
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val url = intent.data
        var idDetail: String? = null
        if (url != null) {
            val id = url.pathSegments
            //Toast.makeText(this, "$intent", Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, "${id.get(id.lastIndex)}", Toast.LENGTH_SHORT).show()
            idDetail = id.get(id.lastIndex)

        } else {
            idDetail = intent.getStringExtra("ID")
        }
        detailCommunityViewModel.storyDetail(idDetail!!.toInt())
        detailCommunityViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        detailCommunityViewModel.isError.observe(this) {
            if (it!!) {
                binding.imgEmpty.visibility = View.VISIBLE
            } else {
                detailCommunityViewModel.story.observe(this) { story ->
                    showData(story)
                }
            }
        }

        binding.ivShare.setOnClickListener {
            Toast.makeText(this, binding.root.context.getString(R.string.share_story), Toast.LENGTH_SHORT).show()
            val url = "https://opet.com/community/$idDetail"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            intent.putExtra(Intent.EXTRA_TEXT, url)
            binding.root.context.startActivity(Intent.createChooser(intent, "Share using ...."))
        }


        binding.ivSend.setOnClickListener {
            sendMessage(idDetail)
        }

        showComment()

        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun sendMessage(idDetail: String) {
        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]
        val id = idDetail.toInt()
        val etComment = binding.etComment.text.toString()
        when {
            etComment.isEmpty() -> {
                Toast.makeText(this, getString(R.string.empty_comment), Toast.LENGTH_SHORT).show()
            }
            else -> {
                val comment = etComment.toRequestBody("text/plain".toMediaType())
                userViewModel.getUser().observe(this) { user ->
                    val token = user.token
                    detailCommunityViewModel.postComment(token, id, comment)
                    detailCommunityViewModel.isLoading.observe(this) {
                        showLoading2(it)
                    }
                    detailCommunityViewModel.isError.observeOnce(this) {
                        if (it!!) {
                            Toast.makeText(this, getString(R.string.comment_failed), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, getString(R.string.comment_success), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                detailCommunityViewModel.storyDetail(id)
                detailCommunityViewModel.story.observe(this) { story ->
                    showData(story)
                }
                showComment()
                binding.etComment.setText("")
            }
        }
    }

    private fun showLoading2(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar2.visibility = View.VISIBLE
        } else {
            binding.progressBar2.visibility = View.GONE
        }
    }

    private fun showComment() {
        detailCommunityViewModel.listComment.observe(this) { listComment ->
            setCommentData(listComment)
        }
    }

    private fun setCommentData(list: List<CommentsItem?>?) {
        val listComment = ArrayList<CommentsItem>()
        for (item in list!!) {
            listComment.add(
                CommentsItem(
                    commenterName = item?.commenterName,
                    comment = item?.comment,
                    commenterProfil = item?.commenterProfil,
                    commentTime = item?.commentTime
                )
            )
        }
        setAdapter(listComment)
    }

    private fun setAdapter(listComment: ArrayList<CommentsItem>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.rvComments.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvComments.addItemDecoration(itemDecoration)

        val adapter = CommentAdapter(listComment)
        binding.rvComments.adapter = adapter
        binding.rvComments.setHasFixedSize(true)
        binding.rvComments.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showData(story: Story?) {
        binding.cardView.visibility = View.VISIBLE
        binding.cardComment.visibility = View.VISIBLE
        Glide.with(this)
            .load(story?.senderProfil)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.ivProfile)
        binding.tvName.text = story?.senderName
        binding.tvCaption.text = story?.description
        binding.tvDate.text = story?.createdAt?.getRelativeTime(this)
        binding.tvComment.text = formatLikesCount(story?.commentCount ?: 0)
        Glide.with(this)
            .load(story?.photo)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.ivImage)
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

    override fun onBackPressed() {
        val url = intent.data
        if (url != null) {
            val intentCom = Intent(this, MainActivity::class.java)
            intentCom.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intentCom.putExtra("FRAGMENT", "Community")
            startActivity(intentCom)
            finish()
        } else {
            super.onBackPressed()
        }

    }
    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}