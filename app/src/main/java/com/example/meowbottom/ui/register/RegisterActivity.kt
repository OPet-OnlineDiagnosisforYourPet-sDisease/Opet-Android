package com.example.meowbottom.ui.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.meowbottom.R
import com.example.meowbottom.databinding.ActivityRegisterBinding
import com.example.meowbottom.ui.login.LoginActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        setupRegister()
        setupBack()
    }

    private fun setupBack() {
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRegister() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            when {
                name.isEmpty() -> {
                    binding.etName.error = getString(R.string.empty_name)
                }
                email.isEmpty() -> {
                    binding.etEmail.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.etPassword.error = getString(R.string.empty_password)
                }
                password.length < 8 -> {
                    binding.etPassword.error = getString(R.string.invalid_password)
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.etEmail.error = getString(R.string.email_invalid)
                }
                else -> {
                    registerViewModel.register(name, email, password)
                    registerViewModel.isLoading.observe(this, {
                        showLoading(it)
                    })
                    registerViewModel.isError.observeOnce(this, {
                        registerViewModel.message.observeOnce(this, { message ->
                            showError(it, message)
                        })
                    })
                }
            }
        }
    }

    private fun showError(error: Boolean?, message: String?) {
        if (error!!) {
            val snackbar = Snackbar.make(binding.root, "$message", Snackbar.LENGTH_SHORT)
            snackbar.show()
        } else {
            val intentLogin = Intent(this, LoginActivity::class.java)
            intentLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentLogin)
            finish()
            Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
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

    private fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: Observer<T>) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}