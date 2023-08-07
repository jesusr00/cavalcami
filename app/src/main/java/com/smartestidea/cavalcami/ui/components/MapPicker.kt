package com.smartestidea.cavalcami.ui.components

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.Address
import android.location.LocationManager
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.ui.components.fields.DefaultField
import com.smartestidea.cavalcami.ui.screens.USER_AGENT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.location.GeocoderNominatim
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale

@Composable
fun MapPicker(onSelect:(GeoPoint?)->Unit) {
    val ctx = LocalContext.current
    val locationManager =  ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    var geoPoint: GeoPoint? by rememberSaveable {
        mutableStateOf(null)
    }
    var place by rememberSaveable {
        mutableStateOf("")
    }
    var places by rememberSaveable {
        mutableStateOf(emptyList<Address>())
    }
    var mapView: MapView? by remember{
        mutableStateOf(null)
    }
    val geoCoder = GeocoderNominatim(Locale.getDefault(), USER_AGENT)
    geoPoint?.toDoubleString()?.let { Log.i(null, it) }
    Box(modifier= Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    val startPoint = GeoPoint(23.1136, -82.3666)
                    controller.setCenter(startPoint)
                    if(location!=null) {
                        controller.setCenter(GeoPoint(location.latitude, location.longitude))
                    }
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    controller.setZoom(20.0)
                    minZoomLevel = 9.0
                    val myLocationOverlay = MyLocationNewOverlay(this)
                    overlays.add(myLocationOverlay)
                    myLocationOverlay.enableMyLocation()
                    myLocationOverlay.enableFollowLocation()
                    myLocationOverlay.runOnFirstFix {
                        if(geoPoint == null){
                            geoPoint = myLocationOverlay.myLocation
                        }
                    }
                    controller.setCenter(myLocationOverlay.myLocation)
                    //colors
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0.1f)
                    val inverseMatrix = ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 0f, 255f,
                        )
                    )
                    matrix.preConcat(inverseMatrix)
                    val filter = ColorMatrixColorFilter(matrix)
                    overlayManager.tilesOverlay.setColorFilter(filter)
                    //events
                    val eventReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            if(p!=null){
                                geoPoint = p
                            }
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint?): Boolean = true
                    }

                    overlays.add(MapEventsOverlay(eventReceiver))
                    mapView = this
                }
            }, update={
                if(geoPoint!=null){
                    println(geoPoint)
                    val currPoint = GeoPoint(geoPoint!!.latitude, geoPoint!!.longitude)
                    val marker = Marker(mapView)
                    marker.position = currPoint
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.icon = ctx.getDrawable(R.drawable.iconmonstr_location_26)
                    if(it.overlays.size == 3){
                        it.overlays[2] = marker
                    }else{
                        it.overlays.add(marker)
                    }
                    it.controller.animateTo(geoPoint)
                }
            },modifier= Modifier
        )
        Column(modifier = Modifier
            .padding(top = 20.dp)
            .align(Alignment.TopCenter)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            DefaultField(value = place, labelRes = R.string.place, icon = Icons.Rounded.Place,
                onValueChange = {
                    place = it
                }, trailingIcon = Icons.Rounded.Search, onTrailingIconPress = {
                    CoroutineScope(Dispatchers.IO).launch{
                        val results = geoCoder.getFromLocationName(place,5, location!!.latitude-0.1,location.longitude-0.1,location.latitude+0.1,location.longitude+0.1)
                        places = results
                    }
                }, trailingIconDescRes = R.string.search, keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ), keyboardActions = KeyboardActions(
                    onSearch = {
                        CoroutineScope(Dispatchers.IO).launch{
                            val results = geoCoder.getFromLocationName(place,5, location!!.latitude-0.1,location.longitude-0.1,location.latitude+0.1,location.longitude+0.1)
                            places = results
                        }
                    }
                )
            )
            Box(modifier = Modifier.fillMaxWidth(0.8f)){
                DropdownMenu(expanded = places.isNotEmpty(), onDismissRequest = {
                    places = emptyList()
                }) {
                    for(i in places.indices){
                        DropdownMenuItem(text = @Composable {
                            Text(text = places[i].extras.getString("display_name")?:places[i].thoroughfare )
                        }, onClick = {
                            geoPoint= GeoPoint(places[i].latitude,places[i].longitude)
                            places = emptyList()
                        })
                        if(i!= places.lastIndex){
                            Divider(modifier= Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
        Button(onClick = {
            onSelect(geoPoint)
        }, modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 10.dp)
            .align(Alignment.BottomCenter), colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
        )) {
            Text(text = stringResource(id = R.string.select), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp), color = Color.White)
        }
    }
}