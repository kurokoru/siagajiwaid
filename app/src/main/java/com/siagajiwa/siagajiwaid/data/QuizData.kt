package com.siagajiwa.siagajiwaid.data

data class QuizQuestion(
    val id: Int,
    val text: String,
    val options: List<String>
)

data class QuizPage(
    val pageNumber: Int,
    val questions: List<QuizQuestion>
)

object QuizData {
    val pages = listOf(
        QuizPage(
            pageNumber = 1,
            questions = listOf(
                QuizQuestion(
                    id = 1,
                    text = "Bagaimana tidur anda semalam ?",
                    options = listOf("Nyenyak", "Gelisah")
                ),
                QuizQuestion(
                    id = 2,
                    text = "Bagaimana suasana hati anda ?",
                    options = listOf("Bahagia", "Sedih")
                ),
                QuizQuestion(
                    id = 3,
                    text = "Apakah anda makan dengan lahap ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 4,
                    text = "Apakah anda merasa lelah hari ini ?",
                    options = listOf("Iya", "Tidak")
                )
            )
        ),
        QuizPage(
            pageNumber = 2,
            questions = listOf(
                QuizQuestion(
                    id = 5,
                    text = "Apakah anda merasa stres hari ini ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 6,
                    text = "Apakah anda merasa cemas ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 7,
                    text = "Apakah anda merasa bahagia ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 8,
                    text = "Apakah anda merasa sehat ?",
                    options = listOf("Iya", "Tidak")
                )
            )
        ),
        QuizPage(
            pageNumber = 3,
            questions = listOf(
                QuizQuestion(
                    id = 9,
                    text = "Apakah anda berolahraga hari ini ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 10,
                    text = "Apakah anda minum air yang cukup ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 11,
                    text = "Apakah anda berinteraksi dengan orang lain ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 12,
                    text = "Apakah anda merasa produktif ?",
                    options = listOf("Iya", "Tidak")
                )
            )
        ),
        QuizPage(
            pageNumber = 4,
            questions = listOf(
                QuizQuestion(
                    id = 13,
                    text = "Apakah anda merasa tenang ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 14,
                    text = "Apakah anda merasa bersemangat ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 15,
                    text = "Apakah anda tidur cukup ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 16,
                    text = "Apakah anda merasa puas dengan hari ini ?",
                    options = listOf("Iya", "Tidak")
                )
            )
        ),
        QuizPage(
            pageNumber = 5,
            questions = listOf(
                QuizQuestion(
                    id = 17,
                    text = "Apakah anda mengalami kesulitan berkonsentrasi ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 18,
                    text = "Apakah anda merasa mudah tersinggung ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 19,
                    text = "Apakah anda merasa memiliki harapan untuk masa depan ?",
                    options = listOf("Iya", "Tidak")
                ),
                QuizQuestion(
                    id = 20,
                    text = "Apakah anda merasa didukung oleh orang-orang terdekat ?",
                    options = listOf("Iya", "Tidak")
                )
            )
        )
    )
}
