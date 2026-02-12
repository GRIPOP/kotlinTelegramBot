package org.example.app

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

const val TELEGRAM_BASE_URL = "https://api.telegram.org/bot"

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
        println(updates)

        val up: Regex = "\"update_id\":(\\d+)".toRegex()
        val matchResultUp = up.find(updates) ?: continue
        val group = matchResultUp.groups
        val idNum = group[1]?.value
        if (idNum != null) {
            updateId = idNum.toInt() + 1
        } else {
            println("update_id is empty")
            updateId = 0
        }

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult = messageTextRegex.find(updates) ?: continue
        val groups = matchResult.groups
        val text = groups[1]?.value
        println(text)
    }
}

fun getUpdates(botToken: String, updateId: Int): String {
    val client: HttpClient = HttpClient.newBuilder().build()
    val urlGetUpdates = "$TELEGRAM_BASE_URL$botToken/getUpdates?offset=$updateId"
    val requestGetUpdates: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val responseGetUpdates: HttpResponse<String> = client.send(requestGetUpdates, HttpResponse.BodyHandlers.ofString())
    return responseGetUpdates.body()
}
