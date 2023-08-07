package com.smartestidea.cavalcami.ui.screens

import android.location.Address
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.data.model.toTrip
import com.smartestidea.cavalcami.ui.components.MapPicker
import com.smartestidea.cavalcami.ui.components.TripView
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.theme.Success
import com.smartestidea.cavalcami.ui.viewmodels.TripViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.util.GeoPoint
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTripScreen(navController: NavHostController, tripViewModel: TripViewModel) {
    val snackBarHostState = SnackbarHostState()
    val parseTrip by tripViewModel.parseTrip.collectAsState()

    var startAddress:Address? by rememberSaveable {
        mutableStateOf(null)
    }
    var onSelectStartAddress by rememberSaveable {
        mutableStateOf(false)
    }
    var isLoadingStartAddress by rememberSaveable {
        mutableStateOf(false)
    }
    var onSelectDestAddress by rememberSaveable {
        mutableStateOf(false)
    }
    var destAddress:Address? by rememberSaveable {
        mutableStateOf(null)
    }
    var isLoadingDestAddress by rememberSaveable {
        mutableStateOf(false)
    }
    var passengers by rememberSaveable {
        mutableStateOf(1)
    }
    var suitcases by rememberSaveable {
        mutableStateOf(0)
    }
    LaunchedEffect(Unit){
        if(parseTrip!=null){
            val trip = parseTrip!!.toTrip()
            isLoadingStartAddress = true
            getAddress(trip.startGeoPoint){address ->
                startAddress = address
                isLoadingStartAddress = false
            }
            isLoadingDestAddress = true
            getAddress(trip.endGeoPoint){address ->
                destAddress = address
                isLoadingDestAddress = false
            }
            passengers = trip.passengers
            suitcases = trip.suitcases
        }
    }

    val ctx = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val uiState by tripViewModel.mainUIState.collectAsState()
    when(uiState ) {
        is MainUIState.Error -> displaySb(snackBarHostState, (uiState as MainUIState.Error).errorMsgRes, scope, ctx)
        MainUIState.Success -> displaySb(snackBarHostState, R.string.request_complete, scope, ctx)
        else -> {}
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.reservation_request)) },navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))
            }
        })
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState){
                Snackbar(snackbarData = it, containerColor = if(uiState == MainUIState.Success) Success else MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error, actionColor = MaterialTheme.colorScheme.onErrorContainer)
            }
        }, modifier = Modifier.imePadding()
    ) {innerPadding->
        Column(modifier= Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(state = scrollState, enabled = true, reverseScrolling = true),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)){

            Card(modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(200.dp)) {
                TripView(start = startAddress, dest = destAddress)
            }

            ElevatedButton(onClick = { onSelectStartAddress = true }, modifier = Modifier
                .animateContentSize()
                .fillMaxWidth(0.9f), enabled = !isLoadingStartAddress) {
                if(isLoadingStartAddress){
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.dp)
                }else{
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null)
                        Text(text = if(startAddress==null) stringResource(id = R.string.press_to_select_a_start_location) else startAddress!!.extras.getString("display_name")?:startAddress!!.thoroughfare, fontWeight = FontWeight.Bold)
                    }
                }
            }
            ElevatedButton(onClick = { onSelectDestAddress = true },
                enabled = !isLoadingDestAddress,
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth(0.9f)) {
                if(isLoadingDestAddress){
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.dp)
                }else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null)
                        Text(
                            text = if (destAddress == null) stringResource(id = R.string.press_to_select_a_dest_location) else destAddress!!.extras.getString(
                                "display_name"
                            ) ?: destAddress!!.thoroughfare, fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Divider(modifier= Modifier.fillMaxWidth())
            Text(text = stringResource(id = R.string.passengers), fontWeight = FontWeight.Bold,  modifier= Modifier.fillMaxWidth(0.9f))
            Card(modifier= Modifier.fillMaxWidth(0.9f)) {
                Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if(passengers>1) passengers--  }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = null)
                    }
                    Text(text = "$passengers", fontSize = 20.sp, fontWeight = FontWeight.Bold )
                    IconButton(onClick = { passengers++  }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
            Text(text = stringResource(id = R.string.suitcases), fontWeight = FontWeight.Bold,  modifier= Modifier.fillMaxWidth(0.9f))
            Card(modifier= Modifier.fillMaxWidth(0.9f)) {
                Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if(suitcases>0) suitcases--  }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowLeft, contentDescription = null)
                    }
                    Text(text = "$suitcases", fontSize = 20.sp, fontWeight = FontWeight.Bold )
                    IconButton(onClick = { suitcases++  }) {
                        Icon(imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }
            Divider(modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                if(parseTrip == null) {
                    tripViewModel.saveTrip(
                        startAddress = if (startAddress != null) startAddress!!.extras.getString("display_name")
                            ?: startAddress!!.thoroughfare else "",
                        destAddress = if (destAddress != null) destAddress!!.extras.getString("display_name")
                            ?: destAddress!!.thoroughfare else "",
                        client = ParseUser.getCurrentUser(),
                        driver = null,
                        passengers = passengers,
                        suitcases = suitcases,
                        startGeoPoint = if (startAddress != null) GeoPoint(
                            startAddress!!.latitude,
                            startAddress!!.longitude
                        ) else null,
                        destGeoPoint = if (destAddress != null) GeoPoint(
                            destAddress!!.latitude,
                            destAddress!!.longitude
                        ) else null
                    )
                }else{
                    tripViewModel.changeTrip(startAddress,destAddress,passengers,suitcases,)
                }
            }, modifier = Modifier.fillMaxWidth(0.9f), colors = ButtonDefaults.buttonColors(
                containerColor =  MaterialTheme.colorScheme.tertiary,
            ), enabled = uiState != MainUIState.Loading && !isLoadingDestAddress && !isLoadingStartAddress) {
                if(uiState != MainUIState.Loading) Text(text = stringResource(id = R.string.request), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp), color = Color.White)
                else CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.dp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
    if(onSelectStartAddress){
        MapPicker{ gP->
            if(gP!=null) {
                isLoadingStartAddress = true
                getAddress(gP){address->
                    startAddress = address
                    isLoadingStartAddress = false
                }
            }
            onSelectStartAddress = false
        }
        BackHandler{
            onSelectStartAddress = false
        }
    }
    if(onSelectDestAddress){
        MapPicker{ gP->
            if(gP!=null) {
                isLoadingDestAddress = true
                getAddress(gP){address->
                    destAddress = address
                    isLoadingDestAddress = false
                }
            }
            onSelectDestAddress = false
        }
        BackHandler{
            onSelectDestAddress = false
        }
    }
}

fun getAddress (geoPoint: GeoPoint, onError:()->Unit= {}, onSuccess:(Address)->Unit){
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable->
        throwable.printStackTrace()
        onError()
    }
    CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
        val geocoder = GeocoderNominatim(Locale.getDefault(), USER_AGENT)
        val result = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
        onSuccess(result[0])
    }
}
