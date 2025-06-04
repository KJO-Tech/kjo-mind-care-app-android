package tech.kjo.kjo_mind_care.ui.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

// Conjunto de animaciones para pantallas que "se superponen" (vienen de la derecha)
fun defaultModalEnterTransition() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(400)
) + fadeIn(animationSpec = tween(400))

fun defaultModalExitTransition() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(400)
) + fadeOut(animationSpec = tween(400))

fun defaultModalPopEnterTransition() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(400)
) + fadeIn(animationSpec = tween(400))

fun defaultModalPopExitTransition() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(400)
) + fadeOut(animationSpec = tween(400))

// Conjunto de animaciones para transiciones "laterales" (entre pestañas)
fun defaultHorizontalEnterTransition() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(450)
) + fadeIn(animationSpec = tween(450))

fun defaultHorizontalExitTransition() = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(450)
) + fadeOut(animationSpec = tween(450))

fun defaultHorizontalPopEnterTransition() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(450)
) + fadeIn(animationSpec = tween(450))

fun defaultHorizontalPopExitTransition() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(450)
) + fadeOut(animationSpec = tween(450))

// Extensión para simplificar la declaración de composables con animaciones modales
fun NavGraphBuilder.modalComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = { defaultModalEnterTransition() },
        exitTransition = { defaultModalExitTransition() },
        popEnterTransition = { defaultModalPopEnterTransition() },
        popExitTransition = { defaultModalPopExitTransition() },
        content = content
    )
}