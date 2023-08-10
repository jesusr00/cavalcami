package com.smartestidea.cavalcami.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.parse.Parse
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.getEstimatePrice
import com.smartestidea.cavalcami.data.model.isDriver
import com.smartestidea.cavalcami.data.model.toTrip
import com.smartestidea.cavalcami.ui.components.CircleAvatar
import com.smartestidea.cavalcami.ui.components.DrawerDetailContent
import com.smartestidea.cavalcami.ui.components.TopBar
import com.smartestidea.cavalcami.ui.components.TripView
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.theme.Success
import com.smartestidea.cavalcami.ui.viewmodels.TripViewModel
import com.smartestidea.cavalcami.ui.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
interface Screen{
    val id:String
    val labelRes:Int
}
object Home:Screen{
    override val id: String
        get() = "home"
    override val labelRes: Int
        get() = R.string.home
}
object Help:Screen{
    override val id: String
        get() = "help"
    override val labelRes: Int
        get() = R.string.help
}
object Profile:Screen{
    override val id: String
        get() = "profile"
    override val labelRes: Int
        get() = R.string.edit_profile
}
object NewTrip:Screen{
    override val id: String
        get() = "new_trip"
    override val labelRes: Int
        get() = R.string.new_trip
}

const val USER_AGENT = "CavalCami/1.0"
val tabsSections = listOf(
    Home,Help
)

var myLocationOverlay:MyLocationNewOverlay?=null
var startGeoPoint:GeoPoint? = null
var driverGeoPoint:GeoPoint?=null
var lastCacheLocation:GeoPoint? = null
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CavalCamiApp(
    userViewModel: UserViewModel,
    tripViewModel: TripViewModel,
    setOsmMap: (mapView: MapView) -> Unit
) {
    val permissions = mutableListOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        permissions.add(2,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    }
    val ctx= LocalContext.current
    val allPermissions = rememberMultiplePermissionsState(permissions = permissions)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerDetailState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentStack by navController.currentBackStackEntryAsState()
    val currentScreen = currentStack?.destination?.route
    var center:GeoPoint? by rememberSaveable {
       mutableStateOf(startGeoPoint)
    }
    val tripUiState by tripViewModel.mainUIState.collectAsState()

    val snackBarHostState = SnackbarHostState()
    var isDialogDeleteConfirm by rememberSaveable {
        mutableStateOf(false)
    }
    val user = ParseUser.getCurrentUser()

    val parseTrips by tripViewModel.parseTrips.collectAsState()
    val parseTrip  = parseTrips.getOrNull(0)

    val query = ParseQuery<ParseObject>("Trip")
    if(!user.isDriver()){
        query.whereEqualTo("client",user)
        query.limit = 1
    }


    LaunchedEffect(Unit){
        if(!allPermissions.allPermissionsGranted){
            allPermissions.launchMultiplePermissionRequest()
        }else{
            val locationManager =  ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(location !=null) {
                lastCacheLocation = GeoPoint(location.latitude, location.longitude)
            }
        }
    }

    when(tripUiState ) {
        is MainUIState.Error -> displaySb(
            snackBarHostState,
            (tripUiState as MainUIState.Error).errorMsgRes,
            scope,
            ctx
        )
        is MainUIState.Success -> (tripUiState as MainUIState.Success).successMsgRes?.let {
            displaySb(
                snackBarHostState,
                it, scope, ctx
            )
        }
        else -> {}
    }

    ModalNavigationDrawer(drawerContent = {
        DrawerContent(navController,userViewModel){
            scope.launch {
                drawerState.close()
            }
        }
    }, drawerState= drawerState, gesturesEnabled = drawerState.isOpen) {
        ModalNavigationDrawer(drawerContent = {
            parseTrip?.let { DrawerDetailContent({
                scope.launch {
                    drawerDetailState.close()
                }
                navController.navigate(NewTrip.id)
            },it){
                isDialogDeleteConfirm = true
                scope.launch {
                    drawerDetailState.close()
                }
            } }
        }, drawerState= drawerDetailState, gesturesEnabled = drawerDetailState.isOpen) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState){
                        if(currentScreen != Home.id)
                            Snackbar(
                                snackbarData = it,
                                containerColor = if(tripUiState is MainUIState.Success) Success else MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error,
                                actionColor = MaterialTheme.colorScheme.onErrorContainer,
                            )
                    }
                },
                floatingActionButton = {
                    if(currentScreen == Home.id) {
                        Controllers(parseTrip,tripUiState, navController = navController,
                            onMyLocationPress = {
                                center = myLocationOverlay?.myLocation
                            },onStartLocationPress={
                                center = startGeoPoint
                                scope.launch {
                                    drawerDetailState.open()
                                }
                            }
                        ) {
                            center = driverGeoPoint
                        }
                    }
                }
            ) {innerPadding->
                NavHost(navController = navController, startDestination = tabsSections[0].id,modifier = Modifier.padding(innerPadding)){
                    composable(Home.id){
                        HomeScreen(center= center,parseTrips, onSelectedItem={ trip->
                            scope.launch {
                                drawerDetailState.open()
                            }
                        }, resetCenter= {center = null },setOsmMap = setOsmMap) {
                            scope.launch {
                                drawerState.open()
                            }
                        }

                    }
                    composable(Help.id){
                        HelpScreen(navController)
                    }
                    composable(Profile.id){
                        ProfileScreen(navController,userViewModel)
                    }
                    composable(NewTrip.id){
                        NewTripScreen(navController, tripViewModel)
                    }
                }
            }
        }
    }
    AnimatedVisibility(visible = isDialogDeleteConfirm) {
        ConfirmDeleteDialog({ isDialogDeleteConfirm = false  }){
            isDialogDeleteConfirm = false
            tripViewModel.removeTrip()
        }
    }
}

