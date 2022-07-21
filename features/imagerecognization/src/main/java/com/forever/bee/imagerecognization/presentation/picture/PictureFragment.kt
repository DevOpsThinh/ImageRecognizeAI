/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognization.presentation.picture

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.forever.bee.common.utils.extensions.setImage
import com.forever.bee.imagerecognization.R
import com.forever.bee.imagerecognization.databinding.FragmentPictureBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Build the [Fragment] with displaying the captured image with a small text label to make it
 * clear that the image being shown has been loaded from a file.
 *
 * @property _binding
 * @property binding
 * @property args An [PictureFragmentArgs] instance for passing arguments to [PictureFragment]
 * */
@AndroidEntryPoint
class PictureFragment : Fragment() {
    private var _binding: FragmentPictureBinding? = null
    private val binding get() = _binding!!

    private val args: PictureFragmentArgs by navArgs()

    /**
     * Initialises the fragment_camera.xml layout's binding class & allows the fragment to interact
     * with the layout & its widgets.
     * */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPictureBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_anime, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.anime) {
            navigateToAnimeStyle()
            true
        }
        else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Handle user interactions with the fragment's components (displaying the captured image)
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayLoading()

        binding.pictureImageView.setImage(args.filePath)
    }

    private fun navigateToAnimeStyle() {
        val path = requireArguments().getString(args.filePath)
        val action = PictureFragmentDirections.actionPictureToAnime(path.toString())

        findNavController().navigate(action)
    }

    private fun displayLoading() {
        binding.progressBar.isVisible = false
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