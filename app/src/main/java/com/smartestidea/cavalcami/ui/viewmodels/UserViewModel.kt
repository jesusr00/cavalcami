package com.smartestidea.cavalcami.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.parse.ParseFile
import com.parse.ParseUser
import com.parse.SaveCallback
import com.smartestidea.cavalcami.R
import com.smartestidea.cavalcami.core.anyEmpty
import com.smartestidea.cavalcami.core.getError
import com.smartestidea.cavalcami.core.isValidEmail
import com.smartestidea.cavalcami.domain.LoginUseCase
import com.smartestidea.cavalcami.domain.SaveUserUseCase
import com.smartestidea.cavalcami.domain.SignUpUseCase
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val saveUserUseCase: SaveUserUseCase
) :ViewModel() {
    private val _mainUIState= MutableStateFlow<MainUIState>(MainUIState.Idle)
    val mainUIState: StateFlow<MainUIState> = _mainUIState
    fun login(emailOrUsername: String, password: String){
        if(emailOrUsername.isEmpty() || password.isEmpty()){
            _mainUIState.value = MainUIState.Error(R.string.empty_fields)
        }else if(isValidEmail(emailOrUsername)){
            _mainUIState.value = MainUIState.Loading
            loginUseCase(
                email = emailOrUsername,
                password = password,
                onSuccess = {
                    _mainUIState.value = MainUIState.Success
                },
                onError = {errorMsgRes: Int ->
                    _mainUIState.value = MainUIState.Error(errorMsgRes)
                }
            )
        }else{
            _mainUIState.value = MainUIState.Loading
            loginUseCase(
                username = emailOrUsername,
                password = password,
                onSuccess = {
                    _mainUIState.value = MainUIState.Success
                },
                onError = {errorMsgRes: Int ->
                    _mainUIState.value = MainUIState.Error(errorMsgRes)
                }
            )
        }
    }
    fun signUp(
        username: String, email: String, password: String, repeatPassword: String
        , phoneNumber: String, egyPhoneNumber: String, otherEgyPhoneNumber: String
        , profilePhoto: File, frontCI: File, backCI: File
    ){
        if(!listOf(username,email,password,repeatPassword,phoneNumber, egyPhoneNumber,otherEgyPhoneNumber).anyEmpty() &&
                (profilePhoto.length()!=0L && frontCI.length()!=0L && backCI.length()!=0L)
        ){
            println(profilePhoto.length())
            if(password == repeatPassword){
                _mainUIState.value = MainUIState.Loading
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
                                        signUpUseCase(user,{
                                            _mainUIState.value = MainUIState.Success
                                        }, {errorMsgRes: Int ->
                                            _mainUIState.value = MainUIState.Error(errorMsgRes)
                                        })
                                    }else{
                                        Log.e("B4AppError", e.message+" :: "+e.code)
                                        _mainUIState.value = MainUIState.Error(getError(e.code))
                                    }
                                })
                            }else{
                                Log.e("B4AppError", e.message+" :: "+e.code)
                                _mainUIState.value = MainUIState.Error(getError(e.code))
                            }
                        })
                    }else{
                        Log.e("B4AppError", e.message+" :: "+e.code)
                        _mainUIState.value = MainUIState.Error(getError(e.code))
                    }
                })
            }else{
                _mainUIState.value = MainUIState.Error(R.string.the_passwords_are_differents)
            }
        }else{
            _mainUIState.value = MainUIState.Error(R.string.empty_fields)
        }
    }
    fun saveUser(
        username: String,
        email: String,
        phoneNumber: String,
        egyPhoneNumber: String,
        otherEgyPhoneNumber: String,
        profilePhoto: File,
        frontCI: File,
        backCI: File
    ) {
        if(!listOf(username,email,phoneNumber, egyPhoneNumber,otherEgyPhoneNumber).anyEmpty() &&
            (profilePhoto.length()!=0L && frontCI.length()!=0L && backCI.length()!=0L)
        ){
                _mainUIState.value = MainUIState.Loading
                val user = ParseUser.getCurrentUser()
                user.username = username
                user.email = email
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
                                        saveUserUseCase(user,{
                                            _mainUIState.value = MainUIState.Success
                                        }, {errorMsgRes: Int ->
                                            _mainUIState.value = MainUIState.Error(errorMsgRes)
                                        })
                                    }else{
                                        Log.e("B4AppError", e.message+" :: "+e.code)
                                        _mainUIState.value = MainUIState.Error(getError(e.code))
                                    }
                                })
                            }else{
                                Log.e("B4AppError", e.message+" :: "+e.code)
                                _mainUIState.value = MainUIState.Error(getError(e.code))
                            }
                        })
                    }else{
                        Log.e("B4AppError", e.message+" :: "+e.code)
                        _mainUIState.value = MainUIState.Error(getError(e.code))
                    }
                })
        }else{
            _mainUIState.value = MainUIState.Error(R.string.empty_fields)
        }
    }
    fun logout(){
        ParseUser.logOutInBackground {e->
            if(e == null) {
                _mainUIState.value = MainUIState.Success
            }else{
                _mainUIState.value = MainUIState.Error(getError(e.code))
            }
        }
    }
    fun idle() {
        _mainUIState.value = MainUIState.Idle
    }
}