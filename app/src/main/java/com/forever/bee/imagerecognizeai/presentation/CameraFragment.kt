package com.forever.bee.imagerecognizeai.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.forever.bee.imagerecognizeai.MainActivity
import com.forever.bee.imagerecognizeai.R
import com.forever.bee.imagerecognizeai.databinding.FragmentCameraBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalStateException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()

        openCamera()

        binding.fabTakePhoto.setOnClickListener {
            // TODO("capturePhoto() here")
        }
    }

    /**
     * Establish a connection with the camera and display a live  camera feed.
     * */
    private fun openCamera() {
        if (MainActivity.CameraPermissionHelper.hasCameraPermission(requireActivity()) &&
            MainActivity.CameraPermissionHelper.hasStoragePermission(requireActivity())
        ) {
            val cameraPF = ProcessCameraProvider.getInstance(requireActivity())

            cameraPF.addListener(
                {
                    val camProvider = cameraPF.get()
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(binding.cameraFeed.surfaceProvider)
                        }

//                    val camSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    val camSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                    // Initialise the ImageCapture instance
                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()


                    try {
                        camProvider.unbindAll()
                        camProvider.bindToLifecycle(this, camSelector, preview, imageCapture)
                    } catch (e: IllegalStateException) {
                        Toast.makeText(
                            requireActivity(),
                            resources.getString(R.string.error_connecting_camera),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                ContextCompat.getMainExecutor(requireActivity())
            )
        } else {
            MainActivity.CameraPermissionHelper.requestPermission(requireActivity())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}