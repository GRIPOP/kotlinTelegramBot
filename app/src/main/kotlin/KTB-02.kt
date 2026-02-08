package org.example.app

import java.io.File

const val CRITERIA_LEARNED_WORD = 3
const val NUMBER_OF_WORD_TRANSLATIONS = 4

fun main() {
    val dictionary = loadDictionary()

    while (true) {
        println("1 - Учить слова \n2 - Статистика\n0 - Выход")
        when (readln()) {
            "1" -> {
                while (true) {
                    val learnedWord = dictionary.filter { it.correctAnswersCount >= CRITERIA_LEARNED_WORD }
                    val notLearnedList = dictionary.filter { it.correctAnswersCount < CRITERIA_LEARNED_WORD }
                    val fromNotLearned = notLearnedList.shuffled()
                    val fromLearned = learnedWord.shuffled()
                    if (notLearnedList.isNotEmpty()) {
                        val questionWords = if (notLearnedList.size >= NUMBER_OF_WORD_TRANSLATIONS) {
                            notLearnedList.shuffled().take(NUMBER_OF_WORD_TRANSLATIONS)
                        } else {
                            (fromNotLearned + fromLearned).take(NUMBER_OF_WORD_TRANSLATIONS)
                        }
                        val wordForTranslate = notLearnedList.random()
                        println("\n${wordForTranslate.original}:")
                        questionWords.forEachIndexed { index, word -> println("${index + 1} - ${word.translate}") }
                        println("-----------\n0 - меню")
                        val userAnswerInput = readln().toIntOrNull()
                        val correctAnswerId = questionWords.indexOf(wordForTranslate) + 1
                        if (userAnswerInput == 0) {
                            break
                        } else if (userAnswerInput == correctAnswerId) {
                            println("Правильно!")
                            dictionary.find { it == wordForTranslate }?.correctAnswersCount += 1
                            saveDictionary(dictionary)
                        } else {
                            println("Неправильно! ${wordForTranslate.original} – это ${wordForTranslate.translate}")
                        }
                    } else {
                        println("Все слова в словаре выучены")
                        break
                    }
                }
            }

            "2" -> {
                if (dictionary.isEmpty()) {
                    println("Словарь пуст!")
                } else {
                    val totalCount = dictionary.size
                    val learnedCount = dictionary.filter { it.correctAnswersCount >= CRITERIA_LEARNED_WORD }.size
                    val percent = (learnedCount.toDouble() / totalCount * 100).toInt()
                    println("Выучено $learnedCount из $totalCount слов | $percent%\n")
                }
            }

            "0" -> {
                return
            }

            else -> {
                println("Введите число 1, 2 или 0")
            }
        }
    }
}

fun loadDictionary(): List<Word> {
    val dictionary: MutableList<Word> = mutableListOf()
    val wordsFile = File("words.txt")
    val lines: List<String> = wordsFile.readLines()

    for (line in lines) {
        val parts = line.split("|")
        if (parts.size >= 3) {
            val word =
                Word(original = parts[0], translate = parts[1], correctAnswersCount = parts[2].toIntOrNull() ?: 0)
            dictionary.add(word)
        }
    }
    return dictionary
}

fun saveDictionary(dictionary: List<Word>) {
    val wordsFile = File("words.txt")

    wordsFile.writeText(
        dictionary.joinToString("\n") { word -> "${word.original}|${word.translate}|${word.correctAnswersCount}" })
}
