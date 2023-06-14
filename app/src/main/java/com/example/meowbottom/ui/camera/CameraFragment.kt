package com.example.meowbottom.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.meowbottom.R
import com.example.meowbottom.databinding.FragmentCameraBinding
import com.example.meowbottom.ui.detailResult.DetailResultActivity
import com.example.meowbottom.ui.result.ResultActivity
import com.example.meowbottom.ui.symptom.SymptomViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private var getFile: File? = null
    //private val symptomViewModel by viewModels<SymptomViewModel>()
    private val cameraViewModel by viewModels<CameraViewModel>()
    private var isFirstTime = true

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

    private fun allPermissionsGranted() = Companion.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isFirstTime) {
            showAlertDialog()
            isFirstTime = false
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSION
            )
        }
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun showAlertDialog() {
        /*val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle(context?.getString(R.string.disclaimer))
        alertDialogBuilder.setMessage(context?.getString(R.string.text_disclaimer_camera))
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()*/

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        val dialogButton = dialogView.findViewById<Button>(R.id.btn_ok)
        dialogButton.setOnClickListener {
            alertDialog.dismiss()
        }
        val close = dialogView.findViewById<ImageButton>(R.id.btn_dialog_close)
        close.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            //val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLU11MG5KOEkzN3RVTFhhUW4iLCJpYXQiOjE2ODU0Njk5NDR9.nv2Gex8CRBRepy0sfL3OxxDeSWsunbW6KOJ0IVG6zv4"
            val file = reduceImage(getFile as File)
            //val descriptionEDT = "TEST"
           // val description = descriptionEDT.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jepg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "input",
                file.name,
                requestImageFile
            )
            cameraViewModel.checkDisease(imageMultipart)
            cameraViewModel.isLoading.observe(this, { isLoading ->
                showLoading(isLoading)
            })
            cameraViewModel.isError.observe(this, { error ->
                cameraViewModel.message.observe(this, {messsage ->
                    /*Toast.makeText(context, it, Toast.LENGTH_SHORT).show()*/
                    detailResult(messsage, error)
                })
            })


        } else {
            Toast.makeText(context, getString(R.string.error_file), Toast.LENGTH_SHORT).show()

        }
    }

    private fun detailResult(messsage: String?, error: Boolean?) {
        if (error!!) {
            Toast.makeText(context, getString(R.string.failed_camera), Toast.LENGTH_SHORT).show()
        } else {
            val intentDetail = Intent(context, DetailResultActivity::class.java)
            intentDetail.putExtra("DISEASE", messsage)
            intentDetail.putExtra("SOURCE", "Camera")
            startActivity(intentDetail)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, requireContext())
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireContext().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.meowbottom",
                it
            )
            currentPhotPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private lateinit var currentPhotPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {

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
            binding.previewImageView.setImageBitmap(result)
        }
    }

    companion object {

        fun newInstance(): CameraFragment {
            return CameraFragment()
        }
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}