package com.example.meowbottom.ui.result

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.meowbottom.MainActivity
import com.example.meowbottom.databinding.ActivityResultBinding
import com.example.meowbottom.ui.detailResult.DetailResultActivity
import com.example.meowbottom.ui.symptom.SymptomActivity

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra("RESULT")
        //val result = intent.getStringArrayExtra("RESULT")
        binding.tvResult.text = result.toString()

        binding.btnMoreResult.setOnClickListener {
            //val url = result!!.replace(" ", "-")
            val intentDetail = Intent(this, DetailResultActivity::class.java)
            intentDetail.putExtra("DISEASE", result)
            startActivity(intentDetail)
        }

        binding.btnAskCommunity.setOnClickListener {
            val intentCommunity = Intent(this, MainActivity::class.java)
            intentCommunity.putExtra("FRAGMENT", "Community")
            intentCommunity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentCommunity)
        }

        setupView()
        backPress()
    }

    private fun backPress() {
        binding.back.setOnClickListener {
            onBackPressed()
            /*val intent = Intent(this, SymptomActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)*/
            //finish()
        }
    }

    override fun onBackPressed() {
        /*super.onBackPressed()
        val intent = Intent(this, SymptomActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)*/
        val intent = Intent(this, SymptomActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
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