package com.example.shitakemura.mlkitpractice

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.max

class MainActivity : AppCompatActivity(), ImagePickFragment.ImagePickListener {

    private var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val detectors = listOf(
                TEXT_DETECTION,
                CLOUD_TEXT_DETECTION,
                FACE_DETECTION,
                BARCODE_DETECTION,
                LABELING,
                CLOUD_LABELING,
                CLOUD_LANDMARK
        )

        detectorSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, detectors).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        detectButton.setOnClickListener {
            bitmap?.let { detect(it) }
        }
    }

    override fun onImagePicked(imageUri: Uri) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

        val scaleFactor = max(
                imageBitmap.width.toFloat() / imageView.width.toFloat(),
                imageBitmap.height.toFloat() / imageView.height.toFloat()
        )

        val targetWidth = (imageBitmap.width / scaleFactor).toInt()
        val targetHeight = (imageBitmap.height / scaleFactor).toInt()

        bitmap = Bitmap.createScaledBitmap(
                imageBitmap,
                targetWidth,
                targetHeight,
                true
        )

        imageView.setImageBitmap(bitmap)

        overlay.clear()
        overlay.targetWidth = targetWidth
        overlay.targetHeight = targetHeight

        detectButton.isEnabled = true
    }

    private fun detect(bitmap: Bitmap) {
        overlay.clear()

        val detectorName = detectorSpinner.selectedItem as String
        when (detectorName) {
            TEXT_DETECTION -> {

            }
            FACE_DETECTION -> {

            }
            BARCODE_DETECTION -> {

            }
            LABELING -> {

            }
            CLOUD_TEXT_DETECTION -> {

            }
            CLOUD_LABELING -> {

            }
            CLOUD_LANDMARK -> {

            }
        }
    }

    companion object {
        private const val TEXT_DETECTION = "text"
        private const val CLOUD_TEXT_DETECTION = "Cloud Text"
        private const val FACE_DETECTION = "Face"
        private const val BARCODE_DETECTION = "Barcode"
        private const val LABELING = "Labeling"
        private const val CLOUD_LABELING = "Cloud Labeling"
        private const val CLOUD_LANDMARK = "Cloud Landmark"
    }
}
