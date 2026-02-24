package org.example.app

fun main(args: Array<String>) {
    val botToken = args[0]
    val updateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()
    var lastUpdateId = 0
    val telegramBotService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(lastUpdateId)

        println(updates)

        val updateId = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        lastUpdateId = updateId + 1

        val message = messageTextRegex.find(updates)?.groups?.get(1)?.value
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLong()
        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (message?.lowercase() == GREETING && chatId != null) {
            telegramBotService.sendMessage(chatId, message)
        }

        if (message?.lowercase() == MENU && chatId != null) {
            telegramBotService.sendMenu(chatId)
        }

        if (data == STATISTICS && chatId != null) {
            val infoStatistics = trainer.getStatistics()
            telegramBotService.sendMessage(
                chatId,
                "Выучено ${infoStatistics.learnedCount} " +
                        "из ${infoStatistics.totalCount} слов | " +
                        "${infoStatistics.percent}%\n"
            )
        }

        if (data == LEARNING_WORDS && chatId != null) {
            telegramBotService.checkQuestionAndSend(trainer, telegramBotService, chatId)
        }
    }
}
