package com.smartestidea.cavalcami.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.parse.ParseFile
import com.parse.ParseUser
import com.parse.SaveCallback
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.anyEmpty
import com.smartestidea.cavalcami.core.anyNull
import com.smartestidea.cavalcami.core.getError
import com.smartestidea.cavalcami.core.isValidEmail
import com.smartestidea.cavalcami.domain.LoginUseCase
import com.smartestidea.cavalcami.domain.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase
) :ViewModel() {
    fun login(emailOrUsername:String,password:String, onSuccess:()->Unit, onError:(errorMsgRes:Int)->Unit){
        if(emailOrUsername.isEmpty() || password.isEmpty()){
            onError(R.string.empty_fields)
        }else if(isValidEmail(emailOrUsername)){
            loginUseCase(
                email = emailOrUsername,
                password = password,
                onSuccess = onSuccess,
                onError = onError
            )
        }else{
            loginUseCase(
                username = emailOrUsername,
                password = password,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
    fun signUp(username:String, email:String,password: String, repeatPassword:String
               , phoneNumber:String, egyPhoneNumber:String, otherEgyPhoneNumber: String
               , profilePhoto:File, frontCI: File, backCI:File, onSuccess: () -> Unit, onError: (errorMsgRes: Int) -> Unit
    ){
        if(!listOf(username,email,password,repeatPassword,phoneNumber, egyPhoneNumber,otherEgyPhoneNumber).anyEmpty() &&
                (profilePhoto.length()!=0L && frontCI.length()!=0L && backCI.length()!=0L)
        ){
            println(profilePhoto.length())
            if(password == repeatPassword){
                val user = ParseUser()
                user.username = username
                user.email = email
                user.setPassword(password)
                user.put("phone_number", phoneNumber)
                user.put("egy_phone_number", egyPhoneNumber)
                user.put("other_egy_phone_number", otherEgyPhoneNumber)
                val parseProfilePhoto = ParseFile(profilePhoto)
                parseProfilePhoto.saveInBackground(SaveCallback {e->
                    if(e==null){
                        val parseFrontCI = ParseFile(frontCI)
                        parseFrontCI.saveInBackground(SaveCallback {e->
                            if(e==null){
                                val parseBackCI = ParseFile(backCI)
                                parseBackCI.saveInBackground(SaveCallback {e->
                                    if(e==null){
                                        user.put("profile_photo", parseProfilePhoto)
                                        user.put("front_ci",parseFrontCI)
                                        user.put("back_ci",parseBackCI)
                                        signUpUseCase(user,onSuccess, onError)
                                    }else{
                                        Log.e("B4AppError", e.message+" :: "+e.code)
                                        onError(getError(e.code))
                                    }
                                })
                            }else{
                                Log.e("B4AppError", e.message+" :: "+e.code)
                                onError(getError(e.code))
                            }
                        })
                    }else{
                        Log.e("B4AppError", e.message+" :: "+e.code)
                        onError(getError(e.code))
                    }
                })
            }else{
                onError(R.string.the_passwords_are_differents)
            }
        }else{
            onError(R.string.empty_fields)
        }
    }
}