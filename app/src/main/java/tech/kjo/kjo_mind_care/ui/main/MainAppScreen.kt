package tech.kjo.kjo_mind_care.ui.main

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import tech.kjo.kjo_mind_care.ui.main.blog_detail.BlogPostDetailScreen
import tech.kjo.kjo_mind_care.ui.main.blog.BlogScreen
import tech.kjo.kjo_mind_care.ui.main.blog_form.BlogFormScreen
import tech.kjo.kjo_mind_care.ui.main.home.HomeScreen
import tech.kjo.kjo_mind_care.ui.main.mood.MoodEntryDetail
import tech.kjo.kjo_mind_care.ui.main.mood.MoodTrackerStart
import tech.kjo.kjo_mind_care.ui.main.profile.ProfileScreen
import tech.kjo.kjo_mind_care.ui.main.profile.ProfileViewModel
import tech.kjo.kjo_mind_care.ui.navigation.BottomNavigationBar
import tech.kjo.kjo_mind_care.ui.navigation.Screen
import tech.kjo.kjo_mind_care.ui.navigation.defaultHorizontalEnterTransition
import tech.kjo.kjo_mind_care.ui.navigation.defaultHorizontalExitTransition
import tech.kjo.kjo_mind_care.ui.navigation.defaultHorizontalPopEnterTransition
import tech.kjo.kjo_mind_care.ui.navigation.defaultHorizontalPopExitTransition

@Composable
fun MainAppScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    mainNavController: NavController // Recibe el NavController global para navegar fuera del bottom bar
) {
    val bottomNavController = rememberNavController() // NavController para las pestañas
    Scaffold(
        topBar = {},
        bottomBar = {
            BottomNavigationBar(bottomNavController)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                mainNavController.navigate(Screen.CreateBlogScreen.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Nuevo")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        // NavHost ANIDADO para la navegación dentro del Bottom Bar
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.HomeGraph.route,
            modifier = Modifier.padding(paddingValues),

            // Transiciones de animación
            enterTransition = { defaultHorizontalEnterTransition() },
            exitTransition = { defaultHorizontalExitTransition() },
            popEnterTransition = { defaultHorizontalPopEnterTransition() },
            popExitTransition = { defaultHorizontalPopExitTransition() }
        ) {

            // HOME GRAPH
            navigation(
                startDestination = Screen.HomeStart.route,
                route = Screen.HomeGraph.route
            ) {
                composable(Screen.HomeStart.route) {
                    HomeScreen(
                        onNavigateToNotifications = { mainNavController.navigate(Screen.NotificationsScreen.route) },
                        navController = bottomNavController
                    )
                }
            }

            // BLOG GRAPH
            navigation(
                startDestination = Screen.BlogList.route,
                route = Screen.BlogGraph.route
            ) {
                composable(Screen.BlogList.route) {
                    BlogScreen(
                        onNavigateToBlogPostDetail = { blogId ->
                            bottomNavController.navigate(
                                Screen.BlogPostDetail.createRoute(blogId)
                            )
                        }
                    )
                }

                composable(
                    route = Screen.BlogPostDetail.route,
                        arguments = listOf(navArgument("blogId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val blogId = backStackEntry.arguments?.getString("blogId")
                    if (blogId != null) {
                        BlogPostDetailScreen(
                            blogId = blogId,
                            onNavigateBack = { bottomNavController.popBackStack() },
                            onNavigateToEditBlog = { idToEdit ->
                                // Navega a la ruta de edición, pasando el ID del blog
                                bottomNavController.navigate(Screen.EditBlog.createRoute(idToEdit))
                            }
                        )
                    } else {
                        // TODO: Manejar el caso donde blogId es nulo (ej. mostrar error o volver)
                        Text("Error: ID de blog no proporcionado.")
                    }
                }

                composable(
                    route = Screen.EditBlog.route,
                    arguments = listOf(navArgument("blogId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val blogId = backStackEntry.arguments?.getString("blogId")
                    BlogFormScreen(
                        blogId = blogId,
                        onBlogSaved = {
                            if (blogId != null) {
                                // Navega de nuevo al detalle del blog actualizado y limpia la pila para no apilar.
                                // La idea es que al guardar, el detalle se refresque con los nuevos datos.
                                bottomNavController.navigate(Screen.BlogPostDetail.createRoute(blogId)) {
                                    popUpTo(Screen.BlogPostDetail.route) { inclusive = true } // Elimina todas las instancias anteriores de detalle
                                    launchSingleTop = true // Evita múltiples copias de la misma pantalla si ya está en la parte superior
                                }
                            } else {
                                // Esto no debería ocurrir si se viene de "editar", pero por seguridad
                                bottomNavController.popBackStack(Screen.BlogList.route, inclusive = false)
                            }
                        },
                        onBackClick = { bottomNavController.popBackStack() }
                    )
                }
            }

            // MOOD TRACKING GRAPH
            navigation(
                startDestination = Screen.MoodTrackerStart.route,
                route = Screen.MoodTrackingGraph.route
            ) {
                composable(Screen.MoodTrackerStart.route) {
                    MoodTrackerStart(
                        onRecordMoodClicked = {
                            bottomNavController.navigate(Screen.MoodEntryDetail.route)
                        },
                        onNavigateToMoodEntry = { }
                    )
                }
                composable(
                    route = Screen.MoodEntryDetail.route,
//                        arguments = listOf(navArgument("entryId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val entryId = backStackEntry.arguments?.getString("entryId")
//                        MoodEntryDetailScreen(
//                            entryId = entryId ?: "N/A",
//                            onNavigateBack = { bottomNavController.popBackStack() }
//                        )
                    MoodEntryDetail(
                        onCancel = { bottomNavController.popBackStack() },
                        onSave = { mood, note ->
                            bottomNavController.popBackStack()
                        }
                    )
                }
            }

            // RESOURCES GRAPH
            navigation(
                startDestination = Screen.ResourcesList.route,
                route = Screen.ResourcesGraph.route
            ) {
                composable(Screen.ResourcesList.route) {
//                        ResourcesListScreen(
//                            onNavigateToResourceDetail = { resourceId -> bottomNavController.navigate(Screen.ResourceDetail.createRoute(resourceId)) }
//                        )
                }
                composable(
                    route = Screen.ResourceDetail.route,
//                        arguments = listOf(navArgument("resourceId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val resourceId = backStackEntry.arguments?.getString("resourceId")
//                        ResourceDetailScreen(
//                            resourceId = resourceId ?: "N/A",
//                            onNavigateBack = { bottomNavController.popBackStack() }
//                        )
                }
            }

            // PROFILE GRAPH
            navigation(
                startDestination = Screen.ProfileDetails.route,
                route = Screen.ProfileGraph.route
            ) {
                composable(Screen.ProfileDetails.route) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onEditProfile = {
                            bottomNavController.navigate(Screen.EditProfile.route)
                        },
                        onAccountSettings = {
                            //Logica
                        },
                        onLogout = {
                            mainNavController.navigate(Screen.WelcomeLoginScreen.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.EditProfile.route) {
//                        EditProfileScreen(
//                            onProfileSaved = { bottomNavController.popBackStack() },
//                            onNavigateBack = { bottomNavController.popBackStack() }
//                        )
                }
            }
        }
    }
}
