package pt.ipp.estg.peddypaper.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ipp.estg.peddypaper.data.remote.firebase.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRankingScreen(players: List<Player>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Header with Back Button
        TopAppBar(
            title = {
                Text(
                    text = "Player Ranking",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        // Player List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(players) { player ->
                PlayerRankItem(player = player)
            }
        }
    }
}

@Composable
fun PlayerRankItem(player: Player) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Information
            Column {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Score: ${player.score}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Rank Icon (Optional)
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
