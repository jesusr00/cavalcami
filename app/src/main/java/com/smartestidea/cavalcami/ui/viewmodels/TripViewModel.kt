package com.smartestidea.cavalcami.ui.viewmodels

import android.location.Address
import androidx.lifecycle.ViewModel
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.anyEmpty
import com.smartestidea.cavalcami.core.getError
import com.smartestidea.cavalcami.data.model.Trip
import com.smartestidea.cavalcami.domain.RemoveTripUseCase
import com.smartestidea.cavalcami.domain.SaveTripUseCase
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.theme.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val saveTripUseCase: SaveTripUseCase,
    private val removeTripUseCase: RemoveTripUseCase,
) :ViewModel() {
    private val _mainUIState= MutableStateFlow<MainUIState>(MainUIState.Idle)
    val mainUIState: StateFlow<MainUIState> = _mainUIState
    private val _trip = MutableStateFlow<ParseObject?>(null)
    val parseTrip: StateFlow<ParseObject?> = _trip

    init {
        getTrip()
    }

    private fun getTrip() {
        val query = ParseQuery<ParseObject>("Trip")
        query.whereEqualTo("client", ParseUser.getCurrentUser())
        query.limit = 1
        _mainUIState.value = MainUIState.Loading
        query.findInBackground { parseTrips, e ->
            if(e == null){
                _trip.value = parseTrips.getOrNull(0)
                _mainUIState.value = MainUIState.Success
            }else{
                _mainUIState.value = MainUIState.Error(getError(e.code))
            }
        }
    }

    fun saveTrip(
        startAddress: String,
        destAddress: String,
        startGeoPoint: GeoPoint?,
        destGeoPoint: GeoPoint?,
        client: ParseUser?,
        driver: ParseUser?,
        passengers: Int,
        suitcases: Int
    ){
        if( !listOf(startAddress,destAddress).anyEmpty() && startGeoPoint!=null && destGeoPoint!=null) {
            _mainUIState.value = MainUIState.Loading
            val trip = Trip(startAddress, destAddress,startGeoPoint,destGeoPoint, client, driver, passengers, suitcases)
            saveTripUseCase(trip, {
                _mainUIState.value = MainUIState.Success
                getTrip()
            }) {
                _mainUIState.value = MainUIState.Error(it)
            }
        }else{
            _mainUIState.value = MainUIState.Error(R.string.empty_fields)
        }
    }

    fun removeTrip(){
        removeTripUseCase({
            _mainUIState.value = MainUIState.Success
            _trip.value = null
        }){
            _mainUIState.value = MainUIState.Error(it)
        }
    }

    fun changeTrip(startAddress: Address?, destAddress: Address?, passengers: Int, suitcases: Int) {
        if(startAddress != null && destAddress != null) {
            _mainUIState.value = MainUIState.Loading
            val parseTrip = _trip.value!!.apply {
                put("startAddress", startAddress.extras.getString("display_name") ?: startAddress.thoroughfare)
                put("endAddress", destAddress.extras.getString("display_name") ?: destAddress.thoroughfare)
                put("passengers", passengers)
                put("suitcases", suitcases)
                put("startGeoPoint", "${startAddress.latitude};${startAddress.longitude}")
                put("endGeoPoint", "${destAddress.latitude};${destAddress.longitude}")
            }
            parseTrip.saveInBackground {e->
                if(e==null){
                    _mainUIState.value = MainUIState.Success
                    _trip.value = parseTrip
                }else{
                    _mainUIState.value = MainUIState.Error(e.code)
                }
            }
        }else{
            _mainUIState.value = MainUIState.Error(R.string.empty_fields)
        }
    }

    fun idle() {
        _mainUIState.value = MainUIState.Idle
    }

}