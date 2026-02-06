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
                        val wordForTranslate = questionWords.random()
                        println("\n${wordForTranslate.original}:")
                        val correctAnswers = wordForTranslate.translate
                        println(
                            """
                            |  1 - ${questionWords[0].translate}
                            |  2 - ${questionWords[1].translate}
                            |  3 - ${questionWords[2].translate}
                            |  4 - ${questionWords[3].translate}
                        """.trimMargin()
                        )
                        val userTranslate = readln()
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
