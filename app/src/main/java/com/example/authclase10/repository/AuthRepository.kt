package com.example.authclase10.repository

import android.util.Log
import com.example.authclase10.config.RetrofitConfig
import com.example.authclase10.datasource.AuthService
import com.example.authclase10.datasource.LoginData
import com.example.authclase10.datasource.local.LocalDataSourceProvider
import kotlinx.coroutines.flow.firstOrNull

class AuthRepository(
    val authService: AuthService = RetrofitConfig.directusRetrofit.create(AuthService::class.java)
) {

    //Logica
    suspend fun login(loginData: LoginData){
        //Invocar el endpoint de auth
        val response = authService.login(loginData)
        //Almacenar el token
        LocalDataSourceProvider.get().save("accesstoken", response.data.access_token)
    }

    suspend fun getAccessToken() : String? {
        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()
        Log.e(">>>", token.toString())
        return token
    }

    suspend fun getAllUsers() {
        var token = LocalDataSourceProvider.get().load("accesstoken").firstOrNull()

        val response = authService.getAllUsers("Bearer $token")
        response.data.forEach { user ->
            Log.e(">>>", user.email)
        }
    }
}