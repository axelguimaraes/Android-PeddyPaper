package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.ui.theme.Shapes
import pt.ipp.estg.peddypaper.ui.viewModel.MenuCreateGameQuestionDetailsViewModel
import pt.ipp.estg.peddypaper.utils.rememberQrBitmapPainter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuCreateGameQuestionDetailsScreen(
    navController: NavController,
    question: String,
) {

    val viewModel: MenuCreateGameQuestionDetailsViewModel = viewModel()
    viewModel.setQuestionId(question)
    val question = viewModel.question.observeAsState().value

    var isQRDialogOpen by remember { mutableStateOf(false) }

    if (question == null) {
        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            title = { Text(text = "Error") },
            text = { Text(text = "Error loading question") },
            confirmButton = {
                TextButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),

        topBar = {
            TopAppBar(
                title = { Text(text = "Question") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO: Delete*/ }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { isQRDialogOpen = true },
                Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Filled.QrCode2,
                    contentDescription = "QR"
                )
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
                Spacer(modifier = Modifier.height(32.dp))
                if (isQRDialogOpen) {
                    question?.id?.let { it1 ->
                        QRCodeDialog(
                            onDismissRequest = {
                                isQRDialogOpen = false
                            },
                            question = it1
                        )
                    }
                }
            }

            // Question text
            item {
                question?.let {
                    QuestionInfo(
                        infoDisplay = "Question",
                        text = question.text
                    )
                }
            }

            // Score
            item {
                question?.let {
                    QuestionInfo(
                        infoDisplay = "Score",
                        text = question.score.toString()
                    )
                }
            }

            // Location
            item {
                question?.let {
                    QuestionInfo(
                        infoDisplay = "Location",
                        text = "Latitude: ${question.location.latitude}\nLongitude: ${question.location.longitude}"
                    )
                }
            }

            // Options
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Options:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = Shapes.extraSmall
                    ) {
                        Divider()
                        question?.let{
                            question.options.forEach { answer ->
                                Text(
                                    text = answer.text,
                                    Modifier.padding(16.dp)
                                )
                                Divider()
                            }
                        }
                    }
                }
            }

            // Correct answer
            item {
                question?.let {questionDetails ->
                    questionDetails.options.find { it.correct }?.text?.let { optionCorrect ->
                        QuestionInfo(
                            infoDisplay = "Correct answer",
                            text = optionCorrect
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionInfo(infoDisplay: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = infoDisplay)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = Shapes.extraSmall
        ) {
            Text(
                text = text,
                Modifier.padding(16.dp),
                textAlign = TextAlign.Left
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QRCodeDialog(
    onDismissRequest: () -> Unit,
    question: String
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = { Text(text = "Generate QR") },
                    navigationIcon = {
                        IconButton(onClick = { onDismissRequest() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Card(
                        modifier = Modifier
                            .size(300.dp)
                    ) {
                        // TODO: Change to a generated QR
                        Image(
                            rememberQrBitmapPainter(question),
                            contentDescription = "QR",
                            Modifier
                                .size(300.dp)
                                .fillMaxSize()
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Code:")
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = Shapes.extraSmall,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = question,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                IconButton(onClick = { /*TODO*/ }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Export options:")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            DialogButtons(
                                icon = Icons.Default.Email,
                                text = "Email",
                                clickable = {
                                    // TODO: Clickable
                                }
                            )
                            DialogButtons(
                                icon = Icons.Default.Download,
                                text = "Download",
                                clickable = {
                                    // TODO: Clickable
                                })
                        }
                    }
                }
            }
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