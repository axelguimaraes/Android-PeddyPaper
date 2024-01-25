package pt.ipp.estg.peddypaper.data.local.entities

import androidx.room.*

@Entity(
    tableName = "option_question_cross_ref",
    primaryKeys = ["optionId", "questionId"],
    foreignKeys = [
        ForeignKey(
            entity = Option::class,
            parentColumns = ["optionId"],
            childColumns = ["optionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Question::class,
            parentColumns = ["questionId"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["optionId"]),
        Index(value = ["questionId"])
    ]
)
data class OptionQuestionCrossRef(
    val optionId: String,
    val questionId: String
)