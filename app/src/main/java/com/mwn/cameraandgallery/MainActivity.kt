package com.mwn.cameraandgallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mwn.cameraandgallery.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Declared  a variable used to store the Bitmap
    private var bitmap: Bitmap? = null

    // Declared a variable used to store the Image/File Uri
    private var galleryImageUri: Uri? = null

    //Declaring and  Defining a variable to store the result of activity when the Passed Intent is called.
    private var captureImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.data != null) {
            // Storing the Extras in bitmap variable as bitmap
            bitmap = result.data?.extras?.get("data") as Bitmap

            // Changing the visibility of Views of our activity
            binding.imagePreview.visibility = View.VISIBLE
            binding.mainView.visibility = View.GONE
            binding.inputImage.setImageBitmap(bitmap)
        }
    }

    //Declaring and  Defining a variable to store the result of activity when the Passed Intent is called.
    private var galleryImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result?.data != null) {
            // Storing the Extras in bitmap variable as bitmap
            galleryImageUri = result.data?.data

            // Changing the visibility of Views of our activity
            binding.imagePreview.visibility = View.VISIBLE
            binding.mainView.visibility = View.GONE
            binding.inputImage.setImageURI(galleryImageUri)
        }
    }

    // I have created a function to get permissions from the users
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {

    }
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        for (i in permissions) {
            if (!checkPermission(i)) {
                permissionLauncher.launch(permissions)
            }
        }

        clickListener()

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.imagePreview.visibility == View.VISIBLE) {
                    binding.imagePreview.visibility = View.GONE
                    binding.mainView.visibility = View.VISIBLE
                } else {
                    finish()
                }
            }

        })


    }

    private fun clickListener() {
        binding.cameraBtn.setOnClickListener {
            if (checkPermission(Manifest.permission.CAMERA)) {
                val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                captureImage.launch(captureIntent)
            } else {
                permissionLauncher.launch(permissions)
            }

        }

        binding.galleryBtn.setOnClickListener {
            val pickImgIntent = Intent(MediaStore.ACTION_PICK_IMAGES)
            galleryImage.launch(pickImgIntent)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    /*private fun sendImgToActivity() {
        val intent = Intent(this, ImageDisplayActivity::class.java)
        if (bitmap != null) {
            val byteArrayStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayStream)
            val byteArray = byteArrayStream.toByteArray()
            intent.putExtra("from", "camera")
            intent.putExtra("bitmap_image", byteArray)
            startActivity(intent)
        } else {
            intent.putExtra("from", "gallery")
            intent.putExtra("gallery_image_uri", galleryImageUri.toString())
            startActivity(intent)
        }

    }*/

}

