package com.example.authclase10.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authclase10.config.RetrofitConfig
import com.example.authclase10.datasource.AuthService
import com.example.authclase10.datasource.LoginData
import com.example.authclase10.datasource.local.LocalDataSourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class AuthViewModel(
    val authRepository: AuthRepository = AuthRepository()
) : ViewModel(){

    var authState:MutableStateFlow<AuthState> = MutableStateFlow( AuthState() )

    fun login(email:String, pass:String) {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.login(LoginData(
                email, pass
            ))
            authState.value = AuthState(state = AUTH_STATE)
        }
    }
}

var AUTH_STATE = "AUTH"
var NO_AUTH_STATE = "NO_AUTH"
var IDLE_AUTH_STATE = "IDLE_AUTH"

data class AuthState(
    var state:String = IDLE_AUTH_STATE
)

class AuthRepository(
    val authService: AuthService = RetrofitConfig.directusRetrofit.create(AuthService::class.java)
) {
    suspend fun login(loginData: LoginData){
        val response = authService.login(loginData)
        LocalDataSourceProvider.get().save("accesstoken", response.data.access_token)
    }

    suspend fun getAccessToken() : String? {
        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()
        return token
    }
}