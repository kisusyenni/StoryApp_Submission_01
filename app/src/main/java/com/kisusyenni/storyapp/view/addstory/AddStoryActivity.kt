package com.kisusyenni.storyapp.view.addstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.kisusyenni.storyapp.R
import com.kisusyenni.storyapp.data.SessionPreference
import com.kisusyenni.storyapp.databinding.ActivityAddStoryBinding
import com.kisusyenni.storyapp.utils.createCustomTempFile
import com.kisusyenni.storyapp.utils.reduceFileImage
import com.kisusyenni.storyapp.utils.uriToFile
import com.kisusyenni.storyapp.view.home.HomeActivity
import com.kisusyenni.storyapp.viewmodel.AddStoryViewModel
import com.kisusyenni.storyapp.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var token: String

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            contentResolver
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setViewModel()

        binding.cameraBtn.setOnClickListener { startTakePhoto() }
        binding.galleryBtn.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.no_camera_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setViewModel() {
        addStoryViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreference.getInstance(dataStore))
        )[AddStoryViewModel::class.java]

        addStoryViewModel.getSession().observe(this) { session ->
            token = session.token
        }

        addStoryViewModel.isLoading.observe(this) { loading ->
            showLoading(loading)
        }

        addStoryViewModel.error.observe(this) { res ->
            if (res.error) {
                Toast.makeText(this@AddStoryActivity, res.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@AddStoryActivity,
                    resources.getString(R.string.upload_story),
                    Toast.LENGTH_SHORT
                ).show()
                Intent(this@AddStoryActivity, HomeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

    }

    private fun showLoading(loading: Boolean) {
        binding.pbAddStory.visibility = if (loading) View.VISIBLE else View.GONE
        binding.buttonAdd.isEnabled = !loading
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.kisusyenni.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uploadImage() {
        val desc = binding.edAddDescription.text.toString()

        if (getFile === null && TextUtils.isEmpty(desc)) {
            Toast.makeText(
                this@AddStoryActivity,
                resources.getString(R.string.empty_data_story),
                Toast.LENGTH_LONG
            ).show()
        } else if (getFile === null && !TextUtils.isEmpty(desc)) {
            Toast.makeText(
                this@AddStoryActivity,
                resources.getString(R.string.empty_image),
                Toast.LENGTH_LONG
            ).show()
        } else if (getFile != null && TextUtils.isEmpty(desc)) {
            Toast.makeText(
                this@AddStoryActivity,
                resources.getString(R.string.empty_description),
                Toast.LENGTH_LONG
            ).show()
        } else if (getFile != null && !TextUtils.isEmpty(desc)) {
            val file = reduceFileImage(getFile as File)
            val description = desc.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            addStoryViewModel.uploadStory(
                token = token,
                image = imageMultipart,
                description = description
            )
        } else {
            Toast.makeText(this@AddStoryActivity, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}