package com.smartestidea.cavalcami.data.back4app

import android.util.Log
import com.parse.Parse
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SignUpCallback
import com.smartestidea.cavalcami.core.getError
import com.smartestidea.cavalcami.data.model.Trip
import com.smartestidea.cavalcami.data.model.toParse
import javax.inject.Inject

class Back4AppManager @Inject constructor() {
    fun login(
        username:String?,
        email:String?,
        password:String, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ){
        if(username == null){
            val query = ParseUser.getQuery()
            query.whereEqualTo("email",email)
            query.limit = 1
            query.findInBackground{user, e ->
                if(user!=null){
                    loginWithUsername(user[0].username,password, onSuccess, onError)
                }else if(e != null){
                    onError(getError(e.code))
                }
            }
        }else{
            loginWithUsername(username, password, onSuccess, onError)
        }
    }
    private fun loginWithUsername(
        username:String?,
        password:String, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ){
        ParseUser.logInInBackground(username, password){user:ParseUser?, e:ParseException? ->
            if(user!=null){
                onSuccess()
            }else{
                ParseUser.logOut()
                if(e!=null){
                    onError(getError(e.code))
                }
            }
        }
    }
    fun signUp(user:ParseUser, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit){
        user.signUpInBackground { e ->
            if (e == null) {
                onSuccess()
            } else {
                Log.e("B4App_ERROR", e.message+" :: "+ e.code)
                ParseUser.logOut();
                onError(getError(e.code))
            }
        }
    }
    fun saveUser(user:ParseUser, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit){
        user.saveInBackground { e->
            if(e==null){
                onSuccess()
            }else{
                Log.e("B4App_ERROR", e.message+" :: "+ e.code)
                onError(getError(e.code))
            }
        }
    }

    fun saveTrip(trip: Trip, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit){
        val parseTrip = trip.toParse()
        parseTrip.saveInBackground {e->
            if(e==null){
                onSuccess()
            }else{
                Log.e("B4App_ERROR", e.message+" :: "+ e.code)
                onError(getError(e.code))
            }
        }
    }

    fun removeTrip(onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit){
        val query = ParseQuery<ParseObject>("Trip")
        query.whereEqualTo("client",ParseUser.getCurrentUser())
        query.findInBackground { trips, e ->
            if(e==null){
                trips[0].deleteInBackground { e->
                    if(e==null){
                        onSuccess()
                    }else{
                        onError(getError(e.code))
                    }
                }
            }else{
                onError(getError(e.code))
            }
        }
    }

}