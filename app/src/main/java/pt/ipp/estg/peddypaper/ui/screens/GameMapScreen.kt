package pt.ipp.estg.peddypaper.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pt.ipp.estg.peddypaper.R
import pt.ipp.estg.peddypaper.Routes
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import pt.ipp.estg.peddypaper.data.remote.firebase.Question
import pt.ipp.estg.peddypaper.ui.viewModel.GameMapViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun GameMapScreen(
    navController: NavController,
    gameId: String
) {
    val context = LocalContext.current
    val viewModel: GameMapViewModel = viewModel()
    var mapRendered by remember { mutableStateOf(false) }

    LaunchedEffect(gameId) {
        viewModel.loadGame(gameId)

        viewModel.questions.observe(context as LifecycleOwner) { questions ->
            if (questions.isNotEmpty()) {
                mapRendered = true
            }
        }
    }

    val game = viewModel.game.observeAsState()
    val questions = viewModel.questions.observeAsState()

    Log.e("questions", questions.toString())

    var showLocationDialog by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var showQRButton by remember { mutableStateOf(false) }

    val locationPermissionGiven = remember { mutableIntStateOf(0) }
    var notificationsPermissionGiven by remember { mutableStateOf(false) }

    val notifiedLocations = remember { mutableSetOf<LatLng>() }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        locationPermissionGiven.intValue = 2
    }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        notificationsPermissionGiven = true
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                locationPermissionGiven.intValue += 1
            }
        }

    val notificationsPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                notificationsPermissionGiven = true
            }
        }

    LaunchedEffect(key1 = "Permission") {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        notificationsPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    if (locationPermissionGiven.intValue == 2) {
        LocationUpdates(
            onLocationUpdate = { newLocation ->
                userLocation = newLocation
                val closeLocations = getCloseLocations(
                    userLocation,
                    questions.value?.map { LatLng(it.location.latitude, it.location.longitude) }
                        ?: emptyList()
                )
                showQRButton = closeLocations.isNotEmpty()

                val newCloseLocations = closeLocations.filter { it !in notifiedLocations }
                if (newCloseLocations.isNotEmpty()) {
                    showNotification(context, newCloseLocations.map { question ->
                        questions.value?.find { it.location.latitude == question.latitude && it.location.longitude == question.longitude }!!
                    })
                    notifiedLocations.addAll(newCloseLocations)
                    Log.d("Notifications", newCloseLocations.toString())
                }
            },
            onLocationServicesDisabled = {
                showLocationDialog = true
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Permissions Needed...")
        }
    }

    val portugalCenter = LatLng(39.5, -8.0)
    val cameraPositionState = rememberCameraPositionState {
        position = if (userLocation == null) {
            CameraPosition.fromLatLngZoom(portugalCenter, 6f)
        } else {
            userLocation?.let { CameraPosition.fromLatLngZoom(it, 15f) }!!
        }
    }

    var myLocationButtonPressed by remember { mutableStateOf(false) }

    LaunchedEffect(myLocationButtonPressed) {
        if (myLocationButtonPressed) {
            userLocation?.let { location ->
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(location, 16f, 0f, 0f)
                    ),
                    durationMs = 1000
                )
            }
        }
        myLocationButtonPressed = false
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box {
            // Map
            questions.value?.let { Map(userLocation, cameraPositionState, it) }

            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    // if (showQRButton) {
                    FloatingActionButton(
                        onClick = { navController.navigate(Routes.GAME_QR_SCAN_SCREEN + "/${game.value?.id}") },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan Qr Code"
                        )
                    }
                    // }
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FloatingActionButton(
                        onClick = {
                            myLocationButtonPressed = true
                        },
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "My location")
                    }
                }
            }
        }

        if (showLocationDialog) {
            AlertDialog(
                onDismissRequest = {
                    showLocationDialog = false
                    navController.popBackStack()
                },
                title = { Text("Location Services Disabled") },
                text = { Text("Please enable location services to use this feature.") },
                confirmButton = {
                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            navController.popBackStack()
                        }
                    ) {
                        Text("Enable")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showLocationDialog = false
                            navController.popBackStack()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun Map(
    userLocation: LatLng?,
    cameraPositionState: CameraPositionState?,
    questions: List<Question>
) {
    if (cameraPositionState != null) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
        ) {
            questions.forEach { question ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            question.location.latitude,
                            question.location.longitude
                        )
                    ),
                    title = question.text,
                    snippet = question.id
                )
            }

            // Marker for the user's current location
            userLocation?.let {
                val blueDot = createBlueDotBitmapDescriptor()
                Marker(
                    state = MarkerState(position = it),
                    title = "Your Location",
                    snippet = "You are here",
                    icon = blueDot
                )
            }
        }
    }
}

@Composable
private fun createBlueDotBitmapDescriptor(): BitmapDescriptor {
    // Create a blue dot bitmap
    val blueDotBitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(blueDotBitmap)
    val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    canvas.drawCircle(16f, 16f, 16f, paint)

    // Convert the bitmap to BitmapDescriptor
    return BitmapDescriptorFactory.fromBitmap(blueDotBitmap)
}

@Composable
@SuppressLint("MissingPermission")
fun LocationUpdates(onLocationUpdate: (LatLng) -> Unit, onLocationServicesDisabled: () -> Unit) {
    val ctx = LocalContext.current

    DisposableEffect(Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx)

        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onLocationServicesDisabled()
        }

        // Check if location services are enabled
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onLocationUpdate(LatLng(location.latitude, location.longitude))
            } else {
                // Handle the case where last location is null
            }
        }.addOnFailureListener {
            // Handle failure if needed
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000,
        ).setMinUpdateIntervalMillis(10000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locations: LocationResult) {
                for (location in locations.locations) {
                    if (location != null) {
                        onLocationUpdate(LatLng(location.latitude, location.longitude))
                    } else {
                        // Handle the case where location is null
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

private fun calculateDistance(location1: LatLng, location2: LatLng): Double {
    val earthRadius = 6371 // Earth radius in kilometers

    val dLat = Math.toRadians(location2.latitude - location1.latitude)
    val dLon = Math.toRadians(location2.longitude - location1.longitude)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(location1.latitude)) * cos(Math.toRadians(location2.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c * 1000 // Convert distance to meters
}

private fun getCloseLocations(
    userLocation: LatLng?,
    questionsLocation: List<LatLng>
): List<LatLng> {
    userLocation ?: return emptyList()

    val closeLocations = mutableListOf<LatLng>()

    for (location in questionsLocation) {
        val distance =
            calculateDistance(userLocation, LatLng(location.latitude, location.longitude))
        if (distance <= 50.0) {
            closeLocations.add(LatLng(location.latitude, location.longitude))
        }
    }
    return closeLocations
}

private fun showNotification(context: Context, nearbyLocations: List<Question>) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Extract names of nearby locations
    val nearbyLocationNames = nearbyLocations.joinToString(", ") { it.text }

    val notification = NotificationCompat.Builder(context, "channel_id")
        .setContentText("Nearby locations: $nearbyLocationNames")
        .setContentTitle("PeddyPaper")
        .setSmallIcon(R.drawable.logo)
        .build()

    notificationManager.notify(1, notification)
}


