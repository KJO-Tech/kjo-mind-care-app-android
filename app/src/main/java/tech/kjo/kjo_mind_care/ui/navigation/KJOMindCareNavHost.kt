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
import tech.kjo.kjo_mind_care.ui.main.blog_form.BlogFormScreen
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

        modalComposable(Screen.WelcomeLoginScreen.route) {
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

        modalComposable(Screen.RegisterScreen.route) {
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
            MainAppScreen(mainNavController = navController)
        }

        // Pantalla superpuesta/modal para crear (con su TopBar de regreso) y que se puede superponer sobre el flujo principal.
        modalComposable(
            Screen.CreateBlogScreen.route
        ) {
            BlogFormScreen(
                blogId = null,
                onBlogSaved = {
                    navController.popBackStack(
                        Screen.BlogList.route,
                        inclusive = false
                    )
                }, // Vuelve a la pantalla de blogs
                onBackClick = { navController.popBackStack() }
            )
        }

        modalComposable(Screen.NotificationsScreen.route) {
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
fun CreateBlogScreenPreview() {
    KJOMindCareTheme {
        BlogFormScreen(
            blogId = null,
            onBlogSaved = { },
            onBackClick = { }
        )
    }
}
