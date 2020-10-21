package com.usk.videoscreenrecord

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
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
        fab.setOnTouchListener(object : OnTouchListener {
            var dX = 0f
            var dY = 0f
            var startX = 0f
            var startY = 0f
            var lastAction = 0
            var screenWidth = getScreenWidth(this@MainActivity)
            var screenHeight = getScreenHeight(this@MainActivity)

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        startX = event.rawX
                        startY = event.rawY
                        lastAction = MotionEvent.ACTION_DOWN
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val newY = event.rawY + dY
                        val newX = event.rawX + dX
                        v.y = when {
                            newY > screenHeight.toFloat() - v.height  -> screenHeight.toFloat() - v.height
                            newY < 0 -> 0f
                            else -> newY
                        }
                        v.x = when {
                            newX > screenWidth.toFloat() - v.width -> screenWidth.toFloat()- v.width
                            newX < 0 -> 0f
                            else -> newX
                        }
                        lastAction = MotionEvent.ACTION_MOVE
                        Log.d("uday", "v.y: ${v.y}  v.x : ${v.x } screenHeight: $screenHeight screenWidth: $screenWidth v.height: ${v.height} v.width: ${v.width}")
                    }
                    MotionEvent.ACTION_UP -> if (Math.abs(startX - event.rawX) < 10 && Math.abs(startY - event.rawY) < 10) {
                        camera_view.toggleCamera()
                    }
                    else -> return false
                }
                return true
            }
        })

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
    fun getScreenWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}