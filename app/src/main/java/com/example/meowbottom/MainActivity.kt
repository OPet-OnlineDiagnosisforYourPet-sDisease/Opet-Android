package com.example.meowbottom

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.meowbottom.databinding.ActivityMainBinding
import com.example.meowbottom.ui.camera.CameraFragment
import com.example.meowbottom.ui.community.CommunityFragment
import com.example.meowbottom.ui.home.HomeFragment
import com.example.meowbottom.ui.location.LocationFragment
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.example.meowbottom.ui.profile.ProfileFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupTheme()


        // Menambahkan item-menu ke MeowBottomNavigation
        binding.bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.paw))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.baseline_location_on_24))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.camera))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.community))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(5, R.drawable.baseline_person_24))

        setFragment(HomeFragment.newInstance())
        binding.bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    // Navigasi ke HomeActivity
                    setFragment(HomeFragment.newInstance())
                }
                2 -> {
                    // Navigasi ke Location
                    setFragment(LocationFragment.newInstance())

                }
                3 -> {
                    // Navigasi ke Camera
                    setFragment(CameraFragment.newInstance())
                }
                4 -> {
                    // Navigasi ke Community
                    setFragment(CommunityFragment.newInstance())
                }
                5 -> {
                    // Navigasi ke ProfileActivity
                    setFragment(ProfileFragment.newInstance())
                }
            }
        }
        binding.bottomNavigation.show(1)

        val fragmentData = intent.getStringExtra("FRAGMENT")
        if (fragmentData == "Community") {
            setFragment(CommunityFragment.newInstance())
            binding.bottomNavigation.show(4)
        } else if (fragmentData == "Profile") {
            setFragment(ProfileFragment.newInstance())
            binding.bottomNavigation.show(5)
        } else if (fragmentData == "Home") {
            setFragment(HomeFragment.newInstance())
            binding.bottomNavigation.show(1)
        } else if (fragmentData == "Camera") {
            setFragment(CameraFragment.newInstance())
            binding.bottomNavigation.show(3)
        }
        /**
         * PAKAI ACTIVITY
         */
        // Mengatur halaman awal
        /*binding.bottomNavigation.show(1)

        binding.bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    // Navigasi ke HomeActivity
                }
                2 -> {
                    // Navigasi ke FavoritesActivity
                }
                3 -> {
                    // Navigasi ke ProfileActivity
                }
                4 -> {
                    // Navigasi ke ProfileActivity
                }
                5 -> {
                    // Navigasi ke ProfileActivity
                    val intentProfile = Intent(this, ProfileActivity::class.java)
                    intentProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentProfile)
                }
            }
        }*/
    }

    private fun setupTheme() {
        // Periksa dark mode bawaan pada perangkat
        val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val isSystemDarkModeEnabled = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES

        if (isSystemDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            // Gunakan pengaturan dark mode dari datastore aplikasi
            userViewModel = ViewModelProvider(
                this,
                LoginViewModelFactory(UserPreference.getInstance(dataStore))
            )[UserViewModel::class.java]

            userViewModel.getThemeSettings().observe(this, { isDarkModeActive: Boolean ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            })
        }
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment, TAG)
            .commit()
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

    companion object {
        private const val TAG = "MainActivity"
    }
}