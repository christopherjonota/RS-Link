package com.example.rs_link.feature_dashboard.safety

import android.widget.Space
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SafetyScreen() {
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
                SafetyScreenItem("Crash Detection & Alert")
                Spacer(Modifier.height(20.dp))
                SafetyScreenItem("Emergency Contacts", "0 contacts added")
            }


        }
    }

}


@Composable
fun SafetyScreenItem(title: String? = null, label: String? = null){

    Card (
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = {}
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
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
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
                imageVector = Icons.Default.Person,
                contentDescription = null
            )



        }
    }
}