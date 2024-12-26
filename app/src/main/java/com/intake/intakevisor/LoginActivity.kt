package com.intake.intakevisor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Check if the activity was opened via a magic link or if the user needs to be signed in
        verifyAccountExists()
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
                    navigateToMainActivity(user)
                }
            } else {
                Toast.makeText(this, "Sign-in failed. Try again.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToMainActivity(user: FirebaseUser) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun verifyAccountExists() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Account exists but not signed in, validate if they need to sign in again
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Token is valid, navigate to main activity
                        navigateToMainActivity(currentUser)
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
}
