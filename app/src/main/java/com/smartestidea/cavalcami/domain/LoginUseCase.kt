package com.smartestidea.cavalcami.domain

import com.smartestidea.cavalcami.data.Repository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: Repository
){
    operator fun invoke(
        username:String? = null,
        email:String? = null,
        password:String, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit
    ) = repository.login(username, email, password, onSuccess, onError)
}