package com.example.authclase10

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authclase10.ui.theme.AuthClase10Theme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.authclase10.datasource.local.LocalDataSourceProvider
import com.example.authclase10.viewmodel.AUTH_STATE
import com.example.authclase10.viewmodel.AuthViewModel
import com.example.authclase10.viewmodel.IDLE_AUTH_STATE
import com.example.authclase10.viewmodel.NO_AUTH_STATE


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "AppVariables")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inicialicemos el DataSource
        LocalDataSourceProvider.init(applicationContext.dataStore)

        enableEdgeToEdge()
        setContent {
            AuthClase10Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "profile") {
                    composable("login") { LoginScreen(navController) }
                    composable("profile") { ProfileScreen(navController) }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("drincon@icesi.edu.co") }
    var password by remember { mutableStateOf("contrasena12345") }

    val authState by viewModel.authState.collectAsState()

    if(authState.state == AUTH_STATE){
        navController.navigate("profile") {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    Column {
        Box(modifier = Modifier.height(200.dp))
        TextField(value = email, onValueChange = { email = it })
        TextField(value = password, onValueChange = { password = it })
        Button(onClick = { viewModel.login(email, password) }) {
            Text(text = "Iniciar sesión")
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAuthStatus()
    }

    when (authState.state) {
        NO_AUTH_STATE -> {
            //Navigation a login
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
        AUTH_STATE -> {
            Column {
                Box(modifier = Modifier.height(200.dp))
                Button(onClick = {
                    viewModel.getAllUsers()
                }) {
                    Text(text = "Listar usuarios")
                }
            }
        }
        IDLE_AUTH_STATE -> {
            CircularProgressIndicator()
        }
    }


}