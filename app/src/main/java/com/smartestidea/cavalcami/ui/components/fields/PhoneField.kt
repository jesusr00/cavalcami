package com.smartestidea.cavalcami.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Phone
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
import androidx.compose.ui.text.style.TextOverflow
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.isValidPhoneNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneField(value:String, label:Any,isEmpty:Boolean = true, isPhoneNumber: Boolean = true,onValueChange:(value:String)->Unit){
    val text = if(label is Int) stringResource(id = label) else label.toString()
    OutlinedTextField(value = value, onValueChange = {onValueChange(it)}, label = {
        Text(text = text, maxLines = 1, overflow = TextOverflow.Ellipsis,modifier = Modifier
            .alpha(0.5f))
    }, placeholder = {
        Text(text = stringResource(id = R.string.with_country_code_example), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier
            .alpha(0.5f))
    }, leadingIcon = {
        Icon(imageVector = Icons.Rounded.Phone, contentDescription = text)
    }, shape = CircleShape, keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Phone
    ), colors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
    ), singleLine = true, isError = ( (isEmpty && value.isEmpty()) ||  (isPhoneNumber && !isValidPhoneNumber(value))), modifier = Modifier.fillMaxWidth(0.9f))
}