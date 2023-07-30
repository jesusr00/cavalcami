package com.smartestidea.cavalcami.data.model

import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

abstract class User(
    val username:String,
    val email:String,
    val password:String,
    val phoneNumber:String,
    val profilePhoto: ParseFile?,
    val frontCI: ParseFile?,
    val backCI: ParseFile?
)

class Client(
    val egyPhoneNumber:String,
    val otherEgyPhoneNumber: String,
    username: String,
    email: String,
    password: String,
    phoneNumber: String,
    profilePhoto: ParseFile?,
    frontCI: ParseFile?,
    backCI: ParseFile?
):User(username, email, password, phoneNumber, profilePhoto, frontCI, backCI)

fun ParseUser.toClient() = Client(getString("egy_phone_number")?:"",getString("other_egy_phone_number")?:"",username,email,"",getString("phone_number")?:"", getParseFile("profile_photo"), getParseFile("front_ci"), getParseFile("back_ci"))
