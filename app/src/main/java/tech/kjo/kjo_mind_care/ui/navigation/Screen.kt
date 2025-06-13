package tech.kjo.kjo_mind_care.ui.navigation

sealed class Screen(val route: String) {
    // Rutas Globales
    object SplashScreen : Screen("splash_screen")
    object WelcomeLoginScreen : Screen("welcome_login_screen")
    object RegisterScreen : Screen("register_screen")
    object MainAppScreen : Screen("main_app_screen")

    object CreateBlogScreen : Screen("create_blog_screen")
    object NotificationsScreen : Screen("notifications_screen")

    // Sub-Grafos para cada pestaña del Bottom Navigation Bar
    object HomeGraph : Screen("home_graph")
    object BlogGraph : Screen("blog_graph")
    object MoodTrackingGraph : Screen("mood_tracking_graph")
    object ResourcesGraph : Screen("resources_graph")
    object ProfileGraph : Screen("profile_graph")

    // Rutas específicas dentro de cada sub-grafo
    object HomeStart : Screen("home_start")

    object BlogList : Screen("blog_list")
    object BlogPostDetail : Screen("blog_post_detail/{blogId}") {
        fun createRoute(blogId: String) = "blog_post_detail/$blogId"
    }
    object EditBlog: Screen("edit_blog/{blogId}") {
        fun createRoute(blogId: String) = "edit_blog/$blogId"
    }

    object MoodTrackerStart : Screen("mood_tracker_start")
    object MoodEntryDetail : Screen("mood_entry_detail/{entryId}") {
        fun createRoute(entryId: String) = "mood_entry_detail/$entryId"
    }

    object ResourcesList : Screen("resources_list")
    object ResourceDetail : Screen("resource_detail/{resourceId}") {
        fun createRoute(resourceId: String) = "resource_detail/$resourceId"
    }

    object ProfileDetails : Screen("profile_details")
    object EditProfile : Screen("edit_profile")
}

