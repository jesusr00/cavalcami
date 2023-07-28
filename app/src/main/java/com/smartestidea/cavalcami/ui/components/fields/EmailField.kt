package com.smartestidea.cavalcami.ui.components.fields

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.isValidEmail
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(value:String, label:Any, isEmpty:Boolean = true, isEmail:Boolean = true, onValueChange:(value:String)->Unit){
    val text = if(label is Int) stringResource(id = label) else label.toString()
    OutlinedTextField(value = value, onValueChange = {onValueChange(it)}, label = {
        Text(text = text, modifier = Modifier
            .alpha(0.5f))
    }, leadingIcon = {
        Icon(imageVector = Icons.Rounded.Email, contentDescription = text)
    }, shape = CircleShape, keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Email
    ), colors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
    ), singleLine = true, isError = (isEmpty && value.isEmpty()) || (isEmail && !isValidEmail(value)), modifier = Modifier.fillMaxWidth(0.9f))
}

