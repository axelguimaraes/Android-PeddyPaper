package pt.ipp.estg.peddypaper.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.PatternsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.ui.theme.PeddyPaperTheme

@Composable
fun ResetPasswordEmailScreen(navController: NavController) {
    var showBranding by rememberSaveable { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AnimatedVisibility(
            showBranding,
            Modifier.fillMaxWidth()
        ) {
            Branding()
        }
        ResetPasswordMenu(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResetPasswordMenu(navController: NavController) {
    var email by rememberSaveable { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enter your account email. You will receive a 6-digit code to verify it's really you."
        )
        Spacer(modifier = Modifier.padding(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = isValidEmail(it)
            },
            label = { Text(text = "Email") },
            leadingIcon = {
                Icon(Icons.Default.MailOutline, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            isError = !isEmailValid,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Send
            ),
            singleLine = true
        )
        if (!isEmailValid) {
            Text(
                text = "Invalid email format",
                color = Color.Red
            )
        }

        Button(
            onClick = {
                navController.navigate(Routes.RESET_PASSWORD_DIGIT_CODE_SCREEN)
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
                Text(text = "Next")
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
            }
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

private fun isValidEmail(email: String): Boolean {
    return PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview
@Composable
fun ResetPasswordEmailPreview() {
    val navController = rememberNavController()
    navController.navigate(Routes.REGISTER_SCREEN)
    PeddyPaperTheme {
        ResetPasswordEmailScreen(navController = navController)
    }
}