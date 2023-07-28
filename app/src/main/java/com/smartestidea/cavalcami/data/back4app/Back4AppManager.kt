package com.smartestidea.cavalcami.data.back4app

import android.util.Log
import com.parse.Parse
import com.parse.ParseException
import com.parse.ParseUser
import com.parse.SignUpCallback
import com.smartestidea.cavalcami.core.getError
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
}