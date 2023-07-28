package com.smartestidea.cavalcami.data

import com.parse.ParseUser
import com.smartestidea.cavalcami.data.back4app.Back4AppManager
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
}