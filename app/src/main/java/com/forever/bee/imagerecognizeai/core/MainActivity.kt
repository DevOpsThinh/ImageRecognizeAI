/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognizeai.core

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.forever.bee.imagerecognizeai.R
import com.forever.bee.imagerecognizeai.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController by lazy {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.navController
    }

    private val  appBarConfiguration by lazy {
        AppBarConfiguration(topLevelDestinationIds = setOf(
            R.id.nav_camera, com.forever.bee.facedetection.R.id.nav_face_detection
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch to Theme_ImageRecognizeAI for displaying the activity
        setTheme(R.style.Theme_ImageRecognizeAI)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupBottomNav()
    }

    private fun setupActionBar() {
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNav() {
        binding.navView.setupWithNavController(navController)
    }

    private fun setNavGraphStartDestination(startDestination: Int) {
        val navGraph = navController.navInflater.inflate(R.navigation.camerax_navigation)

        navGraph.setStartDestination(startDestination)
        navController.graph = navGraph
    }

    /**
     * Handles the user's response to the permission request.
     * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (!CameraPermissionHelper.hasCameraPermission(this) ||
            !CameraPermissionHelper.hasStoragePermission(this) ||
            !CameraPermissionHelper.hasWriteESPermission(this)
        ) {
            CameraPermissionHelper.requestPermission(this)
        } else {
            recreate()
        }
    }

    /**
     * The [CameraPermissionHelper] object will contain methods for initialing and
     * handling all permission-related processes.
     * */
    object CameraPermissionHelper {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val READ_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

        fun hasCameraPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                CAMERA_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasStoragePermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity,
                READ_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun hasWriteESPermission(activity: Activity): Boolean {
            return ContextCompat.checkSelfPermission(
                activity, WRITE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermission(activity: Activity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
                AlertDialog.Builder(activity).apply {
                    setMessage(activity.getString(R.string.permission_required))
                    setPositiveButton(activity.getString(R.string.ok_option)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(CAMERA_PERMISSION, READ_PERMISSION, WRITE_PERMISSION),
                            1
                        )
                    }
                    show()
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(CAMERA_PERMISSION, READ_PERMISSION, WRITE_PERMISSION),
                    1
                )
            }
        }
    }
}