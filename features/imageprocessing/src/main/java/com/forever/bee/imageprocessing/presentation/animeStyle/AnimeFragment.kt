/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imageprocessing.presentation.animeStyle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.forever.bee.common.utils.extensions.setImage
import com.forever.bee.imageprocessing.databinding.FragmentAnimeBinding
import com.forever.bee.imageprocessing.di.DaggerImageProcessingComponent
import com.forever.bee.imagerecognizeai.di.ImageProcessingModuleDependencies
import dagger.hilt.android.EntryPointAccessors

/**
 * A simple AnimeFragment [Fragment] subclass.
 */
class AnimeFragment : Fragment() {

    private var _binding: FragmentAnimeBinding? = null
    private val binding get() = _binding!!

    private val args: AnimeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerImageProcessingComponent.builder()
            .context(requireActivity())
            .moduleDependencies(
                EntryPointAccessors.fromApplication(
                    requireActivity().applicationContext,
                    ImageProcessingModuleDependencies::class.java
                )
            )
            .build()
            .inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAnimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        binding.imageView.setImage(args.filePath)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}