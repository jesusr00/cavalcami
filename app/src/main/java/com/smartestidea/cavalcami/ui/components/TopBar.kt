package com.smartestidea.cavalcami.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.parse.ParseFile
import com.parse.ParseUser

@Composable
fun TopBar(modifier: Modifier = Modifier, onNavIconPress: () -> Unit) {
    val user = ParseUser.getCurrentUser()
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data= (user?.get("profile_photo") as ParseFile?)?.file)
            .build()
    )
    val ibContColor = MaterialTheme.colorScheme.tertiary
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
        SmallFloatingActionButton(onClick = onNavIconPress, shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface,modifier = Modifier) {
            Icon(imageVector = Icons.Rounded.Menu, contentDescription = "menu")
        }
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .size(60.dp)
            .padding(10.dp)
            .shadow(5.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)) {
            Surface(shape = CircleShape) {
                Image(painter = painter, contentDescription = "profile_photo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
    }
}