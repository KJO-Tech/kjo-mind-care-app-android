package tech.kjo.kjo_mind_care.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import kotlinx.coroutines.launch
import tech.kjo.kjo_mind_care.ui.main.blog.BlogScreen
import tech.kjo.kjo_mind_care.ui.main.home.HomeScreen
import tech.kjo.kjo_mind_care.ui.navigation.BottomNavigationBar
import tech.kjo.kjo_mind_care.ui.navigation.Screen

@Composable
fun MainAppScreen(
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
                mainNavController.navigate(Screen.CreateNewEntryScreen.route)
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
        ) {

            // HOME GRAPH
            navigation(startDestination = Screen.HomeStart.route, route = Screen.HomeGraph.route) {
                composable(Screen.HomeStart.route) {
                    HomeScreen(
                        onNavigateToNotifications = { mainNavController.navigate(Screen.NotificationsScreen.route) },
                    )
                }
            }

            // BLOG GRAPH
            navigation(startDestination = Screen.BlogList.route, route = Screen.BlogGraph.route) {
                composable(Screen.BlogList.route) {
                        BlogScreen(
                            onNavigateToBlogPostDetail = { postId -> bottomNavController.navigate(Screen.BlogPostDetail.createRoute(postId)) }
                        )
                }
                composable(
                    route = Screen.BlogPostDetail.route,
//                        arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId")
//                        BlogPostDetailScreen(
//                            postId = postId ?: "N/A",
//                            onNavigateBack = { bottomNavController.popBackStack() }
//                        )
                }
            }

            // MOOD TRACKING GRAPH
            navigation(
                startDestination = Screen.MoodTrackerStart.route,
                route = Screen.MoodTrackingGraph.route
            ) {
                composable(Screen.MoodTrackerStart.route) {
//                        MoodTrackingScreen(
//                            onNavigateToMoodEntryDetail = { entryId -> bottomNavController.navigate(Screen.MoodEntryDetail.createRoute(entryId)) }
//                        )
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
//                        ProfileScreen(
//                            onNavigateToEditProfile = { bottomNavController.navigate(Screen.EditProfile.route) }
//                        )
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
