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

class LearnWordsTrainer(private val criteriaLearnedWord: Int = 3, private val numberOfWordTranslations: Int = 4) {
    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= criteriaLearnedWord }.size
        val percent = (learnedCount.toDouble() / totalCount * 100).toInt()
        return Statistics(totalCount, learnedCount, percent)

    }

    fun getNextQuestion(): Question? {
        val learnedWord = dictionary.filter { it.correctAnswersCount >= criteriaLearnedWord }
        val notLearnedList = dictionary.filter { it.correctAnswersCount < criteriaLearnedWord }
        if (notLearnedList.isEmpty()) return null
        val fromNotLearned = notLearnedList.shuffled()
        val fromLearned = learnedWord.shuffled()
        val questionWords = if (notLearnedList.size >= numberOfWordTranslations) {
            notLearnedList.shuffled().take(numberOfWordTranslations)
        } else {
            (fromNotLearned + fromLearned).take(numberOfWordTranslations)
        }
        val correctAnswer = questionWords.random()

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
        try {
            val dictionary: MutableList<Word> = mutableListOf()
            val wordsFile = File("words.txt")
            wordsFile.readLines().forEach {
                val splitLine = it.split("|")
                dictionary.add(Word(splitLine[0], splitLine[1], splitLine[2].toIntOrNull() ?: 0))
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")

        wordsFile.writeText(
            dictionary.joinToString("\n") { word -> "${word.original}|${word.translate}|${word.correctAnswersCount}" })
    }
}
