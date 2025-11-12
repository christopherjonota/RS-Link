package com.example.rs_link.data.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.rs_link.data.model.User
import kotlinx.coroutines.tasks.await


interface UserRepository {

    // --- Authentication Actions ---

    // Signs the user in with Email/Password
    suspend fun login(email: String, password: String)

    // Creates an account AND saves the profile to Firestore
    suspend fun registerUser(user: User, password: String)

    // Signs the user out
    fun logout()

    // Checks if a user is currently logged in (useful for Splash Screen)
    fun isUserLoggedIn(): Boolean

    // Gets the current User's ID (UID)
    fun getCurrentUserId(): String?

    // --- Data Actions (Firestore) ---

    // Fetches the full profile (Name, Birthday, etc.) from Firestore
    suspend fun getUserProfile(userId: String): User?

    // Updates the profile (e.g., user changes their phone number)
    suspend fun updateUserProfile(user: User)
}