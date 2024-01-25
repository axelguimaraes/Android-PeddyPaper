package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.maps.android.compose.GoogleMap
import pt.ipp.estg.peddypaper.data.remote.firebase.Player
import pt.ipp.estg.peddypaper.data.remote.firebase.PlayerRanking
import pt.ipp.estg.peddypaper.ui.viewModel.GameMapAdminViewModel

@Suppress("NAME_SHADOWING")
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameMapAdminScreen(
    navController: NavController,
    gameId : String
) {
    val viewModel: GameMapAdminViewModel = viewModel()
    viewModel.setGameId(gameId)

    val game = viewModel.game.observeAsState()
    val players = viewModel.players.observeAsState().value ?: listOf()
    val questions = viewModel.questions.observeAsState().value ?: listOf()

    var isDropdownMenuExpanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "Game Map - Admin") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (game.value?.active == true){
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            IconButton(onClick = { isDropdownMenuExpanded = !isDropdownMenuExpanded }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                            }
                            DropdownMenu(
                                expanded = isDropdownMenuExpanded,
                                onDismissRequest = { isDropdownMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        isDropdownMenuExpanded = false
                                        showDialog = true
                                        viewModel.endGame()
                                    },
                                    text = { Text(text = "End game") }
                                )
                            }
                            if (showDialog) {
                                EndGameAlertDialog(
                                    onDismissRequest = {
                                        showDialog = false
                                    },
                                    onConfirmation = {
                                        showDialog = false
                                        navController.popBackStack()
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
    ) {
        Spacer(Modifier.height(65.dp))
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // GoogleMap Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take half of the available vertical space
            ) {
                //GoogleMap{}
                Map(null, null, questions)
            }

            // OutlinedCard Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Take half of the available vertical space
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Questions left:")
                        Text(text = "0")
                    }
                }
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Position")
                        Text(text = "Player")
                        Text(text = "Score")
                    }
                }
                if (game.value != null && players.isNotEmpty()) {
                    val sortedPlayers = game.value?.players?.sortedByDescending { it.score }
                    LazyColumn {
                        itemsIndexed(players) { index, player ->
                            if (sortedPlayers != null) {
                                if(index < sortedPlayers.size) {
                                    Log.d("GameMapAdminScreen", "Player: ${player.name} Score: ${sortedPlayers.get(index).score}")
                                    Log.d("GameMapAdminScreen", "Players: ${player.name}")
                                    PlayerCard(
                                        player = player,
                                        score = sortedPlayers.get(index).score,
                                        count = index + 1
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "There are currently no players in the game.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerCard(player: Player?, score: Int?, count: Int) {
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$count")
            player?.let { Text(text = it.name) } ?: Text(text = "Unknown")
            score?.let { Text(text = it.toString()) } ?: Text(text = "x")
        }
    }
}

@Composable
private fun EndGameAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Warning")
        },
        title = {
            Text(text = "Are you sure you want to preemptively end the game?")
        },
        text = {
               Text(text = "This action cannot be reversed.")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Yes", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("No")
            }
        }
    )
}