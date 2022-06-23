/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognization.presentation.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.navigation.findNavController
import com.forever.bee.common.utils.extensions.createUniqueImageFile
import com.forever.bee.imagerecognization.ImageAnalyser
import com.forever.bee.imagerecognization.R
import com.forever.bee.imagerecognization.databinding.FragmentCameraBinding
import com.forever.bee.tflite.task.TFLiteModelClassifier
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *A simple [Fragment] subclass. It's used to show a live feed and capturing photos
 *
 * @property cameraExecutor provide access to an instance of the [ExecutorService] class once initialised
 * (is used to coordinate tasks) to manage actions relating to the camera (capturing photos).
 * @property imageCapture Store an instance of the [ImageCapture] class, handles actions relating
 * to capturing photos using the CameraX library
 * */
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalyser: ImageAnalyser

    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    private companion object {
        private const val TAG = "[ImageRecognition]"
        private const val PERMISSION_REQUEST_CODE = 100
        private val CAMERA_PERMISSIONS_REQUESTED = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    /**
     * Initialises the fragment_camera.xml layout's binding class & allows the fragment to interact
     * with the layout & its widgets.
     * */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Handle user interactions with the fragment's components
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialise the cameraExecutor variable & load the camera feed
        cameraExecutor = Executors.newSingleThreadExecutor()
        imageAnalyser = cameraExecutor.submit(Callable {
            ImageAnalyser(
                requireContext(),
                TFLiteModelClassifier(requireContext())
            ) { result ->
                mainThreadHandler.post {
                    onPredictionResult(result)
                }
            }
        }).get()

        binding.cameraFeed.post {
            if (cameraPermissionsGranted()) {
                openCamera()
            } else {
                requestPermissions(
                    CAMERA_PERMISSIONS_REQUESTED, PERMISSION_REQUEST_CODE
                )
            }
        }

        // The image capture is triggered once the user taps on the floating action button.
        binding.fabTakePhoto.setOnClickListener {
            capturePhoto()
        }
    }

    /**
     * Establish a connection with the camera and display a live camera feed. And
     * configure camera use case
     * */
    private fun openCamera() {

        val cameraPF = ProcessCameraProvider.getInstance(requireContext())

        cameraPF.addListener(
            {
                configureCameraUseCase(cameraPF.get())
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun configureCameraUseCase(camProvider: ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.cameraFeed.surfaceProvider)
            }

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().also {
                it.setAnalyzer(cameraExecutor, imageAnalyser)
            }

//                    val camSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val camSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Initialise the ImageCapture instance
        imageCapture = ImageCapture.Builder()
            .setTargetRotation(requireActivity().windowManager.defaultDisplay.rotation) // Because our app targets a lower API level of 30
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageAnalysis)
            .addUseCase(imageCapture)
        binding.cameraFeed.viewPort?.let {
            useCaseGroup.setViewPort(it)
        }

        try {
            camProvider.unbindAll()
            camProvider.bindToLifecycle(this, camSelector, useCaseGroup.build())
        } catch (e: IllegalStateException) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_connecting_camera) + '\n' + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Capture a freeze-frame of the camera feed and save the resulting image to the user's device.
     * */
    private fun capturePhoto() {
        if (!this::imageCapture.isInitialized) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.error_saving_photo),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        imageCapture.let {
            val pictureFile = createUniqueImageFile(requireContext())
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(pictureFile)
                .build()
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(
                        outputFileResults: ImageCapture.OutputFileResults
                    ) {
                        val action = CameraFragmentDirections
                            .actionCameraToPicture(
                                pictureFile.absolutePath
                            )
                        requireView().findNavController().navigate(action)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Failed to save the picture.", exception)
                        Toast.makeText(
                            context, R.string.error_saving_photo,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

    }

    private fun onPredictionResult(result: List<Pair<String, Float>>) {
        binding.txtClassificationResult.text = result
            .subList(0, 2).joinToString(", ") { "%s (%.0f%%)".format(it.first, it.second) }
    }

    /**
     * Handles the user's response to the permission request.
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (cameraPermissionsGranted()) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(), R.string.permission_required,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun cameraPermissionsGranted() = CAMERA_PERMISSIONS_REQUESTED.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        imageAnalyser.close()
    }
}