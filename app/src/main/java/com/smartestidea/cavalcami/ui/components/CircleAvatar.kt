package com.smartestidea.cavalcami.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.File

@Composable
fun CircleAvatar(size:Dp, user: ParseUser?){
    var file: File? by rememberSaveable {
        mutableStateOf(null)
    }
    user?.fetchIfNeededInBackground<ParseObject>{ u,e->
        if(e==null) file = u.getParseFile()
    }
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data= (user?.fetchIfNeededInBackground { u, e -> u.getParseFile("profile_photo"))?.file })
            .build()
    )
    Surface(shape = CircleShape, modifier = Modifier.size(size)) {
        Image(painter = painter, contentDescription = "profile_photo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}