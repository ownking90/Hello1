package com.smartmate.ai.data.auth

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

data class AppUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?
)

private fun FirebaseUser.toAppUser() = AppUser(
    uid = uid,
    displayName = displayName,
    email = email,
    photoUrl = photoUrl?.toString()
)

/**
 * Wraps Firebase Authentication for email/password and Google sign-in.
 * When the app has not been configured with a real `google-services.json`
 * (e.g. in local development), Firebase is unavailable and this repository
 * safely reports "signed out" instead of crashing, so the rest of the UI
 * remains usable.
 */
class AuthRepository {

    private val auth: FirebaseAuth? = runCatching {
        if (FirebaseApp.getApps(com.smartmate.ai.SmartMateApp.appContext()).isNotEmpty()) {
            FirebaseAuth.getInstance()
        } else {
            null
        }
    }.getOrNull()

    val isCloudAuthAvailable: Boolean get() = auth != null

    fun currentUser(): AppUser? = auth?.currentUser?.toAppUser()

    suspend fun signUp(name: String, email: String, password: String): Result<AppUser> = runCatching {
        val firebaseAuth = auth ?: error("Firebase Authentication is not configured")
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: error("Sign up failed")
        if (name.isNotBlank()) {
            val update = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(update).await()
        }
        user.toAppUser()
    }

    suspend fun login(email: String, password: String): Result<AppUser> = runCatching {
        val firebaseAuth = auth ?: error("Firebase Authentication is not configured")
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        (result.user ?: error("Login failed")).toAppUser()
    }

    suspend fun loginWithGoogleIdToken(idToken: String): Result<AppUser> = runCatching {
        val firebaseAuth = auth ?: error("Firebase Authentication is not configured")
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        (result.user ?: error("Google sign-in failed")).toAppUser()
    }

    fun logout() {
        auth?.signOut()
    }
}
