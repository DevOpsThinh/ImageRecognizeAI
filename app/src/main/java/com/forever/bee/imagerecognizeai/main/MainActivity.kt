/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognizeai.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.forever.bee.imagerecognizeai.R
import com.forever.bee.imagerecognizeai.databinding.ActivityMainBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding

    // Just like the regular nav, but letting you navigate to dynamic feature modules just as to regular modules
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
                as DynamicNavHostFragment).navController
    }

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            topLevelDestinationIds = setOf(
                com.forever.bee.imagerecognization.R.id.imageRecognitionFragment,
                com.forever.bee.facedetection.R.id.faceDetectionFragment
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to Theme_ImageRecognizeAI for displaying the activity
        setTheme(R.style.Theme_ImageRecognizeAI)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupBottomNav()

        initializeAnalytic()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initializeAnalytic() {
        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics
    }

    private fun setupActionBar() {
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNav() {
        binding.navView.setupWithNavController(navController)
        hideBottomNav()
    }

    private fun hideBottomNav() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.forever.bee.imagerecognization.R.id.pictureFragment -> binding.navView.visibility =
                    View.GONE
                else -> binding.navView.visibility = View.VISIBLE
            }
        }
    }
}