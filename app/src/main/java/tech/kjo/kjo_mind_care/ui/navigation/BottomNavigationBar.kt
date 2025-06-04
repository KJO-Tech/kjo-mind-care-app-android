package tech.kjo.kjo_mind_care.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import tech.kjo.kjo_mind_care.R

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String // Ruta del sub-grafo asociado
)

@Composable
fun BottomNavigationBar(
    bottomNavController: NavHostController,
) {
    val bottomNavItems = listOf(
        BottomNavItem(
            stringResource(R.string.home_bottom_navigation),
            Icons.Default.Home,
            Screen.HomeGraph.route
        ),
        BottomNavItem(
            stringResource(R.string.mood_bottom_navigation),
            Icons.Default.Mood,
            Screen.MoodTrackingGraph.route
        ),

        BottomNavItem(
            stringResource(R.string.blog_bottom_navigation),
            Icons.Default.LocalLibrary,
            Screen.BlogGraph.route
        ),
        BottomNavItem(
            stringResource(R.string.resources_bottom_navigation),
            Icons.AutoMirrored.Filled.List,
            Screen.ResourcesGraph.route
        ),
        BottomNavItem(
            stringResource(R.string.profile_bottom_navigation),
            Icons.Default.Person,
            Screen.ProfileGraph.route
        )
    )

    NavigationBar {
        val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    bottomNavController.navigate(item.route) {
                        popUpTo(bottomNavController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}