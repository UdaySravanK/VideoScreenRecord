package com.usk.videoscreenrecord

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName
    var CAMERA_PERMISSION = Manifest.permission.CAMERA

    var RC_PERMISSION = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI()
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            camera_view.toggleCamera()
        }

        if (checkPermissions()) onPermissionGranted() else requestPermissions()

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA_PERMISSION),
                RC_PERMISSION
        )
    }

    private fun checkPermissions(): Boolean {
        return ((ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)) == PackageManager.PERMISSION_GRANTED
                && (ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION)) == PackageManager.PERMISSION_GRANTED)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            RC_PERMISSION -> {
                var allPermissionsGranted = false
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    } else {
                        allPermissionsGranted = true
                    }
                }
                if (allPermissionsGranted) {
                    onPermissionGranted()
                } else {
                    permissionsDenied()
                }
            }
        }
    }

    private fun onPermissionGranted() {
        camera_view.bindToLifecycle(this)
    }

    private fun permissionsDenied() {
        AlertDialog.Builder(this).setTitle("Permissions required")
                .setMessage("These permissions are required to use this app. Please allow Camera and Audio permissions first")
                .setCancelable(false)
                .setPositiveButton("Grant") { dialog, which -> requestPermissions() }
                .show()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}