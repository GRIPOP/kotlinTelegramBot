package org.example.app

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val updateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val telegramBotService = TelegramBotService()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(botToken, updateId)

        println(updates)

        val updateIdMatch = updateIdRegex.find(updates) ?: continue
        val groupUpdateId = updateIdMatch.groups
        val updateIdValue = groupUpdateId[1]?.value
        if (updateIdValue != null) {
            updateId = updateIdValue.toInt() + 1
        } else {
            println("update_id is empty")
            updateId = 0
        }

        val messageTextMatch = messageTextRegex.find(updates) ?: continue
        val groups = messageTextMatch.groups
        val text = groups[1]?.value

        val chatIdMatch = chatIdRegex.find(updates) ?: continue
        val groupChatId = chatIdMatch.groups
        val chatIdValue = groupChatId[1]?.value?.toInt()

        println(telegramBotService.sendMessage(botToken, chatIdValue, text))
    }
}
