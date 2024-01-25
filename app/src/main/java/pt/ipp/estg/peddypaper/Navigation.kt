package pt.ipp.estg.peddypaper

import LoginScreen
import MenuAddQuestionScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipp.estg.peddypaper.ui.screens.GameMapAdminScreen
import pt.ipp.estg.peddypaper.ui.screens.GameMapScreen
import pt.ipp.estg.peddypaper.ui.screens.GameQrCodeScannerScreen
import pt.ipp.estg.peddypaper.ui.screens.GameQuestionScreen
import pt.ipp.estg.peddypaper.ui.screens.MenuCreateGameScreen
import pt.ipp.estg.peddypaper.ui.screens.MenuCreateGameQuestionDetailsScreen
import pt.ipp.estg.peddypaper.ui.screens.MenuHomeScreen
import pt.ipp.estg.peddypaper.ui.screens.MenuJoinGameScreen
import pt.ipp.estg.peddypaper.ui.screens.MenuRankingDashboardScreen
import pt.ipp.estg.peddypaper.ui.screens.MenuYourCreatedGamesScreen
import pt.ipp.estg.peddypaper.ui.screens.RegisterScreen
import pt.ipp.estg.peddypaper.ui.screens.ResetPasswordDigitCodeScreen
import pt.ipp.estg.peddypaper.ui.screens.ResetPasswordEmailScreen
import pt.ipp.estg.peddypaper.ui.screens.ResetPasswordNewPasswordScreen
import pt.ipp.estg.peddypaper.ui.screens.UserProfileEditScreen
import pt.ipp.estg.peddypaper.ui.screens.UserProfileScreen

object Routes {
    const val LOGIN_SCREEN = "LOGIN_SCREEN"
    const val REGISTER_SCREEN = "REGISTER_SCREEN"

    const val RESET_PASSWORD_EMAIL_SCREEN = "RESET_PASSWORD_SCREEN"
    const val RESET_PASSWORD_DIGIT_CODE_SCREEN = "RESET_PASSWORD_DIGIT_CODE_SCREEN"
    const val RESET_PASSWORD_NEW_PASSWORD_SCREEN = "RESET_PASSWORD_NEW_PASSWORD_SCREEN"

    const val USER_PROFILE_SCREEN = "USER_PROFILE_SCREEN"
    const val USER_PROFILE_EDIT_SCREEN = "USER_PROFILE_EDIT_SCREEN"

    const val MENU_HOME_SCREEN = "MENU_HOME_SCREEN"
    const val MENU_JOIN_GAME_SCREEN = "MENU_JOIN_GAME_SCREEN"
    const val MENU_CREATE_GAME_SCREEN = "MENU_CREATE_GAME_SCREEN"
    const val MENU_CREATE_GAME_QUESTION_DETAILS_SCREEN = "MENU_CREATE_GAME_QUESTION_DETAILS_SCREEN"
    const val MENU_ADD_QUESTION_SCREEN = "MENU_ADD_QUESTION_SCREEN"
    const val MENU_RANKING_DASHBOARD_SCREEN = "MENU_RANKING_DASHBOARD_SCREEN"
    const val MENU_YOUR_CURRENT_GAMES_SCREEN = "YOUR_CURRENT_GAMES_SCREEN"

    const val GAME_MAP_SCREEN = "GAME_MAP_SCREEN"
    const val GAME_MAP_ADMIN_SCREEN = "GAME_MAP_ADMIN_SCREEN"
    const val GAME_QR_SCAN_SCREEN = "GAME_QR_SCAN_SCREEN"
    const val GAME_QUESTION_SCREEN = "GAME_QUESTION_SCREEN"
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(
    auth: FirebaseAuth,
    navController: NavHostController = rememberNavController()
) {
    val startDestination: String = if (auth.currentUser != null) {
        Routes.MENU_HOME_SCREEN
    } else {
        Routes.LOGIN_SCREEN
    }

    NavHost(navController, startDestination) {
        composable(Routes.MENU_HOME_SCREEN) {
            MenuHomeScreen(navController, auth)
        }
        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(navController, auth)
        }
        composable(Routes.REGISTER_SCREEN) {
            RegisterScreen(navController, auth, db = FirebaseFirestore.getInstance())
        }
        composable(Routes.RESET_PASSWORD_EMAIL_SCREEN) {
            ResetPasswordEmailScreen(navController)
        }
        composable(Routes.RESET_PASSWORD_DIGIT_CODE_SCREEN) {
            ResetPasswordDigitCodeScreen(navController)
        }
        composable(Routes.RESET_PASSWORD_NEW_PASSWORD_SCREEN) {
            ResetPasswordNewPasswordScreen(navController)
        }
        composable(Routes.GAME_MAP_SCREEN + "/{gameId}") {backStackEntry ->
            GameMapScreen(navController, backStackEntry.arguments!!.getString("gameId")!!)
        }
        composable(Routes.GAME_QR_SCAN_SCREEN + "/{gameId}") {backStackEntry ->
            GameQrCodeScannerScreen(navController, backStackEntry.arguments!!.getString("gameId")!!)
        }
        composable(Routes.MENU_CREATE_GAME_SCREEN + "/{gameId}") { backStackEntry ->
            MenuCreateGameScreen(navController, backStackEntry.arguments!!.getString("gameId")!!)
        }
        composable(Routes.MENU_ADD_QUESTION_SCREEN + "/{gameId}") { backStackEntry ->
            MenuAddQuestionScreen(navController, backStackEntry.arguments!!.getString("gameId")!!)
        }
        composable(Routes.USER_PROFILE_SCREEN) {
            UserProfileScreen(navController)
        }
        composable(Routes.MENU_JOIN_GAME_SCREEN) {
            MenuJoinGameScreen(navController)
        }
        composable(Routes.MENU_RANKING_DASHBOARD_SCREEN) {
            MenuRankingDashboardScreen(navController)
        }
        composable(Routes.GAME_QUESTION_SCREEN + "/{gameId}/{questionId}") { backStackEntry ->
            GameQuestionScreen(navController, backStackEntry.arguments!!.getString("gameId")!!, backStackEntry.arguments!!.getString("questionId")!!)
        }
        composable(Routes.GAME_MAP_ADMIN_SCREEN + "/{gameId}") { backStackEntry ->
            GameMapAdminScreen(navController, backStackEntry.arguments!!.getString("gameId")!!)
        }
        composable(Routes.MENU_YOUR_CURRENT_GAMES_SCREEN) {
            MenuYourCreatedGamesScreen(navController)
        }
        composable(Routes.USER_PROFILE_EDIT_SCREEN) {
            UserProfileEditScreen(navController)
        }
        composable(Routes.MENU_CREATE_GAME_QUESTION_DETAILS_SCREEN + "/{questionId}") {backStackEntry ->
            MenuCreateGameQuestionDetailsScreen(navController, backStackEntry.arguments!!.getString("questionId")!!)
        }
    }
}