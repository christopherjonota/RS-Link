package com.example.rs_link.feature_dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import com.example.rs_link.core.routing.RouterActivity
import com.example.rs_link.core.ui.theme.ThemeRSLink


@AndroidEntryPoint
class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            ThemeRSLink {
                DashboardScreen(
                    onLogout = {
                        // Handle Logout Navigation here
                        startActivity(Intent(this, RouterActivity::class.java))
                        finish()
                    },

                )
            }
        }
    }
}