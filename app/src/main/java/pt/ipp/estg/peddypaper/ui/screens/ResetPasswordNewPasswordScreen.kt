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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.PatternsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.ui.theme.PeddyPaperTheme

@Composable
fun ResetPasswordNewPasswordScreen(navController: NavController) {
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
    var newPassword by rememberSaveable { mutableStateOf("") }
    var newPasswordConfirm by rememberSaveable { mutableStateOf("") }

    var isPasswordValid by remember { mutableStateOf(true) }
    var isPasswordConfirmValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                isPasswordValid = isValidPassword(it)
            },
            label = { Text(text = "New Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            isError = !isPasswordValid,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        if (!isPasswordValid) {
            Text(
                text = "Password must have at least 6 characters, a capital letter, and a digit",
                color = Color.Red
            )
        }

        OutlinedTextField(
            value = newPasswordConfirm,
            onValueChange = {
                newPasswordConfirm = it
                isPasswordConfirmValid = isValidPasswordConfirm(newPassword,it)
            },
            label = { Text(text = "Confirm New Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            isError = !isPasswordConfirmValid,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Send
            ),
            singleLine = true
        )
        if (!isPasswordConfirmValid) {
            Text(
                text = "Passwords do not match.",
                color = Color.Red
            )
        }
        Button(
            onClick = {
                if (newPassword == newPasswordConfirm) {
                    navController.navigate(Routes.LOGIN_SCREEN)
                } else {
                    // TODO: Add else condition
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

private fun isValidPassword(password: String): Boolean {
    val regex = "^(?=.*[A-Z])(?=.*\\d).{6,}\$".toRegex()
    return regex.matches(password)
}

private fun isValidPasswordConfirm(password: String, passwordConfirm: String): Boolean {
    return password == passwordConfirm
}

@Preview
@Composable
fun ResetPasswordNewPreview() {
    val navController = rememberNavController()
    navController.navigate(Routes.REGISTER_SCREEN)
    PeddyPaperTheme {
        ResetPasswordNewPasswordScreen(navController = navController)
    }
}