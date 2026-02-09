package org.example.app

fun Question.asConsoleString(): String {
    val variants = this.variants.mapIndexed { index: Int, word: Word -> " ${index + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return "\n" + this.correctAnswer.original + ":" + "\n" + variants + "\n" + "-----------\n 0 - меню"
}

fun main() {

    val trainer = try {
        LearnWordsTrainer(3, 4)
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    while (true) {
        println("1 - Учить слова \n2 - Статистика\n0 - Выход")
        when (readln().toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question != null) {
                        println(question.asConsoleString())
                        val userAnswerInput = readln().toIntOrNull()
                        if (userAnswerInput == 0) break

                        if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                            println("Правильно!")
                        } else {
                            println("Неправильно! ${question.correctAnswer.original} – это ${question.correctAnswer.translate}")
                        }
                    } else {
                        println("Все слова в словаре выучены")
                        break
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%\n")
            }

            0 -> return

            else -> {
                println("Введите число 1, 2 или 0")
            }
        }
    }
}
