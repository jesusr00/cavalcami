package com.smartestidea.cavalcami.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.ui.components.ImagePicker
import com.smartestidea.cavalcami.ui.components.fields.DefaultField
import com.smartestidea.cavalcami.ui.components.fields.EmailField
import com.smartestidea.cavalcami.ui.components.fields.PhoneField
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ProfileScreen() {
    val snackBarHostState = SnackbarHostState()
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val user = ParseUser.getCurrentUser()

    var username by rememberSaveable { mutableStateOf(user.username) }
//    var userPhoneNumber by rememberSaveable { mutableStateOf(user.get()) }
    var firstEmergencyNumber by rememberSaveable { mutableStateOf("") }
    var secondEmergencyNumber by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    val profilePhoto = File(ctx.filesDir,"profile_photo.jpg")
    val frontCI = File(ctx.filesDir, "front_ci.jpg")
    val backCI = File(ctx.filesDir, "back_ci.jpg")

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState){
            Snackbar(snackbarData = it, containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error, actionColor = MaterialTheme.colorScheme.onErrorContainer)
        }
    }, modifier = Modifier.imePadding()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .animateContentSize()
                .verticalScroll(state = scrollState, enabled = true, reverseScrolling = true),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            ImagePicker(label = null, circleShape = true, file =  profilePhoto ,modifier = Modifier.size(100.dp))
            DefaultField(value = username, labelRes = R.string.username, icon = Icons.Rounded.AccountCircle){ username = it }
            EmailField(value = email, label = R.string.email){ email = it }
//            PhoneField(value = userPhoneNumber, label = R.string.phone_number){ userPhoneNumber = it}
            PhoneField(value = firstEmergencyNumber, label = R.string.first_emergency_phone_number){ firstEmergencyNumber = it}
            PhoneField(value = secondEmergencyNumber, label = R.string.second_emergency_phone_number){ secondEmergencyNumber = it}
            Row(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImagePicker(label = R.string.front_ci,file= frontCI, modifier = Modifier.fillMaxSize().weight(1f))
                Spacer(Modifier.width(10.dp))
                ImagePicker(label=R.string.back_ci,file=backCI, modifier = Modifier.fillMaxSize().weight(1f))
            }
        }
    }
}