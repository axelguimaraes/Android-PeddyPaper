package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.data.remote.firebase.Game
import pt.ipp.estg.peddypaper.ui.viewModel.MenuJoinGameViewModel

data class OnGoingMatch(
    val numberQuestions: Int,
    val numberPlayers: Int
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuJoinGameScreen(navController: NavController) {

    val viewModel: MenuJoinGameViewModel = viewModel()
    LaunchedEffect(true) {
        viewModel.getGames()
    }

    val games = viewModel.games.observeAsState()
    var selectedGameId by remember { mutableStateOf<String?>(null) }

    val navigateToGameId by viewModel.navigateToGame.observeAsState()

    val showJoinGameDialog by viewModel.showJoinGameDialog.observeAsState()

    if (navigateToGameId != null) {
        navController.navigate(Routes.GAME_MAP_SCREEN + "/$navigateToGameId")
        viewModel.resetNavigation()
    }

    if (showJoinGameDialog == true && selectedGameId != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetShowJoinGameDialog() },
            title = { Text("Join Game") },
            text = { Text("You will be added to this game.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.addUserToGame(selectedGameId!!)
                    navController.navigate(Routes.GAME_MAP_SCREEN + "/$selectedGameId")
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.resetShowJoinGameDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    if(showJoinGameDialog == false) {
        navController.navigate(Routes.GAME_MAP_SCREEN + "/$selectedGameId")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Join game")},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            if (games.value?.isNotEmpty() == true) {
                LazyColumn {
                    items(games.value ?: emptyList()) { game ->
                        MatchCard(
                            gameOnGoing = game,
                            onGameSelected = { gameId ->
                                selectedGameId = gameId
                                viewModel.checkIfUserInGameAndJoin(game)
                            }
                        )
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
                        "There are currently no ongoing games. Create a new game in the main menu option.",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchCard(
    gameOnGoing: Game,
    onGameSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onGameSelected(gameOnGoing.id)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f) // Adjust weight as needed
                    .padding(16.dp)
            ) {
                Text(text = gameOnGoing.name)
                Text(text = "#questions: ${gameOnGoing.questions.size}")
                Text(text = "#players: ${gameOnGoing.players.size}")
            }

            Icon(Icons.Default.PlayArrow, contentDescription = "Start Game", Modifier.padding(16.dp))
        }
    }
}

