package com.smartestidea.cavalcami.domain

import com.smartestidea.cavalcami.data.Repository
import javax.inject.Inject

class RemoveTripUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit) = repository.removeTrip(onSuccess, onError)
}