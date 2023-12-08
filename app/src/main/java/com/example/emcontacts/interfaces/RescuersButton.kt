package com.example.emcontacts.interfaces

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
fun RescuersButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth(2.5f)
                .height(350.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rescue),
                    contentDescription = null,
                    modifier = Modifier
                        .height(48.dp)
                        .width(48.dp)
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rescuers",
                    color = Color.White,
                    style = LocalTextStyle.current,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}