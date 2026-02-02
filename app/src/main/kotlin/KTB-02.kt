package org.example.app

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")

    try {
        wordsFile.readLines().forEach {
            println(it)
        }
    } catch (e: java.io.FileNotFoundException) {
        println("Файл отсутствует. Необходимо создать файл со словами.")
    }
}
