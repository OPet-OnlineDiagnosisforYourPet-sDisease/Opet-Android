package com.example.meowbottom.ui.disclaimer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.example.meowbottom.databinding.ActivityDisclaimerBinding
import com.example.meowbottom.ui.symptom.SymptomActivity

class DisclaimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisclaimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisclaimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        backPress()

        val dog = intent.getStringExtra("DOG")
        binding.btnNext.setOnClickListener {
            val intentSymptom = Intent(this, SymptomActivity::class.java)
            intentSymptom.putExtra("DOG", "dog")
            startActivity(intentSymptom)
        }
        //binding.tvDisclaimer.text = dog
    }

    private fun backPress() {
        binding.back.setOnClickListener {
            onBackPressed()
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