package com.smartestidea.cavalcami.ui.screens

import android.Manifest
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.parse.ParseFile
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.ui.components.TopBar
import com.smartestidea.cavalcami.ui.viewmodels.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

const val USER_AGENT = "UserAgent/1.0"
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(userViewModel: UserViewModel, setOsmMap: (mapView: MapView) -> Unit) {
    val res = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val wes = rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val mes = rememberPermissionState(permission = Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    val afl = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val acl = rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION)
    val backColor = MaterialTheme.colorScheme.background
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val animatedBlur by animateDpAsState(if(drawerState.isOpen) 5.dp else 0.dp)
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit){
        if(!res.status.isGranted) res.launchPermissionRequest()
        if(!wes.status.isGranted) wes.launchPermissionRequest()
        if(!mes.status.isGranted) mes.launchPermissionRequest()
        if(!afl.status.isGranted) afl.launchPermissionRequest()
        if(!acl.status.isGranted) acl.launchPermissionRequest()
    }
    ModalNavigationDrawer(drawerContent = {  DrawerContent() }, drawerState= drawerState) {
        Scaffold(
            floatingActionButton = {Controllers()}
        ) {innerPadding->
            Box(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()){
                    OsmMap{
                        setOsmMap(it)
                    }
                    TopBar(onNavIconPress={
                        scope.launch {
                            drawerState.open()
                        }
                    },modifier = Modifier.align(Alignment.TopCenter))
            }
        }
    }
}

@Composable
fun Controllers() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.End) {
        SmallFloatingActionButton(onClick = {  }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface,modifier = Modifier) {
            Icon(painter= painterResource(R.drawable.iconmonstr_car_15), contentDescription = null)
        }
        SmallFloatingActionButton(onClick = {  }, shape = CircleShape, containerColor = MaterialTheme.colorScheme.surface,modifier = Modifier) {
            Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null)
        }
        FloatingActionButton(onClick = {  }, shape = CircleShape) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun DrawerContent(){
    val user = ParseUser.getCurrentUser()
    val painter = rememberAsyncImagePainter(
        ImageRequest
            .Builder(LocalContext.current)
            .data(data= (user.get("profile_photo") as ParseFile).file)
            .build()
    )
    Box(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(0.80f)
        .background(MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(shape = CircleShape, modifier = Modifier.size(60.dp)) {
                Image(painter = painter, contentDescription = "profile_photo", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
            Column() {
                Text(
                    text = user.username,
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(id = R.string.edit_profile),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold)
            }
            NavigationDrawerItem(label = { Text(text = "Home") }, selected = true, onClick = { })
        }
    }

}

@Composable
fun OsmMap(change: (view:MapView)->Unit) = AndroidView(
    factory = { context ->
        MapView(context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            val startPoint = GeoPoint(23.1136, -82.3666)
            controller.setZoom(20.0)
            val startMarker = Marker(this)
            startMarker.position = startPoint
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            startMarker.title = "START"
            startMarker.icon = context.getDrawable(R.drawable.iconmonstr_location_26)
            overlays.add(startMarker)
            controller.setCenter(startPoint)
            val roadManager = OSRMRoadManager(context, USER_AGENT)
            val waypoints = arrayListOf<GeoPoint>()
            waypoints.add(startPoint)
            minZoomLevel = 9.0
            val endPoint = GeoPoint(23.0418, -81.5775)
            waypoints.add(endPoint)
            roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR)
            val myLocationOverlay = MyLocationNewOverlay(this)
            overlays.add(myLocationOverlay)
            myLocationOverlay.enableMyLocation()
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
            //events
            val eventReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                    return true
                }
                override fun longPressHelper(p: GeoPoint?): Boolean {
                    val marker = Marker(this@apply)
                    marker.position = p
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(marker)
                    return true
                }

            }
            overlays.add(MapEventsOverlay(eventReceiver))
            //road
            CoroutineScope(Dispatchers.IO).launch {
                val road = roadManager.getRoad(waypoints)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "${road.mDuration} ${road.mLength}", Toast.LENGTH_LONG)
                        .show()
                }
                val roadOverlay = RoadManager.buildRoadOverlay(road)
                overlays.add(roadOverlay)
                invalidate()
            }
            change(this)
        }
    })