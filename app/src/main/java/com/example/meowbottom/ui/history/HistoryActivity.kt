package com.example.meowbottom.ui.history

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meowbottom.adapter.HistoryAdapter
import com.example.meowbottom.data.HistoryItem
import com.example.meowbottom.database.History
import com.example.meowbottom.databinding.ActivityHistoryBinding
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import java.util.*
import kotlin.collections.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupRecycler()

        val historyViewModel = obtainViewModel(this)
        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]
        userViewModel.getUser().observe(this, { user ->
            val username = user.username
            historyViewModel.getAllHistories(username).observe(this, { history ->
                setDiseaseHistory(history)
            })
        })


        binding.back.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setDiseaseHistory(items: List<History>?) {
        val listHistory = ArrayList<HistoryItem>()

        for (item in items!!) {
            listHistory.add(
                HistoryItem(
                    disease = item.disease,
                    date = item.date!!
                )
            )
        }
        setAdapter(listHistory)
    }

    private fun setAdapter(listHistory: java.util.ArrayList<HistoryItem>) {
        val adapter = HistoryAdapter(listHistory)
        binding.rvHistory.adapter = adapter
        binding.rvHistory.setHasFixedSize(true)
        if (adapter.itemCount == 0) {
            binding.imgEmpty.visibility = View.VISIBLE
        } else {
            binding.rvHistory.adapter = adapter
            binding.rvHistory.setHasFixedSize(true)
        }

    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = HistoryViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(HistoryViewModel::class.java)
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)
        /*binding.btnTest.setOnClickListener {
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val message = "Current Date: $currentDate\nCurrent Time: $currentTime"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }*/
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