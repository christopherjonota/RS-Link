package com.example.rs_link.data.model

data class CrashEvent(
    val id: String = "",
    val timestamp: Long = System.currentTimeMillis(), // When it happened
    val message: String = "", // e.g. "High Impact Detected"
    val location: String = "" // Optional: "8F94+6F Quezon City"
)
