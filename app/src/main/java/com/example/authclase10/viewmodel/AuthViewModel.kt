package com.example.authclase10.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authclase10.datasource.LoginData
import com.example.authclase10.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    val authRepository: AuthRepository = AuthRepository()
) : ViewModel(){

    var authState:MutableStateFlow<AuthState> = MutableStateFlow<AuthState>( AuthState() )

    fun login(email:String, pass:String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(LoginData(
                email, pass
            ))
            authState.value = AuthState(state = AUTH_STATE)
        }
    }

    fun getAuthStatus() {
        viewModelScope.launch (Dispatchers.IO){
            var accessToken = authRepository.getAccessToken()
            accessToken?.let {
                if(it.isEmpty()){
                    authState.value = AuthState(state = NO_AUTH_STATE)
                }else{
                    authState.value = AuthState(state = AUTH_STATE)
                }
            }
        }
    }

    fun getAllUsers() {
        viewModelScope.launch (Dispatchers.IO){
            authRepository.getAllUsers()
        }
    }

}

data class AuthState(
    var state:String = IDLE_AUTH_STATE
)

var AUTH_STATE = "AUTH"
var NO_AUTH_STATE = "NO_AUTH"
var IDLE_AUTH_STATE = "IDLE_AUTH"