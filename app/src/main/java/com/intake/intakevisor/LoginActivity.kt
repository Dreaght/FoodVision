package com.intake.intakevisor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.intake.intakevisor.ui.settings.SettingsActivity
import com.intake.intakevisor.ui.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1
    private val REQUEST_PERMISSIONS = 2
    private var permissionDenialCount = 0  // Track permission denials

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Check if this activity was launched due to a logout
        if (intent.getBooleanExtra("logout", false)) {
            logOut()
            // After logging out, relaunch the login flow
            launchSignInFlow()
            return // Prevent further execution in onCreate
        }

        // Reset permission denial count if the app was restarted
        permissionDenialCount = 0

        // Check if the user is already signed in via Firebase
        verifyAccountExists()
    }

    private fun verifyAccountExists() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Account exists, validate the token
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Token is valid, proceed to check permissions
                        checkPermissionsAndProceed(currentUser)
                    } else {
                        // Token is invalid, sign out and prompt for re-sign-in
                        auth.signOut()
                        Toast.makeText(this, "Session expired. Please sign in again.", Toast.LENGTH_LONG).show()
                        launchSignInFlow()
                    }
                }
        } else {
            // Account doesn't exist, launch sign-in flow
            launchSignInFlow()
        }
    }

    private fun logOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // Ensure sign-out from Google account explicitly
                AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener {
                        // Clear any local user data if needed (optional)
                        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun launchSignInFlow() {
        // Google sign-in provider
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(false)
            .build()

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val user = auth.currentUser
                if (user != null) {
                    // After successful login, check permissions
                    checkPermissionsAndProceed(user)
                }
            } else {
                if (response?.error?.errorCode == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Check your internet connection and try again.", Toast.LENGTH_LONG).show()
                    launchSignInFlow()
                } else {
                    Toast.makeText(this, "Sign-in failed. Try again.", Toast.LENGTH_LONG).show()
                    launchSignInFlow()
                }
            }
        }
    }

    private fun checkPermissionsAndProceed(user: FirebaseUser) {
        if (arePermissionsGranted()) {
            // Permissions granted, proceed with the app
            navigateToMainActivity(user)
        } else {
            // Permissions not granted, request permissions
            if (permissionDenialCount < 2) {
                requestPermissions()
            } else {
                // Permissions denied twice, show dialog explaining why
                showPermissionsDeniedDialog()
            }
        }
    }

    private fun arePermissionsGranted(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        ActivityCompat.requestPermissions(
            this,
            permissions.toTypedArray(),
            REQUEST_PERMISSIONS
        )
    }

    private fun showPermissionsDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("The app requires camera and storage permissions to work correctly. Please grant these permissions in the app settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                // Open app settings to manually enable permissions
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.parse("package:" + packageName)
                startActivity(intent)
            }
            .setNegativeButton("Exit") { _, _ ->
                // Exit the app if the user denies permissions multiple times
                Toast.makeText(this, "Permissions denied multiple times. Exiting the app.", Toast.LENGTH_LONG).show()
                finish()
            }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            val allPermissionsGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allPermissionsGranted) {
                auth.currentUser?.let {
                    navigateToMainActivity(it)
                }
            } else {
                permissionDenialCount++
                if (permissionDenialCount < 2) {
                    Toast.makeText(this, "Permissions denied. Please allow permissions.", Toast.LENGTH_LONG).show()
                    requestPermissions()
                } else {
                    showPermissionsDeniedDialog()
                }
            }
        }
    }

    private fun navigateToMainActivity(user: FirebaseUser) {
        val preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isFirstRun = preferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            startActivity(Intent(this, WelcomeActivity::class.java))

            // Finish LoginActivity to prevent stacking
            finish()
            return
        }

        if (intent.getBooleanExtra("logout", false)) {
            startActivity(Intent(this, SettingsActivity::class.java))
        } else {
            startActivity(Intent(this, DiaryActivity::class.java))
        }

        finish()
    }
}
