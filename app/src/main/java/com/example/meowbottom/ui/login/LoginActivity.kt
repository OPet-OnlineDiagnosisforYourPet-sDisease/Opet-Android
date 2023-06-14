package com.example.meowbottom.ui.login

import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.meowbottom.MainActivity
import com.example.meowbottom.R
import com.example.meowbottom.data.UserModel
import com.example.meowbottom.databinding.ActivityLoginBinding
import com.example.meowbottom.ui.community.CommunityFragment
import com.example.meowbottom.ui.register.RegisterActivity
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var userViewModel: UserViewModel
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        setupViewModel()
        setupLogin()
    }

    private fun setupViewModel() {
        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUser().observe(this, { user ->
            this.user = user
        })
    }

    private fun setupLogin() {
        val dataIntent = intent.getStringExtra("FRAGMENT")
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            when {
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
                    loginViewModel.login(email, password)
                    loginViewModel.login.observeOnce(this, { user ->
                        val username = user?.username
                        val email = user?.email
                        val token = user?.token
                        val profil = user?.profil
                        userViewModel.saveUser(UserModel(username =  username!!, email =  email!!, token =  token!!, isLogin = true, profil = profil))
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("FRAGMENT", dataIntent)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    })
                    loginViewModel.isLoading.observe(this, {
                        showLoading(it)
                    })
                    loginViewModel.message.observeOnce(this, {
                        /*Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()*/
                       showSnackbar(it)
                    })
                    /*loginViewModel.isError.observe(this, {
                        if (!it!!) {
                            val intent = Intent(this, CommunityFragment::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                    })*/
                }
            }
        }
    }

    private fun showSnackbar(message: String?) {
        val snackbar = Snackbar.make(binding.root, "$message", Snackbar.LENGTH_SHORT)
        snackbar.show()
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

    companion object {
        private const val TAG = "LoginActivity"
    }
}