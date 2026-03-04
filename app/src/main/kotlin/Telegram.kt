package org.example.app

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callBackQuery: CallBackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String = "",
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallBackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long?,
    @SerialName("text")
    val text: String?,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

fun main(args: Array<String>) {

    val json = Json { ignoreUnknownKeys = true }
    val botToken = args[0]
    var lastUpdateId = 0L
    val telegramBotService = TelegramBotService(botToken)
    val trainers = HashMap<Long, LearnWordsTrainer>()
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, json, botToken, telegramBotService, trainer, trainers) }
        lastUpdateId = sortedUpdates.last().updateId + 1

    }
}

fun handleUpdate(
    update: Update,
    json: Json,
    botToken: String,
    telegramBotService: TelegramBotService,
    trainer: LearnWordsTrainer,
    trainers: HashMap<Long, LearnWordsTrainer>,
) {

    val message = update.message?.text
    val chatId = update.message?.chat?.id ?: update.callBackQuery?.message?.chat?.id ?: return
    val data = update.callBackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    if (message?.lowercase() == GREETING) {
        telegramBotService.sendMessage(json, chatId, message)
    }

    if (message?.lowercase() == MENU) {
        telegramBotService.sendMenu(json, chatId)
    }

    if (data == STATISTICS_CLICKED) {
        val infoStatistics = trainer.getStatistics()
        telegramBotService.sendMessage(
            json,
            chatId,
            "Выучено ${infoStatistics.learnedCount} " +
                    "из ${infoStatistics.totalCount} слов | " +
                    "${infoStatistics.percent}%\n"
        )
    }

    if (data == LEARNING_WORDS_CLICKED) {
        checkQuestionAndSend(json, trainer, telegramBotService, chatId)
    }

    if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        val index = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
        if (trainer.checkAnswer(index)) {
            telegramBotService.sendMessage(json, chatId, "Правильно")
        } else {
            telegramBotService.sendMessage(
                json,
                chatId,
                "Неправильно! ${trainer.question?.correctAnswer?.original} - это ${trainer.question?.correctAnswer?.translate}"
            )
        }
        checkQuestionAndSend(json, trainer, telegramBotService, chatId)
    }

    if (data == RESET_CLICkED) {
        trainer.resetProgress()
        telegramBotService.sendMessage(json, chatId, "Прогресс сброшен")
    }
}

fun checkQuestionAndSend(
    json: Json,
    trainer: LearnWordsTrainer,
    telegramBotService: TelegramBotService,
    chatId: Long?,
): String? {
    val nextQuestion = trainer.getNextQuestion()
    return if (nextQuestion == null) {
        telegramBotService.sendMessage(json, chatId, "Все слова выучены")
    } else {
        telegramBotService.sendQuestion(json, chatId, nextQuestion)
    }
}
