package com.smartestidea.cavalcami.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.data.model.toClient
import com.smartestidea.cavalcami.ui.components.ImagePicker
import com.smartestidea.cavalcami.ui.components.fields.EditableText
import com.smartestidea.cavalcami.ui.components.fields.PhoneField
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.theme.Success
import com.smartestidea.cavalcami.ui.viewmodels.UserViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val snackBarHostState = SnackbarHostState()
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val user = ParseUser.getCurrentUser().toClient()

    var username by rememberSaveable { mutableStateOf(user.username) }
    var userPhoneNumber by rememberSaveable { mutableStateOf(user.phoneNumber) }
    var firstEmergencyNumber by rememberSaveable { mutableStateOf(user.egyPhoneNumber) }
    var secondEmergencyNumber by rememberSaveable { mutableStateOf(user.otherEgyPhoneNumber) }
    var email by rememberSaveable { mutableStateOf(user.email) }

    val profilePhoto = user.profilePhoto?.file ?: File(ctx.filesDir,"profile_photo.jpg")
    val frontCI = user.frontCI?.file ?: File(ctx.filesDir, "front_ci.jpg")
    val backCI = user.backCI?.file ?: File(ctx.filesDir, "back_ci.jpg")

    val uiState by userViewModel.mainUIState.collectAsState()
    when(uiState ) {
        is MainUIState.Error -> displaySb(snackBarHostState, (uiState as MainUIState.Error).errorMsgRes, scope, ctx)
        MainUIState.Success -> displaySb(snackBarHostState, R.string.user_saved, scope, ctx)
        else -> {}
    }
    Scaffold(
        topBar = { TopAppBar(title = {Text(text = stringResource(id = R.string.edit_profile))},navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))
            }
        })},
        snackbarHost = {
        SnackbarHost(hostState = snackBarHostState){
            Snackbar(snackbarData = it, containerColor = if(uiState == MainUIState.Success) Success else MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error, actionColor = MaterialTheme.colorScheme.onErrorContainer)
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
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                ImagePicker(label = null, circleShape = true, file =  profilePhoto ,modifier = Modifier.size(100.dp))
                Column(verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.height(IntrinsicSize.Min)) {
                    EditableText(value = username, label = R.string.username){ username = it}
                    EditableText(value = email, label = R.string.username, fontSize = 12.sp, keyboardOptions = KeyboardOptions.Default.copy(
                         keyboardType = KeyboardType.Email
                    )){ email = it }
                    Text(
                         text = stringResource(id = R.string.change_password),
                         textAlign = TextAlign.Center,
                         color = MaterialTheme.colorScheme.primary,
                         fontSize = 12.sp,
                         fontWeight = FontWeight.Bold)
                }
            }
            PhoneField(value = userPhoneNumber, label = R.string.phone_number){ userPhoneNumber = it}
            PhoneField(value = firstEmergencyNumber, label = R.string.first_emergency_phone_number){ firstEmergencyNumber = it}
            PhoneField(value = secondEmergencyNumber, label = R.string.second_emergency_phone_number){ secondEmergencyNumber = it}
            Row(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ImagePicker(label = R.string.front_ci,file= frontCI, modifier = Modifier
                    .fillMaxSize()
                    .weight(1f))
                Spacer(Modifier.width(10.dp))
                ImagePicker(label=R.string.back_ci,file=backCI, modifier = Modifier
                    .fillMaxSize()
                    .weight(1f))
            }
            Button(onClick = {
                userViewModel.saveUser(username,email,userPhoneNumber,firstEmergencyNumber,secondEmergencyNumber,profilePhoto,frontCI,backCI)
            }, modifier = Modifier.fillMaxWidth(0.9f), colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            ), enabled = uiState != MainUIState.Loading) {
                if(uiState != MainUIState.Loading)
                    Text(text = stringResource(id = R.string.save), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp), color = Color.White)
                else
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.dp)
            }
        }
    }
}