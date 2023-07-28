package com.smartestidea.cavalcami.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.smartestidea.cavalcami.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(value:String, labelRes:Int, isEmpty:Boolean=true,isLessThan8:Boolean=true, onValueChange:(value:String)->Unit){
    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }
    OutlinedTextField(value = value, onValueChange = { onValueChange(it) }, label = {
        Text(text = stringResource(id = labelRes), modifier = Modifier.alpha(0.5f))
    }, leadingIcon = {
        Icon(painter = painterResource(id = R.drawable.baseline_https_24), contentDescription = stringResource(id = labelRes))
    }, shape = CircleShape, keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password
    ), colors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
    ), singleLine = true, visualTransformation = if(isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(painter = painterResource(id = if(isPasswordVisible) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24), contentDescription = stringResource(
                    id = R.string.change_password_visibility
                ) )
            }
        }, isError = (isEmpty && value.isEmpty()) || (isLessThan8 && value.length<8) ,modifier = Modifier.fillMaxWidth(0.9f))
}