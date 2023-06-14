package com.example.meowbottom.ui.symptom

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.meowbottom.R
import com.example.meowbottom.adapter.SymptomAdapter
import com.example.meowbottom.data.StoryItem
import com.example.meowbottom.data.SymptomItem
import com.example.meowbottom.databinding.ActivitySymptomBinding
import com.example.meowbottom.response.DataItem
import com.example.meowbottom.response.ListStoryItem
import com.example.meowbottom.ui.result.ResultActivity

class SymptomActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySymptomBinding
    private val symptomViewModel by viewModels<SymptomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySymptomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val dog = intent.getStringExtra("DOG")

        setUpViewModel()
        backPress()
    }

    private fun backPress() {
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpViewModel() {
        /*val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLThmTzBLdTRmY3NHcHpGTGciLCJpYXQiOjE2ODMxMDk3OTB9.KkV-GxJau44lusxG3HozILraeGkwGNIR9vIlA1fojyc"
        symptomViewModel.listStory(token)
        symptomViewModel.listStory.observe(this, { storyList ->
            setStoryListData(storyList)
        })
        symptomViewModel.isLoading.observe(this, {
            showLoading(it)
        })*/
        val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLThmTzBLdTRmY3NHcHpGTGciLCJpYXQiOjE2ODMxMDk3OTB9.KkV-GxJau44lusxG3HozILraeGkwGNIR9vIlA1fojyc"
        symptomViewModel.listSymptom.observe(this, { symptomList ->
            setSymptomListData(symptomList)
        })
        symptomViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    private fun setSymptomListData(symptomList: List<DataItem?>?) {
        val listSymptom = ArrayList<SymptomItem>()
        for (item in symptomList!!) {
            listSymptom.add(
                SymptomItem(
                    id = item?.id!!,
                    name = """
                        ${item.nama}
                    """.trimIndent()
                )
            )
        }
        setAdapterSymptom(listSymptom)
    }

    private fun setAdapterSymptom(listSymptom: ArrayList<SymptomItem>) {
        val adapter = SymptomAdapter(listSymptom)
        binding.rvSymptom.adapter = adapter
        binding.rvSymptom.setHasFixedSize(true)

        binding.btnNext.setOnClickListener {
            /* val items = adapter.selectedItems
             val message = "Selected items: ${items.joinToString(", ")}"
             Toast.makeText(this, message, Toast.LENGTH_SHORT).show()*/
            /*val intent = Intent(this, BottomFirstActivity::class.java)
            startActivity(intent)
            finish()*/
            //val list = listOf<String>("1", "0", "1")
            var cek = 0
            val value = ArrayList<String>()
            //val value = listOf<Int>(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
            for (item in listSymptom) {
                value.add(item.value.toString())
                if (item.value == 1) {
                    cek++
                } 
            }
            if (cek <= 2 || cek >= 8) {
                Toast.makeText(this, getString(R.string.invalid_symptom), Toast.LENGTH_SHORT).show()
            } else {
                //Toast.makeText(this, "$value", Toast.LENGTH_SHORT).show()
                symptomViewModel.checkSymptom(value.toString())
                symptomViewModel.message.observe(this, { message ->
                    //Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
                    if (message == "timeout") {
                        Toast.makeText(this, "Timeout, Try again !!", Toast.LENGTH_SHORT).show()
                    } else {
                        val intentResult = Intent(this, ResultActivity::class.java)
                        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        //intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        intentResult.putExtra("RESULT", message)
                        startActivity(intentResult)
                    }
                })
                /*symptomViewModel.postSymptom(value)
                symptomViewModel.message.observe(this, { message ->
                    Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
                })
                symptomViewModel.symptom.observe(this, { list ->
                    val intentResult = Intent(this, ResultActivity::class.java)
                    intentResult.putStringArrayListExtra("RESULT", list as ArrayList<String>?)
                    startActivity(intentResult)
                })*/
            }
            //Toast.makeText(this, "$cek", Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, "$value", Toast.LENGTH_SHORT).show()

            /*val intentResult = Intent(this, ResultActivity::class.java)
            intentResult.putStringArrayListExtra("RESULT", value)
            startActivity(intentResult)*/
        }
        val column = adapter.itemCount / 2
        val layoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvSymptom.layoutManager = layoutManager
        /*Toast.makeText(this, "$column", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()*/
    }

    /*private fun setStoryListData(storyList: List<ListStoryItem?>?) {
        val listStory = ArrayList<StoryItem>()

        for (item in storyList!!) {
            listStory.add(
                StoryItem(
                    name = """
                        ${item?.name}
                    """.trimIndent(),
                    description = """
                        ${item?.description}
                    """.trimIndent(),
                    photoUrl = """
                        ${item?.photoUrl}
                    """.trimIndent(),
                    createdAt = """
                        ${item?.createdAt}
                    """.trimIndent()
                )
            )
        }
        setAdapter(listStory)
    }*/

    /*private fun setAdapter(listStory: ArrayList<StoryItem>) {
        val adapter = SymptomAdapter(listStory)
        binding.rvSymptom.adapter = adapter
        binding.rvSymptom.setHasFixedSize(true)

        binding.btnNext.setOnClickListener {
            *//* val items = adapter.selectedItems
             val message = "Selected items: ${items.joinToString(", ")}"
             Toast.makeText(this, message, Toast.LENGTH_SHORT).show()*//*
            *//*val intent = Intent(this, BottomFirstActivity::class.java)
            startActivity(intent)
            finish()*//*
            //val list = listOf<String>("1", "0", "1")
            val value = ArrayList<String>()
            for (item in listStory) {
                value.add(item.value.toString())
            }
            Toast.makeText(this, "$value", Toast.LENGTH_SHORT).show()
            val intentResult = Intent(this, ResultActivity::class.java)
            intentResult.putStringArrayListExtra("RESULT", value)
            startActivity(intentResult)
        }
        val column = adapter.itemCount / 3
        val layoutManager = StaggeredGridLayoutManager(column, StaggeredGridLayoutManager.HORIZONTAL)
        binding.rvSymptom.layoutManager = layoutManager
        Toast.makeText(this, "$column", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "${adapter.itemCount}", Toast.LENGTH_SHORT).show()
    }*/

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.buffering.visibility = View.VISIBLE
        } else {
            binding.buffering.visibility = View.GONE
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