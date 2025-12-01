package com.example.rs_link.feature_dashboard.safety

import android.media.Image
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.rs_link.R



@Composable
fun SafetyScreen(
    viewModel: SafetyViewModel = hiltViewModel(),
    onNavigateToEmergencyContact: () -> Unit,
    onNavigateToCrashAlert: ()-> Unit
) {

    // 1. Observe the count
    val contactCount by viewModel.contactCount.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
    ){

        Column (modifier = Modifier.fillMaxSize().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .height(80.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ){
                Text(
                    text = "Riding Safety",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            Column(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            ) {
                SafetyScreenItem("Crash Detection & Alert", iconLeft = R.drawable.icon_alert, onClick = onNavigateToCrashAlert)
                Spacer(Modifier.height(20.dp))
                SafetyScreenItem("Emergency Contacts", "$contactCount contacts added", R.drawable.icon_contact, onNavigateToEmergencyContact)
            }


        }
    }

}

@Composable
fun SafetyScreenItem(title: String? = null, label: String? = null, iconLeft: Int? = null, onClick:() -> Unit){

    Card (
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary )
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(iconLeft != null){
                    Image(
                        painter = painterResource(id = iconLeft),
                        contentDescription = null
                    )
                    Spacer(Modifier.width(12.dp))
                }



                if (title != null){
                    Column {
                        Spacer(Modifier.width(20.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge
                        )
                        if(label != null){
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}