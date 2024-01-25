package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.graphics.drawable.shapes.Shape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.ui.theme.Shapes

@Composable
fun MenuHomeScreen(navController: NavController, auth: FirebaseAuth) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        MainMenu(navController, auth)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainMenu(navController: NavController, auth: FirebaseAuth) {
    Scaffold() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Logo()
            Spacer(modifier = Modifier.height(16.dp))
            MenuButtons(auth, navController)
            Spacer(modifier = Modifier.height(16.dp))
            WelcomeMessage(auth)
        }
    }
}

@Composable
private fun MenuButtons(
    auth: FirebaseAuth,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainMenuButton(
            text = "Create Game",
            icon = Icons.Default.PlayArrow,

            onClick = { navController.navigate(Routes.MENU_YOUR_CURRENT_GAMES_SCREEN) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MainMenuButton(
            text = "Join Game",
            icon = Icons.Default.FastForward,
            onClick = { navController.navigate(Routes.MENU_JOIN_GAME_SCREEN) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MainMenuButton(
            text = "Global Player Rankings",
            icon = Icons.Default.List,
            onClick = { navController.navigate(Routes.MENU_RANKING_DASHBOARD_SCREEN) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MainMenuButton(
            text = "Profile",
            icon = Icons.Default.AccountCircle,
            onClick = { navController.navigate(Routes.USER_PROFILE_SCREEN) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        MainMenuButton(
            text = "Logout",
            icon = Icons.Default.ExitToApp,
            onClick = {
                auth.signOut()
                navController.navigate(Routes.LOGIN_SCREEN)
            }
        )
    }
}

@Composable
fun MainMenuButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text)
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        modifier = modifier,
        contentDescription = null
    )
    Spacer(modifier = Modifier.height(8.dp))
    Image(
        painter = painterResource(id = R.drawable.peddypaper_title),
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
private fun WelcomeMessage(auth: FirebaseAuth) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(modifier = Modifier.padding(16.dp))
        Text(
            text = if (auth.currentUser?.displayName != null) {
                "Welcome ${auth.currentUser!!.displayName}"
            } else {
                "Welcome ${auth.currentUser?.email}"
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

/*
@Preview
@Composable
fun MenuHomeScreenPreview() {
    val navController = rememberNavController()
    navController.navigate(Routes.LOGIN_SCREEN)

    MenuHomeScreen(navController = navController)
}
 */