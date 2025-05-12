package com.example.authclase10.datasource.websocket

import android.util.Log
import com.example.authclase10.datasource.local.LocalDataSource
import com.example.authclase10.datasource.local.LocalDataSourceProvider
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONObject


class LiveProductsDataSource(
    val localDataSource: LocalDataSource = LocalDataSourceProvider.get(),
) {

    val messagesFlow = MutableSharedFlow<String>()

    suspend fun observeProducts(): SharedFlow<String> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("wss://d791-186-112-68-24.ngrok-free.app/websocket")
            .build()
        val token = localDataSource.load("accesstoken").first()
        client.newWebSocket(
            request,
            ProductsWebSocketListener(token, messagesFlow)
        )
        return messagesFlow
    }
}

class ProductsWebSocketListener(
    val token: String,
    val messagesFlow: MutableSharedFlow<String>
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.e(">>>", "Connected");
        val authMessage = """
            {"type":"auth", "access_token":"$token"}
        """.trimIndent()
        webSocket.send(authMessage)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        val json = JSONObject(text)
        val type = json.optString("type")
        if (type == "ping") {
            webSocket.send("""
                { 
                    "type": "pong" 
                }
            """.trimIndent())
            println("Respondido con pong")
        } else if (type == "auth") {
            val status = json.optString("status")
            if (status == "ok") {
                val queryMessage = """
                    {"type":"subscribe", "collection":"products", "query":{"limit":10}}
                """.trimIndent()
                webSocket.send(queryMessage)
            }
        } else if (type == "subscription") {
            val data = json.getJSONArray("data").toString()
            var array = Gson().fromJson(data, Array<ProductDTO>::class.java)
            runBlocking {
                array.forEach { item ->
                    messagesFlow.emit(item.name)
                }
            }

        }


    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.e(">>>", "Cerrando WebSocket: $code / $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(">>>", "Error WebSocket: ${t.message}")
    }
}

data class ProductDTO(
    val name: String,
    val price: String
)


