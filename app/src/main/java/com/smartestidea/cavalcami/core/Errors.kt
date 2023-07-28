package com.smartestidea.cavalcami.core

import com.smartestidea.cavalcami.R

fun getError(code:Int) = when(code){
    101 -> R.string.incorrect_field
    205 -> R.string.user_not_found
    202 -> R.string.account_already_exists_for_this_username
    203 -> R.string.account_already_exists_for_this_email
    else-> R.string.unexpected_error
}