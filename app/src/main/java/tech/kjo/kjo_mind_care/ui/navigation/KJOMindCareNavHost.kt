package tech.kjo.kjo_mind_care.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.kjo.kjo_mind_care.ui.auth.login.LoginScreen
import tech.kjo.kjo_mind_care.ui.auth.register.RegisterScreen
import tech.kjo.kjo_mind_care.ui.main.MainAppScreen
import tech.kjo.kjo_mind_care.ui.main.blog.CreateNewEntryScreen
import tech.kjo.kjo_mind_care.ui.main.notifications.NotificationsScreen
import tech.kjo.kjo_mind_care.ui.splash.SplashScreen
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme

@Composable
fun KJOMindCareNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route) {

        composable(Screen.SplashScreen.route) {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate(Screen.WelcomeLoginScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                },
                onNavigateToMainApp = {
                    navController.navigate(Screen.MainAppScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.WelcomeLoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.MainAppScreen.route) {
                        popUpTo(Screen.WelcomeLoginScreen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.RegisterScreen.route)
                }
            )
        }

        composable(Screen.RegisterScreen.route) {
            RegisterScreen(
                onRegistrationSuccess = {
                    // Quizás ir a main, o de vuelta al login
                    navController.navigate(Screen.MainAppScreen.route) {
                        popUpTo(Screen.WelcomeLoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Main App Screen
        composable(Screen.MainAppScreen.route) {
            // Este composable es el "contenedor" del flujo principal
            MainAppScreen(mainNavController = navController) // Pasa el NavController global
        }

        // Pantalla superpuesta/modal para crear (con su TopBar de regreso) y que se puede superponer sobre el flujo principal.
        composable(Screen.CreateNewEntryScreen.route) {
            CreateNewEntryScreen(
                onCreationComplete = { navController.popBackStack() }, // Volver atrás cuando se complete
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.NotificationsScreen.route) {
            NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    KJOMindCareTheme {
        SplashScreen(
            onNavigateToWelcome = { },
            onNavigateToMainApp = { }
        )
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    KJOMindCareTheme {
        LoginScreen(
            onLoginSuccess = { },
            onNavigateToRegister = { }
        )
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    KJOMindCareTheme {
        RegisterScreen(
            onRegistrationSuccess = { },
            onNavigateBack = { }
        )
    }
}

@Preview
@Composable
fun MainAppScreenPreview() {
    KJOMindCareTheme {
        MainAppScreen(mainNavController = rememberNavController())
    }
}

@Preview
@Composable
fun NotificationsScreenPreview() {
    KJOMindCareTheme {
        NotificationsScreen(onNavigateBack = { })
    }
}

@Preview
@Composable
fun CreateNewEntryScreenPreview() {
    KJOMindCareTheme {
        CreateNewEntryScreen(onCreationComplete = { }, onNavigateBack = { })
    }
}