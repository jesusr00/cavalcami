package com.smartestidea.cavalcami.core

import java.util.Calendar

fun getPrice() = when(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)){
    in 2..4 ->145
    5 -> when(Calendar.getInstance().get(Calendar.MINUTE)){
        in 0..14 -> 145
        else -> 126
    }
    6->126
    in 7..11 -> 120
    in 12..18-> 125
    19-> when(Calendar.getInstance().get(Calendar.MINUTE)){
        in 0..19 -> 126
        in 20..34 -> 127
        else -> 128
    }
    20-> when(Calendar.getInstance().get(Calendar.MINUTE)){
        in 0..9 -> 128
        in 10..39 -> 130
        else -> 132
    }
    in 21..22-> 137
    else-> 140
}
fun getEstimatePrice(km:Double) = (getPrice()*km).toInt()