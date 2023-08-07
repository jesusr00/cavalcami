package com.smartestidea.cavalcami.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.ui.components.ImagePicker
import com.smartestidea.cavalcami.ui.components.ImgButton
import com.smartestidea.cavalcami.ui.components.fields.DefaultField
import com.smartestidea.cavalcami.ui.components.fields.EmailField
import com.smartestidea.cavalcami.ui.components.fields.PasswordField
import com.smartestidea.cavalcami.ui.components.fields.PhoneField
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(userViewModel: UserViewModel) {
    val snackBarHostState = SnackbarHostState()
    val ctx = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var username by rememberSaveable { mutableStateOf("") }
    var userPhoneNumber by rememberSaveable {mutableStateOf("") }
    var firstEmergencyNumber by rememberSaveable {mutableStateOf("")}
    var secondEmergencyNumber by rememberSaveable { mutableStateOf("")}
    var email by rememberSaveable {mutableStateOf("") }
    var emailOrUsername by rememberSaveable {mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("")}
    var repeatPassword by rememberSaveable { mutableStateOf("") }
    var isDriver by rememberSaveable {mutableStateOf(false) }
    var isRegister by rememberSaveable { mutableStateOf(false) }

    val profilePhoto = File(ctx.filesDir,"profile_photo.jpg")
    val frontCI = File(ctx.filesDir, "front_ci.jpg")
    val backCI = File(ctx.filesDir, "back_ci.jpg")
    if(profilePhoto.exists()) profilePhoto.delete()
    if(frontCI.exists()) frontCI.delete()
    if(backCI.exists()) backCI.delete()

    val uiState by userViewModel.mainUIState.collectAsState()
    if(uiState is MainUIState.Error) displaySb(snackBarHostState, (uiState as MainUIState.Error).errorMsgRes, scope, ctx)

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackBarHostState){
            Snackbar(snackbarData = it, containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error, actionColor = MaterialTheme.colorScheme.onErrorContainer)
        }
    }, modifier = Modifier.imePadding()){innerPadding->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .animateContentSize()
            .verticalScroll(state = scrollState, enabled = true, reverseScrolling = true), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                , verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    Text(text = "CAVAL", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(text = "CAMI", color = MaterialTheme.colorScheme.secondary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                }
                Text(text = stringResource(id = R.string.sing_in_up_desc), Modifier.alpha(0.8f), textAlign = TextAlign.Center)
            }
            AnimatedVisibility(visible = !isRegister) {
                Row(
                    modifier = Modifier
                        .heightIn(max = 100.dp)
                        .fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ImgButton(
                        stringRes = R.string.client,
                        painterRes = R.drawable.cash_svgrepo_com,
                        isActive = !isDriver
                    ) {
                        isDriver = false
                    }
                    Spacer(Modifier.width(10.dp))
                    ImgButton(
                        stringRes = R.string.driver,
                        painterRes = R.drawable.driver_svgrepo_com,
                        isActive = isDriver
                    ) {
                        isDriver = true
                    }
                }
            }
            AnimatedVisibility(isRegister){
                ImagePicker(label = null, circleShape = true,file = profilePhoto, modifier= Modifier.size(100.dp))
            }
            AnimatedVisibility(isRegister){
                DefaultField(
                    value = username,
                    labelRes = R.string.username,
                    icon = Icons.Rounded.AccountCircle,
                    onValueChange = {
                        username = it
                    },
                )
            }

            EmailField(value = if(!isRegister) emailOrUsername else email,
                label = if(!isRegister) "${ stringResource(id = R.string.email) } ${ stringResource(id = R.string.or) } ${stringResource(id = R.string.username).lowercase(Locale.getDefault())}" else R.string.email, isEmail = isRegister){
                emailOrUsername = it
                email = it
            }

            PasswordField(value = password, labelRes = R.string.password) {
                password = it
            }
            AnimatedVisibility(isRegister){
                PasswordField(value = repeatPassword, labelRes = R.string.repeat_password) {
                    repeatPassword = it
                }
            }
            AnimatedVisibility(visible = isRegister) {
                PhoneField(value = userPhoneNumber, label = R.string.phone_number){
                    userPhoneNumber = it
                }
            }
            AnimatedVisibility(visible = isRegister) {
                PhoneField(value = firstEmergencyNumber, label = R.string.first_emergency_phone_number){
                    firstEmergencyNumber = it
                }
            }
            AnimatedVisibility(visible = isRegister) {
                PhoneField(value = secondEmergencyNumber, label = R.string.second_emergency_phone_number){
                    secondEmergencyNumber = it
                }
            }
            AnimatedVisibility(visible = isRegister) {
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
            }

            AnimatedVisibility(visible = !isRegister,modifier= Modifier
                .align(Alignment.End)
                .padding(
                    end = LocalConfiguration.current.screenWidthDp.dp * 0.1f,
                    bottom = 5.dp
                )) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(0.8f)
                )
            }
            Button(onClick = {
                if(!isRegister) {
                    userViewModel.login(emailOrUsername, password)
                }else{
                    userViewModel.signUp(
                        username,
                        email,
                        password,
                        repeatPassword,
                        userPhoneNumber,
                        firstEmergencyNumber,
                        secondEmergencyNumber,
                        profilePhoto,
                        frontCI,
                        backCI
                    )
                }
            }, modifier = Modifier.fillMaxWidth(0.9f), colors = ButtonDefaults.buttonColors(
                containerColor =  MaterialTheme.colorScheme.tertiary,
            ), enabled = uiState != MainUIState.Loading) {
                if(uiState != MainUIState.Loading)
                    Text(text = stringResource(id = if(!isRegister) R.string.login else R.string.sign_up), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp), color = Color.White)
                else
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.dp)
            }
            AnimatedVisibility(visible = !isRegister) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.dont_have_an_account),
                        Modifier.alpha(0.8f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Text(
                        text = stringResource(id = R.string.sign_up),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            isRegister = true
                        })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            BackHandler(onBack = {isRegister = false}, enabled = isRegister)
        }
    }
}

fun displaySb(
    snackBarHostState: SnackbarHostState,
    msgRes: Int,
    scope: CoroutineScope,
    ctx: Context
) {
    Log.e("ERROR", ctx.getString(msgRes))
    scope.launch {
        snackBarHostState.showSnackbar(
            message = ctx.getString(msgRes),
            actionLabel = ctx.getString(R.string.close),
            duration = SnackbarDuration.Short
        )
    }
}



