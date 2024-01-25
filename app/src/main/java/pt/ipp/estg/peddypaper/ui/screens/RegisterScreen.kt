package pt.ipp.estg.peddypaper.ui.screens

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.PatternsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.data.remote.firebase.Player
import pt.ipp.estg.peddypaper.ui.theme.PeddyPaperTheme

@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth, db : FirebaseFirestore) {
    var showBranding by rememberSaveable { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                showBranding,
                Modifier.fillMaxWidth()
            ) {
                Branding()
            }
            NameEmailPasswordFields(navController, auth, db)
        }
    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically)
    ) {
        Logo(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 76.dp)
        )
    }
}

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = R.drawable.logo),
        modifier = modifier
            .size(150.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
    Image(
        painter = painterResource(id = R.drawable.peddypaper_title),
        modifier = modifier
            .size(150.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.FillWidth,
        contentDescription = null
    )
}

@Composable
private fun NameEmailPasswordFields(navController: NavController, auth: FirebaseAuth, db : FirebaseFirestore) {
    var name by rememberSaveable { mutableStateOf("") }

    var email by rememberSaveable { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }

    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordValid by remember { mutableStateOf(true) }
    val baseContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = {
                if (it.length <= 25) {
                    name = it
                }
            },
            label = { Text("Name") },
            leadingIcon = {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = isValidEmail(it)
            },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.MailOutline, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            isError = !isEmailValid,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        if (!isEmailValid) {
            Text(
                text = "Invalid email format",
                color = Color.Red
            )
        }

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid = isValidPassword(it)
            },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            isError = !isPasswordValid,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )
        if (!isPasswordValid) {
            Text(
                text = "Password must have at least 6 characters, a capital letter, and a digit",
                color = Color.Red
            )
        }
        Button(
            onClick = {
                if (isEmailValid && isPasswordValid) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(baseContext as Activity) { task ->
                            if (task.isSuccessful) {
                                // Criação do usuário bem-sucedida, agora crie o Player
                                val userId = task.result?.user?.uid ?: return@addOnCompleteListener

                                // Crie o objeto Player
                                val newPlayer = Player(
                                    id = userId,
                                    name = name,
                                    email = email,
                                    score = 0
                                )

                                // Adicione o Player ao Firestore
                                val playersRef = db.collection("PLAYERS").document(userId)
                                playersRef.set(newPlayer).addOnSuccessListener {
                                    // Player criado com sucesso, agora atualize o ID
                                    playersRef.update("id", playersRef.id)
                                }.addOnCompleteListener {
                                    // Após atualizar, navegue para outra tela ou mostre uma mensagem
                                    navController.popBackStack()
                                }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                navController.popBackStack()
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Register")
                Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null)
            }
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    val regex = "^(?=.*[A-Z])(?=.*\\d).{6,}\$".toRegex()
    return regex.matches(password)
}

/*
@Preview
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    navController.navigate(Routes.LOGIN_SCREEN)

    PeddyPaperTheme {
        RegisterScreen(navController = navController)
    }
}
*/