package com.example.meowbottom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.meowbottom.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi MeowBottomNavigation


        // Menambahkan item-menu ke MeowBottomNavigation
        binding.bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.paw))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.baseline_location_on_24))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.camera))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(4, R.drawable.community))
        binding.bottomNavigation.add(MeowBottomNavigation.Model(5, R.drawable.baseline_person_24))

        // Mengatur halaman awal
        binding.bottomNavigation.show(5)

        binding.bottomNavigation.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    // Navigasi ke HomeActivity
                    val intentHome = Intent(this, MainActivity::class.java)
                    intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intentHome)
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
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
        }
    }
}