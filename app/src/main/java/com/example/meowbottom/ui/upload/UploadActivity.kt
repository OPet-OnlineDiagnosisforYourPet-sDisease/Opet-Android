package com.example.meowbottom.ui.upload

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.meowbottom.MainActivity
import com.example.meowbottom.R
import com.example.meowbottom.databinding.ActivityUploadBinding
import com.example.meowbottom.ui.camera.createCustomTempFile
import com.example.meowbottom.ui.camera.reduceImage
import com.example.meowbottom.ui.camera.uriToFile
import com.example.meowbottom.ui.login.LoginViewModelFactory
import com.example.meowbottom.ui.login.UserPreference
import com.example.meowbottom.ui.login.UserViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var userViewModel: UserViewModel
    private val uploadViewModel by viewModels<UploadViewModel>()
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var location: Location? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = Companion.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.ivCamera.setOnClickListener { startTakePhoto() }
        binding.ivImage.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.switchLocation.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                getMyLastLocation()
            } else {
                location = null
            }
        }

        setupBack()
        setupView()
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    showStartMarker(location)
                } else {
                    Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show()
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showStartMarker(location: Location) {
        this.location = location
        Toast.makeText(
            this,
            "Latitude : ${location.latitude} dan Longitude : ${location.longitude}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {

            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@UploadActivity,
                "com.example.meowbottom",
                it
            )
            currentPhotPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private lateinit var currentPhotPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {

            val myFile = File(currentPhotPath)

            getFile = myFile


            val decodeFile = BitmapFactory.decodeFile(getFile!!.path)
            val exif = ExifInterface(currentPhotPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            val result : Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(
                    decodeFile,
                    90
                )
                ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(
                    decodeFile,
                    180
                )
                ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(
                    decodeFile,
                    270
                )
                ExifInterface.ORIENTATION_NORMAL -> decodeFile
                else -> decodeFile
            }

            try {
                val output: OutputStream = FileOutputStream(myFile)
                result.compress(Bitmap.CompressFormat.JPEG, 100, output)
                output.flush()
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.ivUpload.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding.ivUpload.setImageURI(selectedImg)
        }
    }


    private fun uploadImage() {
        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]

        if (getFile != null) {
            userViewModel.getUser().observe(this, { user ->
                /*val name = user.username
                binding.tvTitle.text = name*/
                val token = user.token

                val file = reduceImage(getFile as File)

                val etCaption = binding.etCaption.text.toString()

                when {
                    etCaption.isEmpty() -> {
                        Toast.makeText(this, getString(R.string.empty_caption), Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        var lat: RequestBody? = null
                        var lon: RequestBody? = null

                        if (location != null) {
                            lat = location?.latitude.toString()
                                .toRequestBody("text/plain".toMediaType())
                            lon = location?.longitude.toString()
                                .toRequestBody("text/plain".toMediaType())
                        }
                        val caption = etCaption.toRequestBody("text/plain".toMediaType())
                        val requestImageFile = file.asRequestBody("image/jepg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            requestImageFile
                        )
                        uploadViewModel.postStory(token, imageMultipart, caption, lat, lon)
                        uploadViewModel.message.observe(this, { message ->
                            uploadViewModel.isError.observe(this, { error ->
                                isError(message, error)
                            })
                        })
                        uploadViewModel.isLoading.observe(this, { isLoading ->
                            showLoading(isLoading)
                        })
                    }
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.error_file), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun isError(message: String?, error: Boolean?) {
        if (error!!) {
            Toast.makeText(this, "$message", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("FRAGMENT", "Community")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBack() {
        binding.back.setOnClickListener {
            onBackPressed()
        }
    }
    private fun setupView() {

        userViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]

        userViewModel.getUser().observe(this, {user ->
            binding.tvTitle.text = user.username
            Glide.with(this)
                .load(user.profil)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.article)
                .into(binding.ivProfile)
        })
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