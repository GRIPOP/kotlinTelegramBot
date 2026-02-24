package org.example.app

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class TelegramBotService(private val botToken: String) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
        val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val responseGetUpdates: HttpResponse<String> =
            client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
        return responseGetUpdates.body()
    }

    fun sendMessage(chatId: Long?, message: String?): String? {
        val encoded = URLEncoder.encode(
            message,
            StandardCharsets.UTF_8
        )
        val urlSendMessage =
            "$TELEGRAM_BASE_URL$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create(urlSendMessage))
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }

    fun sendMenu(chatId: Long?): String? {
        val urlSendMessage = "$TELEGRAM_BASE_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": "$chatId",
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$LEARNING_WORDS"
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$STATISTICS"
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val requestSendMessage: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .header("Content-Type", "application/json")
            .build()
        val responseSendMessage: HttpResponse<String> =
            client.send(requestSendMessage, HttpResponse.BodyHandlers.ofString())
        return responseSendMessage.body()
    }

    fun sendQuestion(chatId: Long?, question: Question?): String? {
        val urlSendQuestion = "$TELEGRAM_BASE_URL$botToken/sendMessage"
        val sendVariantsAnswers = """
        {
            "chat_id": "$chatId",
            "text": "${question?.correctAnswer?.original}",
            "reply_markup": {
                "inline_keyboard": [
                    [
                        {
                            "text": "${question?.variants[0]?.translate}",
                            "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 0}"
                        },
                        {
                            "text": "${question?.variants[1]?.translate}",
                            "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 1}"
                        }
                    ],
                    [
                        {
                            "text": "${question?.variants[2]?.translate}",
                            "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 2}"
                        },
                        {
                            "text": "${question?.variants[3]?.translate}",
                            "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 3}"
                        }
                    ]
                ]
            }
        }
    """.trimIndent()

        val requestSendQuestion: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendQuestion))
            .POST(HttpRequest.BodyPublishers.ofString(sendVariantsAnswers))
            .header("Content-Type", "application/json")
            .build()
        val responseSendQuestion: HttpResponse<String> =
            client.send(requestSendQuestion, HttpResponse.BodyHandlers.ofString())
        return responseSendQuestion.body()
    }

    fun checkQuestionAndSend(
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        chatId: Long?,
    ): String? {
        val nextQuestion = trainer.getNextQuestion()
        return if (nextQuestion == null) {
            "Все слова в словаре выучены"
        } else {
            telegramBotService.sendQuestion(chatId, nextQuestion)
        }
    }
}