package com.example.rs_link.data.repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.rs_link.data.model.User
import kotlinx.coroutines.tasks.await


interface UserRepository {
    // Example using Firebase Authentication and Firestore
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Registers a user with email/password and saves their details.
     * Throws an exception on failure.
     */
    suspend fun registerUser(user: User, password: String) {
        // Step 1: Create the user in Firebase Authentication
        val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
        val firebaseUser = authResult.user ?: throw Exception("Failed to create user account.")

        // Step 2: Save the additional user details to Firestore
        firestore.collection("users").document(firebaseUser.uid)
            .set(user)
            .await()
    }
}