package com.smartestidea.cavalcami.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.smartestidea.cavalcami.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultField(value:String, labelRes:Int, icon: ImageVector, isEmpty:Boolean=true, onValueChange:(value:String)->Unit){
    OutlinedTextField(value = value, onValueChange = {onValueChange(it)}, label = {
        Text(text = stringResource(id = labelRes), modifier = Modifier.alpha(0.5f))
    }, leadingIcon = {
        Icon(imageVector = icon, contentDescription = stringResource(id = labelRes))
    }, shape = CircleShape, colors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
    ), singleLine = true, isError = isEmpty && value.isEmpty(), modifier = Modifier.fillMaxWidth(0.9f))
}