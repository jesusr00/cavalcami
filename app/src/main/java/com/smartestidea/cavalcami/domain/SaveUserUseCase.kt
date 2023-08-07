package com.smartestidea.cavalcami.domain

import com.parse.ParseUser
import com.smartestidea.cavalcami.data.Repository
import javax.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(user: ParseUser, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit) = repository.saveUser(user, onSuccess, onError)
}