package tech.kjo.kjo_mind_care.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import kotlinx.coroutines.delay
import tech.kjo.kjo_mind_care.ui.auth.login.LoginScreen
import tech.kjo.kjo_mind_care.ui.auth.register.RegisterScreen
import tech.kjo.kjo_mind_care.ui.main.MainAppScreen
import tech.kjo.kjo_mind_care.ui.main.blog_form.BlogFormScreen
import tech.kjo.kjo_mind_care.ui.main.notifications.NotificationsScreen
import tech.kjo.kjo_mind_care.ui.main.profile.ProfileViewModel
import tech.kjo.kjo_mind_care.ui.splash.SplashScreen
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme
import tech.kjo.kjo_mind_care.utils.NotificationUtils

@Composable
fun KJOMindCareNavHost(profileViewModel: ProfileViewModel, deepLinkIntent: Intent? = null) {
    val navController = rememberNavController()

    LaunchedEffect(deepLinkIntent) {
        deepLinkIntent?.let { intent ->
            // Si el intent tiene un action de VIEW y data URI, es un deep link
            if (intent.data != null) {
                val dataUri = intent.data

                val deepLinkRouteFromNotification = intent.getStringExtra(NotificationUtils.EXTRA_DEEPLINK_ROUTE)
                    ?: dataUri?.getQueryParameter("deepLinkRoute")

                // La ruta final a la que queremos navegar dentro de la app
                val finalTargetRoute: String? = when {
                    !deepLinkRouteFromNotification.isNullOrBlank() -> deepLinkRouteFromNotification

                    dataUri?.toString()?.startsWith(Screen.BlogPostDetail.DEEPLINK_APP_PATTERN.substringBefore("/{")) == true -> {
                        // Extrae el ID del blog del URI para reconstruir la ruta interna
                        dataUri.lastPathSegment?.let { blogId ->
                            Screen.BlogPostDetail.createRoute(blogId)
                        }
                    }
                    dataUri?.toString()?.startsWith(Screen.MoodEntryDetail.DEEPLINK_APP_PATTERN.substringBefore("/{")) == true -> {
                        dataUri.lastPathSegment?.let { entryId ->
                            Screen.MoodEntryDetail.createRoute(entryId)
                        }
                    }
                    dataUri?.toString() == Screen.NotificationsScreen.DEEPLINK_APP_PATTERN -> {
                        Screen.NotificationsScreen.route
                    }
                    // Agrega aquí más casos para otros deep links directos si los tienes
                    else -> null // No se pudo determinar una ruta válida de deep link
                }

                if (!finalTargetRoute.isNullOrBlank()) {
                    // Ahora, navega usando el navController global
                    when {
                        finalTargetRoute.startsWith(Screen.BlogPostDetail.route.substringBefore("/{")) ||
                                finalTargetRoute.startsWith(Screen.MoodEntryDetail.route.substringBefore("/{")) -> {
                            navController.navigate(
                                "${Screen.MainAppScreen.route}?deepLinkRoute=" + Uri.encode(finalTargetRoute)
                            ) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        // Ruta que va directamente a NotificationsScreen
                        finalTargetRoute == Screen.NotificationsScreen.route -> {
                            navController.navigate(Screen.NotificationsScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        // Agrega otros casos de deep links globales aquí si es necesario
                        else -> {
                            // En caso de una ruta inesperada, navega a la pantalla principal como fallback
                            navController.navigate(Screen.MainAppScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }

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
        ) { backStackEntry ->
            val deepLinkRoute = backStackEntry.arguments?.getString("deepLinkRoute")

            MainAppScreen(
                mainNavController = navController,
                profileViewModel = profileViewModel,
                initialDeepLinkRoute = deepLinkRoute
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
                onNavigateBack = { navController.navigate(Screen.MainAppScreen.route) },
                onNavigateToRoute = { route ->
                    when {
                        // Rutas que van al MainAppScreen
                        route.startsWith(Screen.BlogPostDetail.route.substringBefore("/{")) ||
                                route.startsWith(Screen.MoodEntryDetail.route.substringBefore("/{")) ||
                                route.startsWith(Screen.ResourceDetail.route.substringBefore("/{")) ||
                                route == Screen.MoodTrackerStart.route ||
                                route == Screen.ProfileDetails.route ||
                                route == Screen.HomeStart.route ||
                                route == Screen.BlogList.route ||
                                route == Screen.ResourcesList.route -> {
                            navController.navigate(
                                "${Screen.MainAppScreen.route}?deepLinkRoute=" + Uri.encode(route)
                            ) {
                                popUpTo(Screen.MainAppScreen.route) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                                restoreState = false
                            }
                        }
                        // Rutas globales
                        else -> {
                            navController.navigate(route) {
                                launchSingleTop = true
                            }
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
