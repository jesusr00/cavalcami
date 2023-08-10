package com.smartestidea.cavalcami.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.parse.ParseFile
import java.io.File

@Composable
fun ParseImageView(parseFile: ParseFile?, modifier: Modifier = Modifier){
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data= parseFile?.data)
            .build()
    )
    Image(painter = painter, contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
}