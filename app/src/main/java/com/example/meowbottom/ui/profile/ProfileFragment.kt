package com.example.meowbottom.ui.profile

import android.Manifest
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.meowbottom.MainActivity
import com.example.meowbottom.R
import com.example.meowbottom.data.UserModel
import com.example.meowbottom.databinding.FragmentProfileBinding
import com.example.meowbottom.ui.camera.CameraFragment
import com.example.meowbottom.ui.camera.reduceImage
import com.example.meowbottom.ui.camera.uriToFile
import com.example.meowbottom.ui.detailResult.DetailResultActivity
import com.example.meowbottom.ui.history.HistoryActivity
import com.example.meowbottom.ui.login.LoginActivity
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.example.meowbottom.ui.post.PostActivity
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userViewModel: UserViewModel
    private val profileViewModel by viewModels<ProfileViewModel>()
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(context, getString(R.string.permission), Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.cardHistory.setOnClickListener {
            val intentHistory = Intent(context, HistoryActivity::class.java)
            startActivity(intentHistory)
        }
        setupProfile()
        setUpDarkLightMode()

        binding.cardLogout.setOnClickListener {
            userViewModel.logout()
            Toast.makeText(context, getString(R.string.log_out_success), Toast.LENGTH_SHORT).show()
            val intentMain = Intent(context, MainActivity::class.java)
            startActivity(intentMain)
        }

        binding.cardLanguage.setOnClickListener {
            Toast.makeText(context, getString(R.string.available_language), Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.fabAdd.setOnClickListener {
            takePhoto()
            binding.btnSave.visibility = View.VISIBLE
        }
        binding.btnSave.setOnClickListener {
            uploadPhoto()
            binding.btnSave.visibility = View.INVISIBLE
            //Toast.makeText(context, getString(R.string.update_profile), Toast.LENGTH_SHORT).show()
        }

    }

    private fun uploadPhoto() {

        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        if (getFile != null) {
            //val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU11MG5KOEkzN3RVTFhhUW4iLCJpYXQiOjE2ODU0Njk5NDR9.nv2Gex8CRBRepy0sfL3OxxDeSWsunbW6KOJ0IVG6zv4"
            val file = reduceImage(getFile as File)
            //val descriptionEDT = "TEST"
            // val description = descriptionEDT.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jepg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "profilePhoto",
                file.name,
                requestImageFile
            )
            userViewModel.getUser().observe(viewLifecycleOwner, { user ->
                profileViewModel.updatePhoto(user.token, imageMultipart)
                profileViewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
                    showLoading(isLoading)
                })
                /*profileViewModel.isError.observeOnce(viewLifecycleOwner, { error ->
                    profileViewModel.message.observeOnce(viewLifecycleOwner, {messsage ->
                        profileViewModel.photoUrl.observeOnce(viewLifecycleOwner, { url ->
                            detailResult(messsage, error, url)
                        })
                        *//*Toast.makeText(context, it, Toast.LENGTH_SHORT).show()*//*
                    })
                })*/
                profileViewModel.isError.observeOnce(viewLifecycleOwner, { error ->
                    if (error) {
                        val snackbar = Snackbar.make(binding.root, R.string.update_profile_failed, Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
                    profileViewModel.photoUrl.observeOnce(viewLifecycleOwner, { url ->
                        detailResult(error, url)
                    })
                })
            })


        } else {
            Toast.makeText(context, getString(R.string.error_file), Toast.LENGTH_SHORT).show()

        }
    }

    private fun takePhoto() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)

    }

    private fun detailResult(error: Boolean?, url: String?) {
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        if (error!!) {
            //Toast.makeText(context, getString(R.string.failed_camera), Toast.LENGTH_SHORT).show()
            val snackbar = Snackbar.make(binding.root, R.string.update_profile_failed, Snackbar.LENGTH_SHORT)
            snackbar.show()
        } else {
            val url = url
            userViewModel.getUser().observe(viewLifecycleOwner, { user ->
                userViewModel.updatePhoto(UserModel(username =  user.username, email =  user.email, token =  user.token, isLogin = true, profil = url))
            })
            val snackbar = Snackbar.make(binding.root, R.string.update_profile, Snackbar.LENGTH_SHORT)
            snackbar.show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding.ivProfile.setImageURI(selectedImg)
        }
    }

    private fun setUpDarkLightMode() {
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        // Periksa dark mode bawaan pada perangkat
        val uiModeManager = requireContext().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val isSystemDarkModeEnabled = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES

        if (isSystemDarkModeEnabled) {
            binding.switchTheme.isChecked = true
        }

        userViewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                // Gunakan dark mode bawaan perangkat jika aktif
                if (isSystemDarkModeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    binding.switchTheme.isEnabled = false
                } else {
                    // Gunakan pengaturan dark mode dari datastore aplikasi jika dark mode bawaan tidak aktif
                    AppCompatDelegate.setDefaultNightMode(if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                    binding.switchTheme.isEnabled = true
                    binding.switchTheme.isChecked = isDarkModeActive
                }
            })

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (!isSystemDarkModeEnabled) {
                userViewModel.saveThemeSetting(isChecked)
            }
        }
    }

    /*private fun setUpDarkLightMode() {
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        userViewModel.getThemeSettings().observe(this,
            { isDarkModeActive: Boolean ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    binding.switchTheme.isChecked = false
                }
            })

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            userViewModel.saveThemeSetting(isChecked)
        }
    }*/

    private fun setupProfile() {
        userViewModel = ViewModelProvider(
            requireActivity(),
            LoginViewModelFactory(UserPreference.getInstance(requireActivity().dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUser().observe(viewLifecycleOwner, {user ->
            if (user.isLogin) {
                val email = user.email
                val name = user.username
                val profil = user.profil
                binding.tvEmail.text = email
                binding.tvName.text = name
                binding.cardPost.setOnClickListener {
                    val intentPost = Intent(context, PostActivity::class.java)
                    intentPost.putExtra("EMAIL", email)
                    startActivity(intentPost)
                }
                if (profil == "") {
                    binding.ivProfile.setImageResource(R.drawable.placeholder)
                } else {
                    Glide.with(requireActivity())
                        .load(profil)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.article)
                        .into(binding.ivProfile)
                }

            } else {
                /*binding.tvEmail.text = getString(R.string.email)
                binding.tvName.text = getString(R.string.name)*/
                val intentLogin = Intent(context, LoginActivity::class.java)
                intentLogin.putExtra("FRAGMENT", "Profile")
                intentLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intentLogin)
            }
        })
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

        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}