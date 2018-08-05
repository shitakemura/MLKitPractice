package com.example.shitakemura.mlkitpractice

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
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
                detectButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                val image = FirebaseVisionImage.fromBitmap(bitmap)

                FirebaseVision.getInstance()
                        .visionTextDetector
                        .detectInImage(image)
                        .addOnSuccessListener { texts ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE

                            for (block in texts.blocks) {
                                for (line in block.lines) {
                                    for (element in line.elements) {
                                        element.boundingBox?.let {
                                            overlay.add(BoxData(element.text, it))
                                        }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE
                            e.printStackTrace()
                        }
            }
            FACE_DETECTION -> {
                detectButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                val image = FirebaseVisionImage.fromBitmap(bitmap)

                FirebaseVision.getInstance()
                        .visionFaceDetector
                        .detectInImage(image)
                        .addOnSuccessListener {faces ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE

                            for (face in faces) {
                                face.boundingBox?.let {
                                    overlay.add(BoxData("", it))
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE
                            e.printStackTrace()
                        }

            }
            BARCODE_DETECTION -> {
                detectButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                val image = FirebaseVisionImage.fromBitmap(bitmap)

                FirebaseVision.getInstance()
                        .visionBarcodeDetector
                        .detectInImage(image)
                        .addOnSuccessListener { barcodes ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE

                            for (barcode in barcodes) {
                                barcode.boundingBox?.let {
                                    overlay.add(BoxData(barcode.rawValue ?: "", it))
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE
                            e.printStackTrace()
                        }
            }
            LABELING -> {
                detectButton.isEnabled = false
                progressBar.visibility = View.VISIBLE

                val image = FirebaseVisionImage.fromBitmap(bitmap)

                FirebaseVision.getInstance()
                        .visionLabelDetector
                        .detectInImage(image)
                        .addOnSuccessListener { labels ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE

                            overlay.add(TextsData(labels.map { "${it.label}, ${it.confidence}" }))
                        }
                        .addOnFailureListener { e ->
                            detectButton.isEnabled = true
                            progressBar.visibility = View.GONE
                            e.printStackTrace()
                        }
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
