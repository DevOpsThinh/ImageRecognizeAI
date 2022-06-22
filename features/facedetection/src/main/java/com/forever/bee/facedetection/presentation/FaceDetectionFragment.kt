/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.facedetection.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import com.forever.bee.facedetection.RealtimeFaceDetector
import com.forever.bee.facedetection.databinding.FragmentFaceDetectionBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.forever.bee.facedetection.R

/**
 * A simple [FaceDetectionFragment] subclass.
 */
class FaceDetectionFragment : Fragment() {
    private var _binding: FragmentFaceDetectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageAnalyser: RealtimeFaceDetector

    private val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    private companion object {
        private const val TAG = "[FaceDetection]"
        private const val PERMISSION_REQUEST_CODE = 100
        private val CAMERA_PERMISSIONS_REQUESTED = arrayOf(
            Manifest.permission.CAMERA
        )
    }

    /**
     * Initialises the fragment_face_detection.xml layout's binding class & allows the fragment to interact
     * with the layout & its widgets.
     * */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFaceDetectionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        imageAnalyser = RealtimeFaceDetector(requireContext()) { face ->
            mainThreadHandler.post {
                onFaceDetected(face)
            }
        }

        binding.cameraFeed.post {
            if (cameraPermissionsGranted()) {
                openCamera()
            } else {
                requestPermissions(CAMERA_PERMISSIONS_REQUESTED, PERMISSION_REQUEST_CODE)
            }
        }

    }

    private fun onFaceDetected(face: Bitmap?) {
        if (face != null) {
            binding.faceView.setImageBitmap(face)
        } else {
            binding.faceView.setImageResource(android.R.drawable.ic_menu_help)
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
            .setTargetResolution(Size(480, 360))
            .build().also {
                it.setAnalyzer(cameraExecutor, imageAnalyser)
            }

//                    val camSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val camSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(preview)
            .addUseCase(imageAnalysis)

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
     * Handles the user's response to the permission request.
     * */
    @Deprecated("Deprecated in Java")
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
    }

    /**
     * Prevent the fragment from accessing components of the layout in the event the layout has been
     * closed but the fragment is not yet shut down
     * */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}