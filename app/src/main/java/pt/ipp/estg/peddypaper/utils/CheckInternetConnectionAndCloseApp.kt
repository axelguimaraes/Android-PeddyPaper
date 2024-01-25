package pt.ipp.estg.peddypaper.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

fun checkInternetConnection(context: Context) {
    if (!isInternetAvailable(context)) {
        showNoInternetDialog(context)
    }
}

@SuppressLint("ObsoleteSdkInt")
private fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}

private fun showNoInternetDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("No Internet Connection")
        .setMessage("Please connect to the internet to use this app.")
        .setPositiveButton("Retry") { _, _ ->
            checkInternetConnection(context)
        }
        .setNegativeButton("Close App") { _, _ ->
            // Close the app
            if (context is Activity) {
                context.finishAffinity()
            }
        }
        .setCancelable(false)
        .show()
}
