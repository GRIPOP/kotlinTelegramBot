package org.example.app

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URLEncoder

class TelegramBotService(private val botToken: String) {
    val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(chatId: Int?, text: String?): String? {
        val urlSendMessage =
            "$TELEGRAM_BASE_URL$botToken/sendMessage?chat_id=$chatId&text=${URLEncoder.encode(text, "UTF-8")}"
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create(urlSendMessage))
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }
}