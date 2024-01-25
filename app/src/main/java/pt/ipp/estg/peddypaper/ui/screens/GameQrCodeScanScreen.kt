package pt.ipp.estg.peddypaper.ui.screens

import android.annotation.SuppressLint
import android.nfc.Tag
import android.util.Log
import android.widget.Toast
import androidx.camera.view.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.*
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import com.google.mlkit.vision.barcode.*
import pt.ipp.estg.peddypaper.Routes
import pt.ipp.estg.peddypaper.ui.viewModel.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GameQrCodeScannerScreen(
    navController: NavController,
    gameId : String
) {
    val ctx = LocalContext.current
    val viewModel: GameQrCodeScanViewModel = viewModel()
    viewModel.loadGame(gameId)

    val code by viewModel.code.observeAsState()
    val check by viewModel.check.observeAsState()

    if (check.equals("Verified")) {
        viewModel.resetCheck()
        navController.navigate(Routes.GAME_QUESTION_SCREEN + "/$gameId/$code")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Scan QR Code") },
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
            Spacer(modifier = Modifier.height(60.dp))

            // QR Code Scanner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Permission(viewModel)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Manual Entry Box
            ManualEntryBox(
                scannedCode = code,
                result = { manualCode ->
                    viewModel.setManualCode(manualCode)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Access Button
            Button(
                onClick = {
                    if (!code.isNullOrEmpty()) {
                        viewModel.checkCode()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Access",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            if(!check.equals("Reading")) {
                Toast.makeText(ctx, check, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
        }
    }
}

/**
 * This function is used to recognize the QR Code
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(viewModel: GameQrCodeScanViewModel) {
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    if(cameraPermission.status.isGranted){
        if(viewModel.isScanned.value == false){
            QRCodeScanner(viewModel)
        } else {
            CodeIsScanned(viewModel)
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val textToShow = if(cameraPermission.status.shouldShowRationale){
                "We need permission to use the camera"
            } else {
                "You can grant permission in your device's Settings"
            }

            Image(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera")
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = textToShow, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { cameraPermission.launchPermissionRequest() }) {
                Text(text = "Grant permission")
            }
        }
    }
}

@Composable
private fun CodeIsScanned(viewModel: GameQrCodeScanViewModel) {
    val isScanned by viewModel.isScanned.observeAsState()

    if (isScanned == true) {
        val code by viewModel.scanCode.observeAsState()
        if (code != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val textToShow = if (code != null) {
                    "$code"
                } else {
                    "Code not found"
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = textToShow)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    viewModel.resetCheck()
                }) {
                    Image(imageVector = Icons.Default.Cached, contentDescription = "Scan Again")
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManualEntryBox(
    scannedCode: String?,
    result: (String) -> Unit ,
) {
    var manualCode by remember { mutableStateOf( "") }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (scannedCode != null) {
        manualCode = scannedCode
    }

    OutlinedTextField(
        value = manualCode,
        onValueChange = {
            manualCode = it
        },
        label = { Text("Insert code manually") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        leadingIcon = {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Manual Entry"
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                result(manualCode)
                keyboardController?.hide()
            }
        )
    )
}

@Composable
fun QRCodeScanner(viewModel: GameQrCodeScanViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    if (viewModel.isScanned.value == false) {
        AndroidView(factory = { context ->
            val previewView = PreviewView(context)
            viewModel
                .startCamera(
                    context,
                    lifecycleOwner,
                    previewView,
                )
            previewView
        }, modifier = Modifier.fillMaxSize())
    }
}
