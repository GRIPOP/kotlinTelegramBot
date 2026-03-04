package org.example.app

import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(json: Json, chatId: Long?, message: String?): String? {
        val urlSendMessage = "$TELEGRAM_BASE_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = message,
        )
        val requestBodyString = json.encodeToString(requestBody)
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .header("Content-Type", "application/json")
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }

    fun sendMenu(json: Json, chatId: Long): String? {
        val urlSendMessage = "$TELEGRAM_BASE_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = "Изучать слова", callbackData = LEARNING_WORDS_CLICKED),
                        InlineKeyboard(text = "Статистика", callbackData = STATISTICS_CLICKED)
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val requestSendMessage: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }

    fun sendQuestion(json: Json, chatId: Long?, question: Question): String? {
        val urlSendQuestion = "$TELEGRAM_BASE_URL$botToken/sendMessage"
        val chunkSize = (question.variants.size + 1) / 2

        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                question.variants.mapIndexed { index, word ->
                    InlineKeyboard(
                        text = word.translate,
                        callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
                    )
                }
                    .chunked(chunkSize))
        )

        val requestBodyString = json.encodeToString(requestBody)

        val requestSendQuestion: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendQuestion))
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .header("Content-Type", "application/json")
            .build()
        val responseSendQuestion: HttpResponse<String> =
            client.send(requestSendQuestion, HttpResponse.BodyHandlers.ofString())
        return responseSendQuestion.body()
    }
}
