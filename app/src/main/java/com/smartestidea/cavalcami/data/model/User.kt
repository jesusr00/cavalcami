package com.smartestidea.cavalcami.data.model

import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

abstract class User(
    val username:String,
    val email:String,
    val password:String,
    val phoneNumber:String,
    val profilePhoto: ParseFile?=null,
    val frontCI: ParseFile?=null,
    val backCI: ParseFile?=null
)

class Client(
    val egyPhoneNumber:String,
    val otherEgyPhoneNumber: String,
    username: String,
    email: String,
    password: String,
    phoneNumber: String,
    profilePhoto: ParseFile?=null,
    frontCI: ParseFile?=null,
    backCI: ParseFile?=null
):User(username, email, password, phoneNumber, profilePhoto, frontCI, backCI)

fun ParseUser.toClient() = Client(getString("egy_phone_number")?:"",getString("other_egy_phone_number")?:"",username,email,"",getString("phone_number")?:"", getParseFile("profile_photo"), getParseFile("front_ci"), getParseFile("back_ci"))
fun Client.toParseUser(): ParseUser = run {
    val user = ParseUser()
    user.apply {
        username = this@run.username
        email = this@run.email
        setPassword(password)
        put("phone_number", phoneNumber)
        put("egy_phone_number", egyPhoneNumber)
        put("other_egy_phone_number", otherEgyPhoneNumber)
        profilePhoto?.let { put("profile_photo",it) }
        frontCI?.let { put("front_ci",it) }
        backCI?.let { put("back_ci",it) }
    }
    return user
}
