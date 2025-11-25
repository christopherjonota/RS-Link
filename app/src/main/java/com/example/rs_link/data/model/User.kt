package com.example.rs_link.data.model

data class User(
    val id: String = "", // Will be filled by Firebase/Backend later
    val firstName: String = "",
    val lastName: String = "",

    val birthDate: Long = 0L,
    val contactNumber: String = "",
    val email: String = ""
)
