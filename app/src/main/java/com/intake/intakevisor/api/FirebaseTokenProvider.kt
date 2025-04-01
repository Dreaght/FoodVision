package com.intake.intakevisor.api

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object FirebaseTokenProvider {
    fun getToken(): String? {
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        return runBlocking(Dispatchers.IO) {
            user.getIdToken(false).await().token
        }
    }
}
