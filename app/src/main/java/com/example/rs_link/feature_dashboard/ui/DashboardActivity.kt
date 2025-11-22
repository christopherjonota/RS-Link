package com.example.rs_link.feature_dashboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rs_link.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.compose.setContent
import com.example.rs_link.core.routing.RouterActivity
import com.example.rs_link.core.ui.theme.ThemeRSLink
import com.example.rs_link.feature_dashboard.ui.DashboardNavigation
import com.example.rs_link.feature_dashboard.ui.DashboardScreen


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