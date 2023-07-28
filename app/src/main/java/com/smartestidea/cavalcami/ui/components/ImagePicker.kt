package com.smartestidea.cavalcami.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.smartestidea.cavalcami.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePicker(label: Any?, modifier: Modifier = Modifier, circleShape:Boolean = false, onSelect:(uri:Uri?)->Unit){
    val text = if(label==null) "" else if(label is Int) stringResource(id = label) else label.toString()
    var photoUri: Uri? by rememberSaveable{
        mutableStateOf(null)
    }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()){
        photoUri = it
        onSelect(it)
    }
    OutlinedCard(onClick = {
        launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
    }, modifier = modifier, shape = if(circleShape) CircleShape else CardDefaults.outlinedShape) {
        Box(modifier = Modifier.fillMaxSize()) {
            if(photoUri!=null){
                val painter = rememberAsyncImagePainter(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(data= photoUri)
                        .build()
                )
                Image(painter = painter, contentDescription = stringResource(id = R.string.front_ci),colorFilter = ColorFilter.colorMatrix(
                    ColorMatrix().apply {
                        setToSaturation(0f)
                    }), modifier= Modifier
                    .alpha(0.3f)
                    .fillMaxSize())
            }
            if(text.isNotEmpty()){
                Text(text = text, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}