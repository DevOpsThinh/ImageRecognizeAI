/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognizeai.imageRecognization.presentation.camera

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.forever.bee.imagerecognizeai.core.MainActivity
import com.forever.bee.imagerecognizeai.R
import com.forever.bee.imagerecognizeai.databinding.FragmentCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
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

        openCamera()
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
                        .setTargetRotation(requireActivity().windowManager.defaultDisplay.rotation) // Because our app targets a lower API level of 30
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    try {
                        camProvider.unbindAll()
                        camProvider.bindToLifecycle(this, camSelector, preview, imageCapture)
                    } catch (e: IllegalStateException) {
                        Toast.makeText(
                            requireActivity(),
                            resources.getString(R.string.error_connecting_camera) + '\n' + e.message,
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

    /**
     * Capture a freeze-frame of the camera feed and save the resulting image to the user's device.
     * */
    private fun capturePhoto() {
        if (!this::imageCapture.isInitialized) {
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.error_saving_photo),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val contentValues = (activity as MainActivity).prepareContentValues()

        val pictureFile = getPictureFile(getOutputDirectory())

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
            requireActivity().applicationContext.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.photo_saved),
                        Toast.LENGTH_LONG
                    ).show()

                    val action =
                        CameraFragmentDirections.actionCameraToPicture(pictureFile.absolutePath)

                    requireView().findNavController().navigate(action)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.error_saving_photo),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        )
    }

    /**
     * Gets the image captured a unique file name
     * */
    private fun getPictureFile(mediaDir: File): File = File(
        mediaDir,
        "image_" + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
    )

    /**
     * Determining the correct location to store the image file
     * */
    private fun getOutputDirectory(): File = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    ).let {
        File(
            it, resources.getString(R.string.app_name)
        ).apply {
            mkdir()
        }
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