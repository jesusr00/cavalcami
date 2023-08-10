package com.smartestidea.cavalcami.ui.viewmodels

import android.location.Address
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.anyEmpty
import com.smartestidea.cavalcami.core.getError
import com.smartestidea.cavalcami.data.Repository
import com.smartestidea.cavalcami.data.model.Trip
import com.smartestidea.cavalcami.data.model.toTrip
import com.smartestidea.cavalcami.domain.RemoveTripUseCase
import com.smartestidea.cavalcami.domain.SaveTripUseCase
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.theme.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val saveTripUseCase: SaveTripUseCase,
    private val removeTripUseCase: RemoveTripUseCase,
    private val repository: Repository,
) :ViewModel() {
    private val _mainUIState= MutableStateFlow<MainUIState>(MainUIState.Idle)
    val mainUIState: StateFlow<MainUIState> = _mainUIState
    private val _parseTrips = MutableStateFlow<List<ParseObject>>(emptyList())
    val parseTrips: StateFlow<List<ParseObject>>
        get() = _parseTrips
    init {
//        _mainUIState.value = MainUIState.Loading
        viewModelScope.launch {
            repository.clientTripFlow
                .collect{
                    _parseTrips.value = it
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
            val trip = Trip(null,startAddress, destAddress,startGeoPoint,destGeoPoint, client, driver, passengers, suitcases)
            saveTripUseCase(trip, {
                _mainUIState.value = MainUIState.Success(R.string.request_complete)
//                getTrip()
            }) {
                _mainUIState.value = MainUIState.Error(it)
            }
        }else{
            _mainUIState.value = MainUIState.Error(R.string.empty_fields)
        }
    }

    fun removeTrip(){
        removeTripUseCase({
            _mainUIState.value = MainUIState.Success(R.string.trip_removed)
        }){
            _mainUIState.value = MainUIState.Error(it)
        }
    }

    fun changeTrip(startAddress: Address?, destAddress: Address?, passengers: Int, suitcases: Int) {
        if(startAddress != null && destAddress != null) {
            _mainUIState.value = MainUIState.Loading
            val parseTrip = parseTrips.value!![0]!!.apply {
                put("startAddress", startAddress.extras.getString("display_name") ?: startAddress.thoroughfare)
                put("endAddress", destAddress.extras.getString("display_name") ?: destAddress.thoroughfare)
                put("passengers", passengers)
                put("suitcases", suitcases)
                put("startGeoPoint", "${startAddress.latitude};${startAddress.longitude}")
                put("endGeoPoint", "${destAddress.latitude};${destAddress.longitude}")
            }
            parseTrip.saveInBackground {e->
                if(e==null){
                    _mainUIState.value = MainUIState.Success(R.string.request_saved)
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