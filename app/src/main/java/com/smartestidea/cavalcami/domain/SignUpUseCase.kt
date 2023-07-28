package com.smartestidea.cavalcami.domain

import com.parse.ParseUser
import com.smartestidea.cavalcami.data.Repository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(user:ParseUser,onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit) = repository.signUp(user, onSuccess, onError)
}