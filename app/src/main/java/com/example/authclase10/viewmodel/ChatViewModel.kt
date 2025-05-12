package com.example.authclase10.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import com.example.authclase10.datasource.websocket.LiveProductsDataSource


class ChatViewModel(
    val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    var messages: MutableStateFlow<List<String>> = MutableStateFlow(listOf())


    fun getLiveFlowOfProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepository.observeProducts().collect { message ->
                messages.update { actual ->
                    actual + message
                }
            }
        }
    }
}

class ChatRepository(
    val liveProductsDataSource: LiveProductsDataSource = LiveProductsDataSource()
) : ViewModel() {

    suspend fun observeProducts() : SharedFlow<String>{
        return liveProductsDataSource.observeProducts()
    }
}