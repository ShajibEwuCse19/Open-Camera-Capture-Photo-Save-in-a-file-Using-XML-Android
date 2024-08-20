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

class MainActivity : AppCompatActivity() {
    private final  val REQUEST_CODE = 101
    private final val CAMERA_REQUEST_CODE = 102
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
        if (requestCode == REQUEST_CODE && grantResults[0] == PERMISSION_GRANTED && grantResults.isNotEmpty()) {
            openCamera()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    //if the file is not null then open the camera
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        file = getImageFile()
        if (file != null) {
            val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, file?.absolutePath)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
        }
    }

    // Android-specific path information.
    private fun getImageFile(): File? {
        try {
            var path = getExternalFilesDir(null)?.absolutePath
            //system time added for unique file name
            var imageFile = File(path, "imageFile1" + System.currentTimeMillis().toString())
            if (!imageFile.exists()) {
                val isCreated = imageFile.createNewFile()

                if (isCreated) {
                    return imageFile
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("onActivityResult", "data: $data requestCode: $requestCode resultCode: $resultCode RESULT_OK: $RESULT_OK")
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            var imagePath = file?.absolutePath
            try {
                var bitmap = BitmapFactory.decodeFile(imagePath)
                imageView?.setImageBitmap(bitmap)

            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            var testData = data?.data
        }
    }
}