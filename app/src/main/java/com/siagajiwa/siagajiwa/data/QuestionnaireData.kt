package com.siagajiwa.siagajiwa.data

data class Question(
    val id: Int,
    val text: String
)

data class QuestionnairePage(
    val pageNumber: Int,
    val questions: List<Question>
)

object QuestionnaireData {
    val pages = listOf(
        QuestionnairePage(
            pageNumber = 1,
            questions = listOf(
                Question(
                    id = 1,
                    text = "Dalam sebulan terakhir, seberapa sering anda merasa bingung/panik ketika ada hal yang terjadi tiba-tiba saat merawat anggota keluarga yang mengalami gangguan jiwa"
                ),
                Question(
                    id = 2,
                    text = "Dalam sebulan terakhir, seberapa sering anda merasa tidak bisa mengatur hal-hal penting saat merawat anggota keluarga yang mengalami gangguan jiwa"
                ),
                Question(
                    id = 3,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa cemas, tegang, atau stres saat menghadapi kondisi anggota keluarga yang mengalami gangguan jiwa"
                ),
                 Question(
                 id = 4,
                text = "Dalam sebulan terakhir, seberapa sering Anda merasa yakin tentang kemampuan Anda untuk mengatasi masalah pribadi saat merawat anggota keluarga"
            ),
            )
        ),
        QuestionnairePage(
            pageNumber = 2,
            questions = listOf(
                Question(
                    id = 5,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa hal-hal berjalan sesuai keinginan Anda dalam merawat anggota keluarga yang mengalami gangguan jiwa"
                ),
                Question(
                    id = 6,
                    text = "Dalam sebulan terakhir, seberapa sering Anda mendapat dukungan dari keluarga atau teman-teman dalam merawat anggota keluarga yang sakit jiwa"
                ),
                Question(
                    id = 7,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa mampu mengatasi banyak hal yang harus dilakukan dalam merawat anggota keluarga"
                ),
                Question(
                    id = 8,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa dapat mengendalikan hal-hal yang mengganggu dalam hidup Anda sebagai pengasuh"
                ),
            )
        ),
        QuestionnairePage(
            pageNumber = 3,
            questions = listOf(

                Question(
                    id = 9,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa berada di puncak atau menguasai situasi dalam merawat anggota keluarga"
                ),
                Question(
                    id = 10,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa marah karena hal-hal yang terjadi berada di luar kendali Anda sebagai pengasuh"
                ),
                Question(
                    id = 11,
                    text = "Dalam sebulan terakhir, seberapa sering Anda mendapati diri Anda memikirkan hal-hal yang harus Anda selesaikan dalam merawat keluarga"
                ),
                Question(
                    id = 12,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa kesulitan mengendalikan hal-hal penting dalam hidup Anda sebagai pengasuh keluarga"
                ),
            )
        ),
        QuestionnairePage(
            pageNumber = 4,
            questions = listOf(

                Question(
                    id = 13,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa lelah secara fisik karena tugas merawat anggota keluarga"
                ),
                Question(
                    id = 14,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa terisolasi atau kesepian karena tanggung jawab merawat anggota keluarga"
                ),
                Question(
                    id = 15,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa bersalah ketika meluangkan waktu untuk diri sendiri"
                ),
                Question(
                    id = 16,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa memiliki waktu yang cukup untuk kegiatan pribadi atau hobi"
                ),
            )
        ),
        QuestionnairePage(
            pageNumber = 5,
            questions = listOf(

                Question(
                    id = 17,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa hubungan Anda dengan keluarga atau teman menjadi lebih baik"
                ),
                Question(
                    id = 18,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa puas dengan cara Anda merawat anggota keluarga yang sakit"
                ),
                Question(
                    id = 19,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa memiliki informasi yang cukup untuk merawat anggota keluarga dengan baik"
                ),
                Question(
                    id = 20,
                    text = "Dalam sebulan terakhir, seberapa sering Anda merasa optimis tentang kondisi kesehatan anggota keluarga di masa depan"
                )
            )
        )
    )
    
    val ratingOptions = listOf(
        "Tidak\nPernah",
        "Sesekali",
        "Kadang\nKadang", 
        "Cukup\nSering",
        "Terlalu\nSering"
    )
}