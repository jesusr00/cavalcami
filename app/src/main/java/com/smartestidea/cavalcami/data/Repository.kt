package com.smartestidea.cavalcami.data

import android.util.Log
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.smartestidea.cavalcami.data.back4app.Back4AppManager
import com.smartestidea.cavalcami.data.model.Trip
import com.smartestidea.cavalcami.data.model.equalsTo
import com.smartestidea.cavalcami.data.model.isDriver
import com.smartestidea.cavalcami.data.model.toTrip
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject


class Repository @Inject constructor(
    private val back4AppManager: Back4AppManager
) {
    val mutex = Mutex()

    val clientTripFlow = channelFlow {
        val query = ParseQuery<ParseObject>("Trip")
        var list: List<ParseObject> = emptyList()
        while (true) {
            mutex.lock()
            val user = ParseUser.getCurrentUser()
            if( user == null) trySend(emptyList())
            else if( !user.isDriver() ){
                query.whereEqualTo("client", ParseUser.getCurrentUser())
                query.limit =1
            }
            query.findInBackground { trips, e ->
                if (e == null
                    && (!trips.all { t -> list.any { t1 -> t1.toTrip().equalsTo(t.toTrip()) } }
                    || !list.all { t -> trips.any { t1 -> t1.toTrip().equalsTo(t.toTrip()) } })
                ) {
                    list = trips
                    trySend(list)
                }
                mutex.unlock()
            }
            delay(200)
        }
    }
    fun login(
        username:String?,
        email:String?,
        password:String, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ) = back4AppManager.login(username,email,password, onSuccess, onError)
    fun signUp(
        user:ParseUser,
        onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ) = back4AppManager.signUp(user,onSuccess, onError)
    fun saveUser(
        user:ParseUser,
        onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ) = back4AppManager.saveUser(user,onSuccess, onError)
    fun saveTrip(
        trip: Trip,
        onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ) = back4AppManager.saveTrip(trip,onSuccess, onError)
    fun removeTrip(
        onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ) = back4AppManager.removeTrip(onSuccess, onError)
}