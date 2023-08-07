package com.smartestidea.cavalcami.domain

import com.smartestidea.cavalcami.data.Repository
import com.smartestidea.cavalcami.data.model.Trip
import javax.inject.Inject

class SaveTripUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(trip: Trip, onSuccess:()->Unit, onError: (errorMsgRes: Int) -> Unit) = repository.saveTrip(trip,onSuccess, onError)
}