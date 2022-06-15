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
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.forever.bee.imagerecognizeai.R
import com.forever.bee.imagerecognizeai.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_camera
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
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
     * Assign content values to an image for the image's display name
     * */
    fun prepareContentValues(): ContentValues {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "image_$timeStamp"

        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM")
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