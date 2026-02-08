package org.example.app

import java.io.File

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {
    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= CRITERIA_LEARNED_WORD }.size
        val percent = (learnedCount.toDouble() / totalCount * 100).toInt()
        return Statistics(totalCount, learnedCount, percent)

    }

    fun getNextQuestion(): Question? {
        val learnedWord = dictionary.filter { it.correctAnswersCount >= CRITERIA_LEARNED_WORD }
        val notLearnedList = dictionary.filter { it.correctAnswersCount < CRITERIA_LEARNED_WORD }
        if (notLearnedList.isEmpty()) return null
        val fromNotLearned = notLearnedList.shuffled()
        val fromLearned = learnedWord.shuffled()
        val questionWords = if (notLearnedList.size >= NUMBER_OF_WORD_TRANSLATIONS) {
            notLearnedList.shuffled().take(NUMBER_OF_WORD_TRANSLATIONS)
        } else {
            (fromNotLearned + fromLearned).take(NUMBER_OF_WORD_TRANSLATIONS)
        }
        val correctAnswer = notLearnedList.random()

        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )

        return question
    }

    fun checkAnswer(userCorrectAnswer: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (userCorrectAnswer == correctAnswerId) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                return true
            } else {
                return false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
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

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")

        wordsFile.writeText(
            dictionary.joinToString("\n") { word -> "${word.original}|${word.translate}|${word.correctAnswersCount}" })
    }
}
