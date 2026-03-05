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

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

class LearnWordsTrainer(
    private val fileName: String = "words.txt",
    private val criteriaLearnedWord: Int = 3,
    private val numberOfWordTranslations: Int = 4,
    ) {
    var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.size
        val learnedCount = dictionary.filter { it.correctAnswersCount >= criteriaLearnedWord }.size
        val percent = if (totalCount == 0) 0 else (learnedCount.toDouble() / totalCount * 100).toInt()
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
                saveDictionary()
                return true
            } else {
                return false
            }
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        try {
            val wordsFile = File(fileName)
            if (!wordsFile.exists()) {
                File("words.txt").copyTo(wordsFile)
            }

            val dictionary: MutableList<Word> = mutableListOf()
            wordsFile.readLines().forEach {
                val splitLine = it.split("|")
                dictionary.add(Word(splitLine[0], splitLine[1], splitLine[2].toIntOrNull() ?: 0))
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary() {
        val wordsFile = File(fileName)

        wordsFile.writeText(
            dictionary.joinToString("\n") { word -> "${word.original}|${word.translate}|${word.correctAnswersCount}" })
    }

    fun resetProgress() {
        dictionary.forEach { it.correctAnswersCount = 0 }
        saveDictionary()
    }
}
