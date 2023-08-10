package com.smartestidea.cavalcami.ui.components

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.parse.ParseObject
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.getEstimatePrice
import com.smartestidea.cavalcami.data.model.isDriver
import com.smartestidea.cavalcami.data.model.toDriver
import com.smartestidea.cavalcami.data.model.toTrip
import com.smartestidea.cavalcami.ui.screens.RowDetailItemContent
import com.smartestidea.cavalcami.ui.screens.USER_AGENT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DrawerDetailContent(onRightIconPress: ()->Unit, parseTrip: ParseObject, onBtnPress:()->Unit) {
    val trip = parseTrip.toTrip()
    val user = ParseUser.getCurrentUser()
    var estimatedPrice: Int? by rememberSaveable {
        mutableStateOf(null)
    }
    var isShowDetail: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    val ctx = LocalContext.current
    val callPermission = rememberPermissionState(permission = Manifest.permission.CALL_PHONE)
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val roadManager = OSRMRoadManager(ctx, USER_AGENT)
            val waypoints = arrayListOf<GeoPoint>()
            waypoints.add(trip.startGeoPoint)
            waypoints.add(trip.endGeoPoint)
            roadManager.setMean(OSRMRoadManager.MEAN_BY_CAR)
            val road = roadManager.getRoad(waypoints)
            val roadOverlay = RoadManager.buildRoadOverlay(road)
            estimatedPrice = getEstimatePrice(roadOverlay.distance / 1000.0)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.9f)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 30.dp, top = 10.dp, start = 20.dp, end = 20.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (estimatedPrice == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    } else {
                        Text(
                            text = "$${estimatedPrice!! * trip.passengers}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (!user.isDriver()) {
                        IconButton(onClick = onRightIconPress) {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = stringResource(id = R.string.edit)
                            )
                        }
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth())
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .heightIn(min = 100.dp)
            ) {
                TripView(start = trip.startGeoPoint, dest = trip.endGeoPoint)
            }
            val rows = listOf(
                Pair(R.string.from, trip.startAddress),
                Pair(R.string.to, trip.endAddress),
                Pair(R.string.passengers, trip.passengers.toString()),
                Pair(R.string.suitcases, trip.suitcases.toString())
            )
            if (trip.driver != null && !user.isDriver()) {
                val driver = trip.driver.fetchIfNeeded().toDriver()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        onClick = {isShowDetail = !isShowDetail},
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircleAvatar(size = 40.dp, user = trip.driver)
                            Text(
                                text = driver.username,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = driver.carNumber,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alpha(0.5f)
                            )
                            IconButton(onClick = {
                                if (callPermission.status.isGranted)
                                    Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel: ${driver.phoneNumber}")
                                        ctx.startActivity(this)
                                    }
                                else
                                    callPermission.launchPermissionRequest()
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Phone,
                                    contentDescription = stringResource(id = R.string.call)
                                )
                            }

                        }
                    }
                    if(isShowDetail) {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth(0.9f),
                            shape = RoundedCornerShape(
                                bottomEnd = 8.dp,
                                bottomStart = 8.dp
                            )
                        ) {
                            ParseImageView(parseFile = driver.carPhoto,modifier = Modifier.fillMaxWidth().height(100.dp))
                        }
                    }
                }
            }
            for (i in rows.indices) {
                if (i < 2) {
                    Column() {
                        RowDetailItemContent(rows[i])
                    }
                } else {
                    Row() {
                        RowDetailItemContent(rows[i])
                    }
                }
            }
            ElevatedButton(onClick = onBtnPress, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = if (user.isDriver()) R.string.take else R.string.cancel),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}
