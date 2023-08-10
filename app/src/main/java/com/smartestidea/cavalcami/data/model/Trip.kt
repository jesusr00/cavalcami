package com.smartestidea.cavalcami.data.model

import com.parse.ParseObject
import com.parse.ParseRelation
import com.parse.ParseUser
import org.osmdroid.util.GeoPoint

data class Trip(
    val id:String?=null,
    val startAddress:String,
    val endAddress:String,
    val startGeoPoint:GeoPoint,
    val endGeoPoint: GeoPoint,
    val client:ParseUser?,
    val driver:ParseUser?,
    val passengers:Int,
    val suitcases:Int
)

fun Trip.equalsTo(trip: Trip) =
            id == trip.id &&
            startAddress==trip.startAddress &&
            endAddress == trip.endAddress &&
            startGeoPoint == trip.startGeoPoint &&
            endGeoPoint == trip.endGeoPoint &&
            client?.objectId == trip.client?.objectId &&
            driver?.objectId == trip.driver?.objectId &&
            passengers == trip.passengers &&
            suitcases == trip.suitcases

fun Trip.toParse() = run {
    val parseTrip = ParseObject("Trip")
    id?.apply { parseTrip.objectId = id}
    parseTrip.put("startAddress",startAddress)
    parseTrip.put("endAddress", endAddress)
    client?.let { parseTrip.put("client", it) }
    driver?.let { parseTrip.put("driver", it) }
    parseTrip.put("passengers",passengers)
    parseTrip.put("suitcases",suitcases)
    parseTrip.put("startGeoPoint","${startGeoPoint.latitude};${startGeoPoint.longitude}")
    parseTrip.put("endGeoPoint","${endGeoPoint.latitude};${endGeoPoint.longitude}")
    parseTrip
}

fun ParseObject.toTrip() =run{
    val startGP = this.getString("startGeoPoint")
    val endGP = this.getString("endGeoPoint")
    val splitStart = startGP!!.split(";")
    val splitEnd = endGP!!.split(";")
    Trip(
        objectId,
        getString("startAddress")?:"",
        getString("endAddress")?:"",
        GeoPoint(splitStart[0].toDouble(),splitStart[1].toDouble()),
        GeoPoint(splitEnd[0].toDouble(),splitEnd[1].toDouble()),
        getParseUser("client"),
        getParseUser("driver"),
        getInt("passengers"),
        getInt("suitcases")
    )
}
