package tech.kjo.kjo_mind_care.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import tech.kjo.kjo_mind_care.ui.auth.login.LoginScreen
import tech.kjo.kjo_mind_care.ui.auth.register.RegisterScreen
import tech.kjo.kjo_mind_care.ui.main.MainAppScreen
import tech.kjo.kjo_mind_care.ui.main.blog_form.BlogFormScreen
import tech.kjo.kjo_mind_care.ui.main.notifications.NotificationsScreen
import tech.kjo.kjo_mind_care.ui.main.profile.ProfileViewModel
import tech.kjo.kjo_mind_care.ui.splash.SplashScreen
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme

@Composable
fun KJOMindCareNavHost(profileViewModel: ProfileViewModel) {
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
        composable(
            route = Screen.MainAppScreen.route + "?deepLinkRoute={deepLinkRoute}",
            arguments = listOf(navArgument("deepLinkRoute") {
                type = NavType.StringType
                nullable = true // Puede ser nulo si no hay deep link
                defaultValue = null
            }),
            deepLinks = listOf(
                navDeepLink { uriPattern = Screen.BlogPostDetail.DEEPLINK_WEB_PATTERN },
                navDeepLink { uriPattern = Screen.BlogPostDetail.DEEPLINK_APP_PATTERN },
                // Asegúrate de que MoodEntryDetail también tenga sus deep links aquí si pueden iniciar la app
                // navDeepLink { uriPattern = Screen.MoodEntryDetail.DEEPLINK_WEB_PATTERN },
                // navDeepLink { uriPattern = Screen.MoodEntryDetail.DEEPLINK_APP_PATTERN },
            )
        ) { backStackEntry ->
            val deepLinkRequestUri = backStackEntry.arguments?.getString("deepLinkRoute")
                ?: backStackEntry.arguments?.getString("android.intent.extra.REFERRER")

            MainAppScreen(
                mainNavController = navController,
                profileViewModel = profileViewModel,
                initialDeepLinkRoute = deepLinkRequestUri
            )
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { route ->
                    when {
                        route.startsWith(Screen.BlogPostDetail.route.substringBefore("/{")) ||
                                route.startsWith(Screen.MoodEntryDetail.route.substringBefore("/{")) -> {
                            // Si es una ruta que pertenece al NavHost interno de MainAppScreen
                            navController.navigate(
                                "${Screen.MainAppScreen.route}?deepLinkRoute=" + Uri.encode(route)
                            ) {
                                // Pop up para limpiar la pila si es necesario
                                popUpTo(Screen.MainAppScreen.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        }

                        else -> {
                            // Para otras rutas globales (ej. CreateBlogScreen, o incluso NotificationsScreen si tuviera un botón que navegara a sí misma)
                            navController.navigate(route)
                        }
                    }

                }
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
        NotificationsScreen(onNavigateBack = { }, onNavigateToRoute = { })
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
