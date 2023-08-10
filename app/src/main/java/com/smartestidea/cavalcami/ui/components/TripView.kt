package com.smartestidea.cavalcami.ui.components

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.Address
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.ui.screens.USER_AGENT
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

@Composable
fun TripView(start: Any?, dest: Any?) {
    val ctx = LocalContext.current
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                controller.setZoom(20.0)
                minZoomLevel = 9.0

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

            }
        }, update = {
            it.overlays.clear()
            if (start != null) {
                val startMarker = Marker(it)
                val startGeoPoint = when (start) {
                    is GeoPoint -> start
                    is Address -> GeoPoint(start.latitude, start.longitude)
                    else -> null
                }
                startMarker.position = startGeoPoint
                startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                startMarker.icon = ctx.getDrawable (R.drawable.iconmonstr_location_26)
                it.overlays.add(startMarker)
                it.controller.setCenter(startGeoPoint)
            }
            if (dest != null) {
                val destMarker = Marker(it)
                val destGeoPoint = when (dest) {
                    is GeoPoint -> dest
                    is Address -> GeoPoint(dest.latitude, dest.longitude)
                    else -> null
                }
                destMarker.position = destGeoPoint
                destMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                destMarker.icon = ctx.getDrawable(R.drawable.iconmonstr_location_26)
                it.overlays.add(destMarker)
            }
            //road
            if (start != null && dest != null) {
                val roadManager = OSRMRoadManager(ctx, USER_AGENT)
                val waypoints = arrayListOf<GeoPoint>()
                val startGeoPoint = when (start) {
                    is GeoPoint -> start
                    is Address -> GeoPoint(start.latitude, start.longitude)
                    else -> null
                }
                val destGeoPoint = when (dest) {
                    is GeoPoint -> dest
                    is Address -> GeoPoint(dest.latitude, dest.longitude)
                    else -> null
                }
                waypoints.add(startGeoPoint!!)
                waypoints.add(destGeoPoint!!)
                roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR)
                CoroutineScope(Dispatchers.IO).launch {
                    val road = roadManager.getRoad(waypoints)
                    val roadOverlay = RoadManager.buildRoadOverlay(road)
                    it.overlays.add(roadOverlay)
                    withContext(Dispatchers.Main){
                        it.zoomToBoundingBox(roadOverlay.bounds,true)
                    }
                    it.invalidate()
                }
            }

        })
}