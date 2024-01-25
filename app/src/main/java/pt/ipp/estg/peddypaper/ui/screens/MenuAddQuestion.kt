import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.ui.theme.Shapes
import pt.ipp.estg.peddypaper.ui.viewModel.MenuAddQuestionViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MenuAddQuestionScreen(
    navController: NavController,
    gameId: String
) {

    val viewModel: MenuAddQuestionViewModel = viewModel()
    LaunchedEffect(true) {
        viewModel.loadGame(gameId)
    }

    var question by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(List(4) { "" }) }
    var correctAnswerIndex by remember { mutableIntStateOf(-1) }
    var score by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var userAddedMarker by remember { mutableStateOf<LatLng?>(null) }
    var showMap by remember { mutableStateOf(false) }

    val randomQuestion = viewModel.question.observeAsState()
    if (randomQuestion.value != null) {
        question = randomQuestion.value!!.text
        options = randomQuestion.value!!.options.map { it.text }
        correctAnswerIndex = randomQuestion.value!!.options.indexOfFirst { it.correct } + 1
        score = randomQuestion.value!!.score.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Add Question") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.MENU_HOME_SCREEN) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadRandomQuestion() }) {
                        Icon(Icons.Default.Sync, contentDescription = "Generate question")
                    }
                }
            )
        },
        floatingActionButton = {
            if (areAllFieldsFilled(question, location, options, correctAnswerIndex, score)) {
                FloatingActionButton(onClick = {
                    // TODO: add the rest
                    viewModel.createQuestion(
                        text = question,
                        score = score.toInt(),
                        location = userAddedMarker!!,
                        options = options,
                        indexCorrectOption = correctAnswerIndex
                    )
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        }

    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(56.dp))
            }

            // Text question
            item {
                InputQuestionText(question) { question = it }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Score
            item {
                InputScoreValue(score) { score = it }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Location
            item {
                InputLocationButton(showMap = { showMap = it }, userAddedMarker = userAddedMarker)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Options
            item {
                Text(
                    text = "Enter four possible answers below, one of which must be the correct answer:",
                    textAlign = TextAlign.Left
                )
                for (i in 0 until 4) {
                    OutlinedTextField(
                        value = options[i],
                        onValueChange = {
                            options = options.toMutableList().also { list ->
                                list[i] = it
                            }
                        },
                        label = { Text("Option ${i + 1}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        leadingIcon = {
                            Icon(
                                Icons.Default.TextFields,
                                contentDescription = "Option"
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Correct answer
            item {
                Text(
                    text = "Select the correct answer number below:",
                    textAlign = TextAlign.Left
                )
                OptionTogglesRow(correctAnswerIndex) { index ->
                    correctAnswerIndex = index
                }
            }
        }

        if(showMap) {
            InputLocation(showMap = { showMap = it }, location = { location = it}, marker = { userAddedMarker = it })
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InputQuestionText(question: String, onValueChange: (String) -> Unit) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Text(
        text = "Enter your question:",
        textAlign = TextAlign.Left
    )
    OutlinedTextField(
        value = question,
        onValueChange = { onValueChange(it) },
        label = { Text("Question") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        leadingIcon = {
            Icon(
                Icons.Default.QuestionAnswer,
                contentDescription = "Question"
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InputScoreValue(score: String, onValueChange: (String) -> Unit) {

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Text(
        text = "Enter the question score (1-100):",
        textAlign = TextAlign.Left
    )
    OutlinedTextField(
        value = score,
        onValueChange = {
            onValueChange(
                if (it != "" && it.length <= 3 && it.toInt() > 0 && it.toInt() <= 100) {
                    it
                } else {
                    ""
                }
            )
        },
        label = { Text("Score") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        leadingIcon = {
            Icon(
                Icons.Filled.Stars,
                contentDescription = "Score"
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    )
}

@Composable
private fun InputLocationButton(showMap: (Boolean) -> Unit, userAddedMarker: LatLng?){
    Text(
        text = "Mark a location on the map to associate to the question:",
        textAlign = TextAlign.Left
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showMap(true) },
            shape = Shapes.extraSmall
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location")
                Spacer(Modifier.width(16.dp))

                if (userAddedMarker == null) {
                    Text(text = "Select a location")
                } else {
                    Text(
                        text = "Selected coordinates:\nLat: ${userAddedMarker.latitude}\nLon: ${userAddedMarker.longitude}",
                        textAlign = TextAlign.Left,)
                }
            }
        }
    }
}

@Composable
private fun InputLocation(showMap: (Boolean) -> Unit, location: (String) -> Unit, marker: (LatLng) -> Unit) {

    var markerLocation by remember { mutableStateOf<LatLng?>(null) }

    Dialog(
        onDismissRequest = { showMap(false) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box {
                GoogleMap(
                    onMapClick = { clickedLatLng ->
                        marker(clickedLatLng)
                        markerLocation = clickedLatLng
                        location("lat/lng: (${clickedLatLng.latitude},${clickedLatLng.longitude})")
                    }
                ) {
                    markerLocation?.let { MarkerState(position = it) }?.let {
                        Marker(
                            state = it,
                            title = "Selected location"
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Button to go back
                    FloatingActionButton(
                        onClick = {
                            showMap(false)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    // Button to confirm
                    if (markerLocation != null) {
                        FloatingActionButton(
                            onClick = { showMap(false) },
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Check"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionTogglesRow(correctAnswerIndex: Int, onToggle: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        for (index in 1..4) {
            OptionToggleButton(
                index = index,
                onToggle = {
                    onToggle(it)
                },
                isSelected = index == correctAnswerIndex
            )
        }
    }
}

@Composable
private fun OptionToggleButton( index: Int, onToggle: (Int) -> Unit, isSelected: Boolean) {
    val buttonModifier = Modifier
        .padding(8.dp)
        .size(56.dp)

    if (isSelected) {
        OutlinedButton(
            onClick = { onToggle(index) },
            modifier = buttonModifier
        ) {
            Text(
                text = index.toString(),
                textAlign = TextAlign.Center
            )
        }
    } else {
        ElevatedButton(
            onClick = { onToggle(index) },
            modifier = buttonModifier
        ) {
            Text(
                text = index.toString(),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun areAllFieldsFilled(
    question: String,
    location: String,
    options: List<String>,
    correctAnswerIndex: Int,
    score: String
): Boolean {
    // Check if question, location, options, and correctAnswerIndex are filled
    val areBasicFieldsFilled =
        question.isNotBlank() && location.isNotBlank() && options.all { it.isNotBlank() } && correctAnswerIndex != -1 && score.isNotBlank()

    // Check if the location contains the marker's coordinates
    val isLocationValid = location.contains("lat/lng:")

    return areBasicFieldsFilled && isLocationValid
}