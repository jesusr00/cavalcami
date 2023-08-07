package com.smartestidea.cavalcami.ui.components.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.smartestidea.cavalcami.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableText(value:String, label:Any, fontSize: TextUnit = 16.sp, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, onValueChange:(value:String)->Unit){
    BasicTextField(value = value, onValueChange = onValueChange, textStyle = TextStyle.Default.copy(
        fontSize= fontSize,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.onBackground,
    ), singleLine = true, keyboardOptions = keyboardOptions)
}