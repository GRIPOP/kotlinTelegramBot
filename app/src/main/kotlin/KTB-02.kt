package org.example.app

import java.io.File

fun main() {
    val dictionary: MutableList<Word> = mutableListOf()
    val wordsFile = File("words.txt")

    try {
        wordsFile.readLines()

    } catch (e: java.io.FileNotFoundException) {
        println("Файл отсутствует. Необходимо создать файл со словами.")
    }

    val lines: List<String> = wordsFile.readLines()
    for (line in lines) {
        val line = line.split("|")
        val word = Word(original = line[0], translate = line[1], correctAnswersCount = line[2].toIntOrNull() ?: 0)
        dictionary.add(word)
    }

    println(dictionary)
}
