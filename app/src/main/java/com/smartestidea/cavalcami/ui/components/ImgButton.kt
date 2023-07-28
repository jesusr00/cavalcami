package com.smartestidea.cavalcami.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.smartestidea.cavalcami.R

@Composable
fun ImgButton(stringRes: Int, painterRes:Int,isActive:Boolean,onClick:()->Unit){
    Button(onClick = onClick, shape = CircleShape, colors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent
    )) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(id = painterRes), contentDescription = stringResource(id = R.string.client)
                , colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                    setToSaturation(if(isActive)1f else 0f)
                }), modifier= Modifier
                    .weight(1f)
                    .alpha(if (isActive) 1f else 0.5f))
            Text(text = stringResource(id = stringRes), fontWeight = FontWeight.Bold, color =if(isActive) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.alpha(if(isActive)1f else 0.5f))
        }
    }
}