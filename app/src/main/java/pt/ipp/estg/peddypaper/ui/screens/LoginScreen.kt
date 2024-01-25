import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.PatternsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.ui.theme.PeddyPaperTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var showBranding by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
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
            EmailLogin(navController, auth)
            GoogleLogin(navController, auth)
            RegisterButton(navController)
        }

    }
}

@Composable
private fun Branding(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.wrapContentHeight(align = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailLogin(navController: NavController, auth: FirebaseAuth) {
    var email by rememberSaveable { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }

    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordValid by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        ClickableTextWithNavigation(navController)

        Button(
            onClick = {
                if (email != "" && password != "" && isEmailValid && isPasswordValid) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(context as Activity) { task ->
                            if (task.isSuccessful) {
                                navController.navigate(Routes.MENU_HOME_SCREEN)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Login")
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
            }
        }
    }
}

@Composable
fun ClickableTextWithNavigation(navController: NavController) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Blue, textDecoration = TextDecoration.None)) {
            append("Forgot my password")
            addStringAnnotation("ClickAction", "RESET_PASSWORD_EMAIL_SCREEN", 0, length)
        }
    }

    ClickableText(
        text = annotatedString,
        modifier = Modifier.padding(16.dp),
        onClick = {
            navController.navigate(Routes.RESET_PASSWORD_EMAIL_SCREEN)
        },
        style = MaterialTheme.typography.bodySmall.copy(color = Color.Blue)
    )
}

@Composable
private fun GoogleLogin(navController: NavController, auth: FirebaseAuth) {
    val context = LocalContext.current as Activity
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data: Intent? = result.data
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken, navController, auth, context)
            } catch (e: ApiException) {
                // Handle sign-in failure
                Log.d("debug", "Google sign-in failed", e)
                Toast.makeText(
                    context,
                    "Firebase Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    Button(
        onClick = {
            signInWithGoogle(launcher, context)
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
            Text(text = "Login with Google")
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
        }
    }
}

@Composable
private fun RegisterButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(Routes.REGISTER_SCREEN) },
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

private fun isValidEmail(email: String): Boolean {
    return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    val regex = "^(?=.*[A-Z])(?=.*\\d).{6,}\$".toRegex()
    return regex.matches(password)
}


private fun signInWithGoogle(launcher: ActivityResultLauncher<Intent>, context: Context) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.web_client_id))
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val signInIntent = googleSignInClient.signInIntent

    launcher.launch(signInIntent)
}

private fun firebaseAuthWithGoogle(idToken: String?, navController: NavController, auth: FirebaseAuth, context: Context) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("debug", "signInWithCredential:success")
                navController.navigate(Routes.MENU_HOME_SCREEN)
            } else {
                Log.w("debug", "signInWithCredential:failure", task.exception)
                Toast.makeText(
                    context,
                    "Firebase Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
}

/*
@Preview
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    navController.navigate(Routes.MENU_HOME_SCREEN)
    PeddyPaperTheme {
        LoginScreen(navController = navController)
    }
}
*/