package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.util.PatternsCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pt.ipp.estg.peddypaper.ui.viewModel.UserProfileEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileEditScreen(navController: NavController) {

    val viewModel: UserProfileEditViewModel = viewModel()
    val currentUser = viewModel.currentUser.observeAsState().value

    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        name = currentUser?.name ?: ""
        email = currentUser?.email ?: ""
    }

    var passwordCurrent by rememberSaveable { mutableStateOf("") }
    var passwordNew by rememberSaveable { mutableStateOf("") }
    var passwordConfirm by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Profile")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (viewModel.doneToUpdateUser(name, email, "", "", "")) {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = {
                        viewModel.updateUser(name, email, passwordConfirm)
                    navController.popBackStack()
                }) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            Text(text = "User information")
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                },
                label = { Text(text = "Name") },
                leadingIcon = { Icon(Icons.Default.TextFields, contentDescription = "Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = { Text(text = "Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                )
            )
            if (!viewModel.isValidEmail(currentUser?.email ?: "")) {
                Text(
                    text = "Invalid email format",
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.padding(16.dp))
            Divider()
            Spacer(modifier = Modifier.padding(16.dp))

            Text(text = "Password")
            OutlinedTextField(
                value = passwordCurrent,
                onValueChange = { passwordCurrent = it },
                label = { Text(text = "Current Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Current Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = passwordNew,
                onValueChange = {
                    if(viewModel.isValidPassword(it)) {
                        passwordNew = it
                    }
                },
                label = { Text(text = "New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "New Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                )
            )
            if (!viewModel.isValidPassword(passwordNew)) {
                Text(
                    text = "Password must have at least 6 characters, a capital letter, and a digit",
                    color = Color.Red
                )
            }

            OutlinedTextField(
                value = passwordConfirm,
                onValueChange = {
                    if(viewModel.isValidPasswordConfirm(passwordNew, it)) {
                        passwordConfirm = it
                    }
                },
                label = { Text(text = "Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            if (!viewModel.isValidPasswordConfirm(passwordNew, passwordConfirm)) {
                Text(
                    text = "Passwords do not match.",
                    color = Color.Red
                )
            }
        }
    }
}