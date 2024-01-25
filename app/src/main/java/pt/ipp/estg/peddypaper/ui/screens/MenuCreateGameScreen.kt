package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pt.ipp.estg.peddypaper.Routes
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import pt.ipp.estg.peddypaper.data.remote.firebase.Question
import pt.ipp.estg.peddypaper.ui.viewModel.MenuCreateGameViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCreateGameScreen(
    navController: NavController,
    gameId: String
){
    val viewModel: MenuCreateGameViewModel = viewModel()
    viewModel.loadGame(gameId)

    val loadedQuestions = viewModel.questions.observeAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var questions by remember { mutableStateOf(listOf<Question>()) }
    if (loadedQuestions.value != null) {
        questions = loadedQuestions.value!!
    }

    var selectedQuestions by remember { mutableStateOf(listOf<Question>()) }
    var isDropdownMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create game") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (selectedQuestions.isNotEmpty()) {
                        IconButton(onClick = {
                            questions = questions.filterNot { it in selectedQuestions }
                            viewModel.deleteQuestionsSelected(selectedQuestions)
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { isDropdownMenuExpanded = !isDropdownMenuExpanded }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = isDropdownMenuExpanded,
                            onDismissRequest = { isDropdownMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    selectedQuestions = questions
                                    isDropdownMenuExpanded = false
                                },
                                text = { Text(text = "Select all") }
                            )
                            DropdownMenuItem(
                                onClick = {
                                    selectedQuestions = listOf()
                                    isDropdownMenuExpanded = false
                                },
                                text = { Text(text = "Select none") }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedQuestions.isEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    FloatingActionButton(onClick = {
                        navController.navigate(Routes.MENU_ADD_QUESTION_SCREEN + "/$gameId")
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (questions.isNotEmpty()) {
                        FloatingActionButton(onClick = { navController.navigate(Routes.GAME_MAP_ADMIN_SCREEN + "/$gameId") }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                        }
                    }
                }
            } else {
                ExtendedFloatingActionButton(
                    onClick = { showBottomSheet = true },
                    icon = { Icon(Icons.Filled.QrCode2, "QR") },
                    text = { Text(text = "Export QR for selected questions") },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Questions List:")
                Text(text = "Count: ${questions.size}")
            }

            if (questions.isNotEmpty()) {
                LazyColumn {
                    items(questions) { question ->
                        QuestionCard(
                            question = question,
                            isSelected = question in selectedQuestions,
                            onSelectedChange = {
                                selectedQuestions = if (it) {
                                    selectedQuestions + question
                                } else {
                                    selectedQuestions - question
                                }
                            },
                            navController
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            } else {
                NoFoundQuestions()
            }
        }
    }

    if (showBottomSheet) {
        ButtonsExport(
            showBottomSheet = { showBottomSheet = it },
            selectedQuestions = { selectedQuestions = it }
        )
    }
}

@Composable
private fun QuestionCard(
    question: Question,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectedChange(it) },
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                Text("Question: ${question.text}")
                Text("Score: ${question.score}")
            }
            Card(
                modifier = Modifier
                    .clickable { navController.navigate(Routes.MENU_CREATE_GAME_QUESTION_DETAILS_SCREEN + "/${question.id}") }
                    .padding(8.dp)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "More")
            }
        }
    }
}

@Composable
private fun NoFoundQuestions() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "There are currently no questions. Add a new question by clicking +",
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ButtonsExport(
    showBottomSheet: (Boolean) -> Unit,
    selectedQuestions: (List<Question>) -> Unit,
){
    ModalBottomSheet(onDismissRequest = { showBottomSheet(false) }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Export options:")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DialogButtons(icon = Icons.Default.Email, text = "Email", clickable = {
                    selectedQuestions(emptyList())
                    showBottomSheet(false)
                    // TODO: Send email with questions
                })
                DialogButtons(icon = Icons.Default.Download, text = "Download", clickable = {
                    selectedQuestions(emptyList())
                    showBottomSheet(false)
                    // TODO: Download questions
                })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DialogButtons(icon: ImageVector, text: String, clickable: () -> Unit) {
    Card {
        Column(
            modifier = Modifier
                .clickable { clickable() }
                .padding(16.dp)
                .size(100.dp, 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, text)
            Text(text)
        }
    }
}