package com.example.meowbottom.ui.detailResult

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
import androidx.lifecycle.ViewModelProvider
import com.example.meowbottom.MainActivity
import com.example.meowbottom.R
import com.example.meowbottom.database.History
import com.example.meowbottom.databinding.ActivityDetailResultBinding
import com.example.meowbottom.ui.history.HistoryActivity
import com.example.meowbottom.ui.history.HistoryViewModel
import com.example.meowbottom.ui.history.HistoryViewModelFactory
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.example.meowbottom.ui.map.MapsActivity
import com.example.meowbottom.ui.symptom.SymptomActivity
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class DetailResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailResultBinding
    private val detailResultViewModel: DetailResultViewModel by viewModels<DetailResultViewModel>()
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var userViewModel: UserViewModel
    private var history: History? = null
    private var isLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        backPress()

        /*detailResultViewModel.isLoading.observe(this, {
            showLoading(it)
        })*/
        val disease = intent.getStringExtra("DISEASE")
        val url = disease!!.replace(" ", "-")
        //setupViewModel(disease)
        setupViewModel(url)

        val source = intent.getStringExtra("SOURCE")

        /*binding.tvAbout.text = getString(R.string.about, disease)
        binding.tvDisease.text = disease*/

        binding.btnHome.setOnClickListener {
            val intentHome = Intent(this, MainActivity::class.java)
            intentHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intentHome)
            finish()
        }

        binding.btnClinic.setOnClickListener {
            val intentClinic = Intent(this, MapsActivity::class.java)
            intentClinic.putExtra("TARGET", "Clinic")
            startActivity(intentClinic)
        }

        binding.btnDiagnosa.setOnClickListener {
            if (source == "Camera") {
                val intentDiagnosa = Intent(this, MainActivity::class.java)
                //intentDiagnosa.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intentDiagnosa.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intentDiagnosa.putExtra("FRAGMENT", "Camera")
                startActivity(intentDiagnosa)
                finish()
            } else {
                val intentDiagnosa = Intent(this, SymptomActivity::class.java)
                //intentDiagnosa.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intentDiagnosa.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intentDiagnosa)
                finish()
            }

        }

        setupHistory(disease)

        /*binding.ivSave.setOnClickListener {
            Toast.makeText(this, "SEGERA HADIR", Toast.LENGTH_SHORT).show()
        }*/

    }

    private fun setupHistory(disease: String?) {
        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]
        userViewModel.getUser().observe(this, {user ->
            isLogin = user.isLogin
            val username = user.username
            if (isLogin) {
                historyViewModel = obtainViewModel(this)
                historyViewModel.getHistoryByDisease(disease!!, username).observe(this, { item ->
                    if (item == null) {
                        binding.ivSave.setImageResource(R.drawable.baseline_bookmark_border_24)
                        binding.ivSave.setOnClickListener {
                            history = History()
                            val penyakit = disease
                            val calendar = Calendar.getInstance()
                            val currentDate = calendar.time

                            val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
                            val date  = dateFormat.format(currentDate)
                            history.let { save ->
                                save?.username = username
                                save?.disease = penyakit
                                save?.date = date
                            }
                            historyViewModel.insert(history as History)
                            //Toast.makeText(this, getString(R.string.success_save_history), Toast.LENGTH_SHORT).show()
                            val mySnackbar = Snackbar.make(binding.root, R.string.success_save_history, Snackbar.LENGTH_SHORT)

                            mySnackbar.setAction(R.string.see) {
                                val intent = Intent(this, HistoryActivity::class.java)
                                startActivity(intent)
                            }
                            mySnackbar.show()
                            binding.ivSave.setImageResource(R.drawable.baseline_bookmark_24)
                        }
                    } else {
                        binding.ivSave.setImageResource(R.drawable.baseline_bookmark_24)
                        //history = History()
                        val penyakit = disease
                        /*history.let { delete ->
                            delete?.username = username
                            delete?.disease = penyakit
                        }*/
                        binding.ivSave.setOnClickListener {
                            //historyViewModel.delete(history as History)
                            historyViewModel.deleteByDiseaseAndUsername(disease, username)
                            Toast.makeText(this, getString(R.string.success_delete_history), Toast.LENGTH_SHORT).show()
                            binding.ivSave.setImageResource(R.drawable.baseline_bookmark_border_24)
                        }
                    }
                })
            } else {
                binding.ivSave.setOnClickListener {
                    Toast.makeText(this, getString(R.string.login_first), Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    private fun obtainViewModel(activity: AppCompatActivity): HistoryViewModel {
        val factory = HistoryViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(HistoryViewModel::class.java)
    }

    private fun backPress() {
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val source = intent.getStringExtra("SOURCE")
        if (source == "Camera") {
            val intentDiagnosa = Intent(this, MainActivity::class.java)
            //intentDiagnosa.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intentDiagnosa.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            intentDiagnosa.putExtra("FRAGMENT", "Camera")
            startActivity(intentDiagnosa)
        } else {
            super.onBackPressed()
        }

    }
    /*override fun onBackPressed() {
        onBackPressed()
        *//*val intent = Intent(this, ResultActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()*//*
    }*/

    private fun setupViewModel(disease: String?) {
        detailResultViewModel.getDisease(disease!!)

        detailResultViewModel.name.observe(this, { name ->
            binding.tvAbout.text = getString(R.string.about, name)
            binding.tvDisease.text = name
        })
        detailResultViewModel.penanganan.observe(this, { penanganan ->
            binding.tvSaran.text = penanganan!!.joinToString("\n")
        })
        detailResultViewModel.deskripsi.observe(this , { deskripsi -> 
            binding.tvDetail.text = deskripsi!!.joinToString("\n")
        })
        detailResultViewModel.isLoading.observe(this, {
            showLoading(it)
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.buffering.visibility = View.VISIBLE
        } else {
            binding.buffering.visibility = View.GONE
            binding.tvDisease.visibility = View.VISIBLE
            binding.btnHome.visibility = View.VISIBLE
            binding.btnDiagnosa.visibility = View.VISIBLE
            binding.btnClinic.visibility = View.VISIBLE
            binding.ivSave.visibility = View.VISIBLE
            binding.cardDetail.visibility = View.VISIBLE
            binding.cardPenanganan.visibility = View.VISIBLE
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