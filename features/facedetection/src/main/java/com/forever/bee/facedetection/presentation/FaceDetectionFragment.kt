/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.facedetection.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.forever.bee.facedetection.databinding.FragmentFaceDetectionBinding

/**
 * A simple [FaceDetectionFragment] subclass.
 */
class FaceDetectionFragment : Fragment() {
    private var _binding: FragmentFaceDetectionBinding? = null
    private val binding get() = _binding!!

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

    /**
     * Prevent the fragment from accessing components of the layout in the event the layout has been
     * closed but the fragment is not yet shut down
     * */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}