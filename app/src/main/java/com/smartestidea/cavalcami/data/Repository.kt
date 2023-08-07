package com.smartestidea.cavalcami.data

import com.parse.ParseUser
import com.smartestidea.cavalcami.data.back4app.Back4AppManager
import com.smartestidea.cavalcami.data.model.Trip
import javax.inject.Inject


class Repository @Inject constructor(
    private val back4AppManager: Back4AppManager
) {
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