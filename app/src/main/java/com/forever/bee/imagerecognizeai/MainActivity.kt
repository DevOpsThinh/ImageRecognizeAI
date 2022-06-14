package com.forever.bee.imagerecognizeai

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.forever.bee.imagerecognizeai.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

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
            !CameraPermissionHelper.hasStoragePermission(this)
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

        fun requestPermission(activity: Activity) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
                AlertDialog.Builder(activity).apply {
                    setMessage(activity.getString(R.string.permission_required))
                    setPositiveButton(activity.getString(R.string.ok)) { _, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(CAMERA_PERMISSION, READ_PERMISSION),
                            1
                        )
                    }
                    show()
                }
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(CAMERA_PERMISSION, READ_PERMISSION),
                    1
                )
            }
        }
    }
}