package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.ipp.estg.peddypaper.data.remote.firebase.Option
import pt.ipp.estg.peddypaper.data.remote.firebase.Question
import pt.ipp.estg.peddypaper.ui.theme.slightlyDeemphasizedAlpha
import pt.ipp.estg.peddypaper.ui.viewModel.GameQuestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameQuestionScreen(
    navController: NavController,
    gameId: String,
    questionId : String
) {
    val viewModel: GameQuestionViewModel = viewModel()
    viewModel.gameId = gameId
    viewModel.questionId = questionId

    viewModel.loadGame()
    viewModel.getQuestion()
    val question = viewModel._question.observeAsState()

    if(viewModel.isAnswered.value == true){
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Question" )},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(65.dp))
            question.value?.let { it1 -> QuestionTitle(question = it1) }
            Spacer(Modifier.height(18.dp))
            question.value?.let { it1 -> ChoiceQuestion(question = it1, navController = navController, viewModel = viewModel) }
        }
    }
}

@Composable
private fun QuestionTitle(
    question: Question,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Score",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        )
        Text(
            text = question.score.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        )
    }
    Text(
        text = question.text.toString(),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
    )
}

@Composable
fun ChoiceQuestion(question: Question, navController: NavController, viewModel: GameQuestionViewModel) {

    val selectedOption by viewModel.selectedOption.observeAsState()
    val correctAnswer by viewModel.correctAnswer.observeAsState()

    question.options.forEach { option ->
        ListAnswers(
            text = option.text,
            selected = selectedOption == option,
            correctAnswer = correctAnswer,
            onAnswerSelected = { viewModel.setSelectedOption(option) }
        )
    }

    Button(
        onClick = {
            viewModel.submitAnswer()
            navController.popBackStack()
            navController.popBackStack()
        },
        enabled = selectedOption != null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Submit")
    }

}

@Composable
fun ListAnswers(
    text: String,
    selected: Boolean = false,
    correctAnswer: Boolean? = null,
    onAnswerSelected: () -> Unit,
) {
    val backgroundColor = if (selected) {
        if (correctAnswer == null) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            if (correctAnswer) {
                ShowToast(true)
                Color.Green
            } else {
                ShowToast(false)
                Color.Red
            }
        }
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected = selected,
                onClick = { onAnswerSelected() },
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Box(Modifier.padding(8.dp)) {
                RadioButton(selected, onClick = {
                    onAnswerSelected()
                })
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun ShowToast(isCorrect: Boolean) {
    val message = if (isCorrect) "Correct answer!" else "Wrong answer!"
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_LONG).show()
}
