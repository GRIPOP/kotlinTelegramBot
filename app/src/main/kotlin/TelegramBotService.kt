package org.example.app

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService {
    fun getUpdates(botToken: String, updateId: Int): String {
        val client: HttpClient = HttpClient.newBuilder().build()
        val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(botToken: String, chatId: Int?, text: String?): String? {
        val client: HttpClient = HttpClient.newBuilder().build()
        val urlSendMessage = "$TELEGRAM_BASE_URL$botToken/sendMessage"
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"chat_id\":$chatId, \"text\":\"$text\"}"))
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }
}