@Composable
fun ConfirmDeleteDialog(onDismissRequest:()->Unit, onConfirm:()->Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(text = stringResource(id = R.string.are_you_sure), fontWeight = FontWeight.Bold)
                Text(text = stringResource(id = R.string.this_trip_will_be_permanently_removed), fontWeight = FontWeight.Light)
                Divider()
                Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = stringResource(id = android.R.string.cancel))
                    }
                    TextButton(onClick = onConfirm) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }
            }
    }
}


@Composable
fun RowDetailItemContent(row:Pair<Int,String>){
    Text(text = stringResource(id = row.first)+": ", fontWeight = FontWeight.Bold)
    Text(text = row.second, fontWeight = FontWeight.Light, textAlign = TextAlign.Justify)
}


@Composable
fun HomeScreen(center: GeoPoint?,parseTrip:List<ParseObject?>,onSelectedItem:(ParseObject)->Unit, resetCenter:()->Unit, setOsmMap: (mapView: MapView) -> Unit, onNavIconPress: () -> Unit){
    Box(modifier = Modifier
        .fillMaxSize()){
        OsmMap(center,parseTrip,onSelectedItem,resetCenter){
            setOsmMap(it)
        }
        TopBar(onNavIconPress = onNavIconPress,modifier = Modifier.align(Alignment.TopCenter))

    }
}
@Composable
fun Controllers(
    parseTrip: ParseObject?,
    tripUIState: MainUIState,
    navController: NavHostController,
    onMyLocationPress: () -> Unit,
    onStartLocationPress:()->Unit,
    onDriverLocationPress: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.End) {
        SmallFloatingActionButton(onClick = onDriverLocationPress, shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface,modifier = Modifier) {
            Icon(painter= painterResource(R.drawable.iconmonstr_car_15), contentDescription = null)
        }
        SmallFloatingActionButton(onClick = onMyLocationPress, shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface,modifier = Modifier) {
            Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null)
        }
        FloatingActionButton(onClick = {
            if(tripUIState != MainUIState.Loading && parseTrip==null) {
                navController.navigate(NewTrip.id)
            }else if(tripUIState != MainUIState.Loading){
                onStartLocationPress()
            }
        }, shape = CircleShape) {
            if(tripUIState == MainUIState.Loading){
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.dp, color = Color.White)
            }
            else if(parseTrip==null){
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }else{
                Image(painter = painterResource(id = R.drawable.iconmonstr_location_26), contentDescription = null, colorFilter = ColorFilter.tint(Color.White))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(navController: NavHostController, userViewModel: UserViewModel, onAnyPress:()->Unit) {
    val user = ParseUser.getCurrentUser()
    Box(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(0.80f)
        .background(MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp)
            .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CircleAvatar(size = 70.dp, user = user)
            Column() {
                Text(
                    text = user?.username?:"",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black)
                    Text(
                        text = stringResource(id = R.string.edit_profile),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onAnyPress()
                        navController.navigate(Profile.id)
                    })
            }
            Spacer(modifier= Modifier.height(20.dp))
            tabsSections.forEach {screen ->
                DrawerItem(label = stringResource(id = screen.labelRes), modifier=Modifier.clickable {
                    onAnyPress()
                    navController.navigate(screen.id)
                })
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(onClick = {
                userViewModel.logout()
            }) {
                Text(
                    text = stringResource(id = R.string.logout),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.alpha(0.6f))
            }
        }
    }

}
@Composable
fun DrawerItem(label: String, modifier: Modifier = Modifier){
    Text(
        text = label,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    modifier = modifier)
}
@Composable
fun OsmMap(center: GeoPoint?,parseTrips:List<ParseObject?>,onSelectedItem:(ParseObject)->Unit, resetCenter: () -> Unit, change: (view:MapView)->Unit) = run {
    val ctx = LocalContext.current
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                if(lastCacheLocation != null){
                    controller.setCenter(lastCacheLocation)
                }
                myLocationOverlay = MyLocationNewOverlay(this)
                myLocationOverlay?.enableMyLocation()
                myLocationOverlay?.enableFollowLocation()
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                controller.setZoom(20.0)
                minZoomLevel = 9.0
                //colors
                val matrix = ColorMatrix()
                matrix.setSaturation(0.1f)
                val inverseMatrix = ColorMatrix(floatArrayOf(
                    -1f,0f,0f,0f,255f,
                    0f,-1f,0f,0f,255f,
                    0f,0f,-1f,0f,255f,
                    0f,0f,0f,0f,255f,
                ))
                matrix.preConcat(inverseMatrix)
                val filter = ColorMatrixColorFilter(matrix)
                overlayManager.tilesOverlay.setColorFilter(filter)
                change(this)
            }
        }, update = {
            it.overlays.clear()
            it.invalidate()
            if(myLocationOverlay!=null){
                it.overlays.add(myLocationOverlay)
            }
            parseTrips.forEach{parseTrip->
                if(parseTrip!=null){
                    val roadManager = OSRMRoadManager(ctx, USER_AGENT)
                    val waypoints = arrayListOf<GeoPoint>()
                    val trip = parseTrip.toTrip()
                    startGeoPoint = trip.startGeoPoint
                    val startMarker = Marker(it)
                    startMarker.position = trip.startGeoPoint
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    startMarker.icon = ctx.getDrawable(R.drawable.iconmonstr_location_26)
                    it.overlays.add(startMarker)
                    startMarker.setOnMarkerClickListener { marker, mapView ->
                        onSelectedItem(parseTrip)
                        false
                    }
                    it.controller.animateTo(trip.startGeoPoint)
                    waypoints.add(trip.startGeoPoint)
                    waypoints.add(trip.endGeoPoint)
                    roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR)
                    CoroutineScope(Dispatchers.IO).launch {
                        val road = roadManager.getRoad(waypoints)
                        val roadOverlay = RoadManager.buildRoadOverlay(road)
                        it.overlays.add(roadOverlay)
                    }
                }
            }
            if(center !=null) {
                it.controller.animateTo(center)
                resetCenter()
            }
            it.invalidate()
        })
}