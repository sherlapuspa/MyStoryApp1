package com.dicoding.mystoryapp.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.databinding.ActivityPostStoryBinding
import com.dicoding.mystoryapp.viewmodel.PostStoryVM
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.UserVMFactory
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class PostStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var token: String

    private var imgFile: File? = null
    private lateinit var compImgFile: File

    private val postStoryVM: PostStoryVM by lazy {
        ViewModelProvider(this)[PostStoryVM::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = resources.getString(R.string.actionBarPost)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        handleUserActions()

        val userPref = UserPreferencesManager.getInstance(dataStore)
        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userPref))[AuthSplashVM::class.java]

        authSplashVM.getToken().observe(this) {
            token = it
        }

        postStoryVM.message.observe(this) {
            showToast(it)
        }

        postStoryVM.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun handleUserActions() {
        binding.postBtn.setOnClickListener {

            if (imgFile == null) {
                showToast(resources.getString(R.string.alertNullImg))
                return@setOnClickListener
            }

            val desc = binding.tvDesc.text.toString().trim()
            if (desc.isEmpty()) {
                binding.tvDesc.error = resources.getString(R.string.alertNullDesc)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    val file = imgFile as File

                    var compFile: File? = null
                    var compFileSize = file.length()

                    while (compFileSize > 1 * 1024 * 1024) {
                        compFile = withContext(Dispatchers.Default) {
                            Compressor.compress(applicationContext, file)
                        }
                        compFileSize = compFile.length()
                    }

                    compImgFile = compFile ?: file

                }

                val reqImg =
                    compImgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    compImgFile.name,
                    reqImg
                )

                val reqDesc = desc.toRequestBody("text/plain".toMediaType())

                postStoryVM.upload(imgMultipart, reqDesc, token)
            }
        }

        binding.camBtn.setOnClickListener {
            startTakePhoto()
        }

        binding.galleryBtn.setOnClickListener {
            startGallery()
        }
    }

    private var img = false
    private lateinit var imgPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myImg = File(imgPath)
            imgFile = myImg
            val result = BitmapFactory.decodeFile(myImg.path)
            img = true
            binding.imgPreview.setImageBitmap(result)
            binding.tvDesc.requestFocus()
        }
    }

    private val timeStamp: String = SimpleDateFormat(
        FILE_DATE_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    private fun createCustomTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val imgUri: Uri = FileProvider.getUriForFile(
                this@PostStoryActivity,
                getString(R.string.package_name),
                it
            )
            imgPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun uriToFile(pickedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myImg = createCustomTempFile(context)

        val inputStream = contentResolver.openInputStream(pickedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myImg)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myImg
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val pickedImg: Uri = result.data?.data as Uri
            val myImg = uriToFile(pickedImg, this@PostStoryActivity)
            imgFile = myImg
            binding.imgPreview.setImageURI(pickedImg)
            binding.tvDesc.requestFocus()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val picker = Intent.createChooser(intent, getString(R.string.select_img))
        galleryLauncher.launch(picker)
    }
    
    private fun showToast(msg: String) {
        Toast.makeText(
            this@PostStoryActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()

        if (msg == getString(R.string.upload_succeed)) {
            val intent = Intent(this@PostStoryActivity, ListStoryActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarPostStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val FILE_DATE_FORMAT = "MMddyyyy"
    }
}