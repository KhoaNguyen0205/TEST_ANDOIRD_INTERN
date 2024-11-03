package com.example.myapp

import PhotosAdapter
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream

class FullImageActivity : AppCompatActivity() {

    private lateinit var imageViewFull: ImageView
    private lateinit var buttonCrop: Button
    private lateinit var buttonRemove: Button
    private var imageUri: Uri? = null // Lưu trữ URI của ảnh
    private val photosList = mutableListOf<Uri>()
    private lateinit var photosAdapter: PhotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)


        imageViewFull = findViewById(R.id.imageViewFull)
        buttonCrop = findViewById(R.id.buttonCrop)
        buttonRemove = findViewById(R.id.buttonDelete)

        // Lấy URI từ Intent
        imageUri = intent.getParcelableExtra("IMAGE_URI")
        imageViewFull.setImageURI(imageUri)

        imageViewFull.setOnClickListener {
            finish() // Đóng activity
        }

        // Thêm listener cho nút cắt
        buttonCrop.setOnClickListener {
            startCrop(imageUri)
        }

        buttonRemove.setOnClickListener {
            removeImg(imageUri)
        }
    }

    private fun removeImg(imageUri: Uri?) {
        imageUri?.let {
            // Delete the image using the content resolver
            val rowsDeleted = contentResolver.delete(imageUri, null, null)
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Image removed from gallery", Toast.LENGTH_SHORT).show()
                finish() // Close the activity
            } else {
                Toast.makeText(this, "Failed to remove image", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No image to remove", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCrop(sourceUri: Uri?) {
        if (sourceUri != null) {
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))
            UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1f, 1f) // Tỉ lệ cắt, có thể điều chỉnh
                .withMaxResultSize(800, 800) // Kích thước tối đa của ảnh cắt
                .start(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            imageViewFull.setImageURI(resultUri) // Hiển thị ảnh đã cắt

            // Save the cropped image to the gallery
            saveImageToGallery(resultUri)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            // Handle the error
            Toast.makeText(this, cropError?.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToGallery(uri: Uri?) {
        uri?.let {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "Cropped_Image_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES) // For Android Q and above
            }

            // Insert the image into the MediaStore
            val resolver = contentResolver
            val uriSaved = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uriSaved?.let { savedUri ->
                resolver.openOutputStream(savedUri)?.use { outputStream ->
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(outputStream) // Copy the cropped image to the output stream
                        Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
