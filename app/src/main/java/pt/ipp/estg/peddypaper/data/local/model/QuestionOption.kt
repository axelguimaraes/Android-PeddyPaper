package pt.ipp.estg.peddypaper.data.local.model

import androidx.room.*
import pt.ipp.estg.peddypaper.data.local.entities.*

/**
 * Data class that represents a question with its options.
 *
 * @property question the question.
 * @property options the list of options.
 */
data class QuestionWithOptions(
    @Embedded val question: Question,
    @Relation(
        parentColumn = "optionId",
        entityColumn = "questionId",
        associateBy = Junction(OptionQuestionCrossRef::class)
    )
    val options: List<Option>
)

/**
 * Data class that represents an option with its questions.
 *
 * @property option the option.
 * @property questions the list of questions.
 */
data class OptionWithQuestions(
    @Embedded val option: Option,
    @Relation(
        parentColumn = "questionId",
        entityColumn = "optionId",
        associateBy = Junction(OptionQuestionCrossRef::class)
    )
    val questions: List<Question>
)