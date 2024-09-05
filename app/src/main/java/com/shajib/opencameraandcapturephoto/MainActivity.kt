package com.shajib.opencameraandcapturephoto

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private var file: File? = null

    private var imageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cameraButton = findViewById<ImageView>(R.id.iv_camera)
        imageView = findViewById(R.id.iv_Image)

        cameraButton.setOnClickListener {
            checkCameraPermission()
        }
    }

    //check if the camera permission is granted
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermission()
        }
    }

    //request the camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CODE)
    }

    //if the permission is granted then open the camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // Open the camera and capture the photo
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = createImageFile()

        file?.let {
            val photoURI = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    // Create a temporary file to store the captured image
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = cacheDir // You can also use externalCacheDir or getExternalFilesDir() for permanent storage
        return try {
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (e: IOException) {
            Log.e("MainActivity", "Error creating file", e)
            null
        }
    }

    // Handle the captured image result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            file?.let {
                val bitmap = BitmapFactory.decodeFile(it.absolutePath)
                imageView?.setImageBitmap(bitmap)
            }
        }
    }
}
