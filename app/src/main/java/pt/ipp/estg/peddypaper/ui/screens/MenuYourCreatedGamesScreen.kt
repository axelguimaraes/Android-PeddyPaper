package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.data.remote.firebase.*
import pt.ipp.estg.peddypaper.ui.viewModel.MenuYourCreatedGamesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuYourCreatedGamesScreen(
    navController: NavController
) {

    val viewModel: MenuYourCreatedGamesViewModel = viewModel()
    val games = viewModel.games.observeAsState().value ?: emptyList()

    var showDialog by remember { mutableStateOf(false) }
    if(showDialog) {
        AlertToInsertGameName(
            viewModel = viewModel,
            showDialog = { showDialog = it },
        )
    }

    val newGame = viewModel.newGame.observeAsState().value
    if (newGame != null) {
        Log.d(TAG, "MenuYourCreatedGamesScreen: newGame: $newGame")
        navController.navigate(Routes.MENU_CREATE_GAME_SCREEN + "/${newGame.id}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Your Created Games")},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
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
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Create Game")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(games) { game ->
                    ElevatedCard(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        onClick = {
                            Log.d("MenuYourCreatedGamesScreen", "Game id: ${game.id}")
                            navController.navigate(Routes.GAME_MAP_ADMIN_SCREEN + "/${game.id}")
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(text = "Questions: ${game.questions.size}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Players: ${game.players.size}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Status: ${if (game.active) "Ongoing" else "Finished"}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlertToInsertGameName(
    viewModel: MenuYourCreatedGamesViewModel,
    showDialog: (Boolean) -> Unit,
) {
    var gameNameState by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            showDialog(false)  // fecha o dialog
        },
        title = { Text(text = "Insert Game Name") },
        text = {
            Text(text = "Please insert the name of the game you want to create.")
        },
        confirmButton = {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = gameNameState,
                    onValueChange = { gameNameState = it },
                    label = { Text(text = "Game Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.createGame(gameNameState)
                        showDialog(false)  // fecha o dialog
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Create")
                }
            }
        }
    )
}
