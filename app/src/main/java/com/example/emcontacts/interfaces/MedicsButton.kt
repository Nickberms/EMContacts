package com.example.emcontacts.interfaces

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emcontacts.R

@Composable
fun MedicsButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_medic),
                        contentDescription = null,
                        modifier = Modifier
                            .height(48.dp)
                            .width(48.dp)
                            .padding(8.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                Text(
                    text = "Medics",
                    color = Color.White,
                    style = LocalTextStyle.current,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}