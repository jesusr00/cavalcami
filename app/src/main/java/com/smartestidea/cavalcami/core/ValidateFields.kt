package com.smartestidea.cavalcami.core

import java.util.regex.Pattern

fun isValidEmail(email: String): Boolean {
    val pattern = Pattern.compile("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isValidPhoneNumber(phoneNumber:String): Boolean{
    val pattern = Pattern.compile("^\\+\\d+\$", Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(phoneNumber)
    return matcher.matches()
}

fun List<String>.anyEmpty() = this.any{ it.isEmpty() }

fun List<Any?>.anyNull() = this.any{ it == null }