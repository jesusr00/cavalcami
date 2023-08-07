package com.smartestidea.cavalcami.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultField(
    value: String,
    labelRes: Int,
    icon: ImageVector,
    isEmpty: Boolean = true,
    onValueChange: (value: String) -> Unit,
    trailingIcon: ImageVector? = null,
    onTrailingIconPress:()->Unit ={},
    trailingIconDescRes: Int? =null ,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
){
    OutlinedTextField(value = value, onValueChange = {onValueChange(it)}, label = {
        Text(text = stringResource(id = labelRes), modifier = Modifier.alpha(0.5f))
    }, leadingIcon = {
        Icon(imageVector = icon, contentDescription = stringResource(id = labelRes))
    }, shape = CircleShape, colors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
    ), singleLine = true, isError = isEmpty && value.isEmpty(),
        trailingIcon = { if(trailingIcon != null) IconButton(onClick = onTrailingIconPress) {
            Icon(imageVector = trailingIcon, contentDescription = if(trailingIconDescRes != null) stringResource(trailingIconDescRes) else null )
        } }
        , modifier = modifier.fillMaxWidth(0.9f),
    keyboardActions = keyboardActions, keyboardOptions = keyboardOptions)
}