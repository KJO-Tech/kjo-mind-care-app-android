package tech.kjo.kjo_mind_care.ui.navigation

sealed class Screen(val route: String) {
    // Rutas Globales
    object SplashScreen : Screen("splash_screen")
    object WelcomeLoginScreen : Screen("welcome_login_screen")
    object RegisterScreen : Screen("register_screen")
    object MainAppScreen : Screen("main_app_screen")

    object CreateBlogScreen : Screen("create_blog_screen")
    object NotificationsScreen : Screen("notifications_screen") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/notifications"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/notifications"
    }

    // Sub-Grafos para cada pestaña del Bottom Navigation Bar
    object HomeGraph : Screen("home_graph")
    object BlogGraph : Screen("blog_graph")
    object MoodTrackingGraph : Screen("mood_tracking_graph")
    object ResourcesGraph : Screen("resources_graph")
    object ProfileGraph : Screen("profile_graph")

    // Rutas específicas dentro de cada sub-grafo
    object HomeStart : Screen("home_start") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/home"
    }

    object ExerciseDetail : Screen("exercise_detail/{exerciseId}") {
        fun createRoute(exerciseId: String) = "exercise_detail/$exerciseId"
    }

    object BlogList : Screen("blog_list") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/community"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/blogs"
    }

    object BlogPostDetail : Screen("blog_post_detail/{blogId}") {
        fun createRoute(blogId: String) = "blog_post_detail/$blogId"

        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/community/post/{blogId}" // Tu dominio web
        const val DEEPLINK_APP_PATTERN =
            "kjoapp://app.kjo-mind-care.com/blog/{blogId}" // Tu esquema personalizado
    }

    object EditBlog : Screen("edit_blog/{blogId}") {
        fun createRoute(blogId: String) = "edit_blog/$blogId"

        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/community/post?id={blogId}"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/blog/{blogId}/edit"
    }

    object MoodTrackerStart : Screen("mood_tracker_start") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/mood/history"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/mood-tracker"
    }

    object MoodEntryDetail : Screen("mood_entry_detail?moodId={moodId}") {
        fun createRoute(moodId: String?) = "mood_entry_detail?moodId=$moodId"

        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/mood/register?moodId={entryId}"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/mood-entry/{entryId}"
    }

    object ResourcesList : Screen("resources_list") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/exercises"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/resources"
    }

    object ResourceDetail : Screen("resource_detail/{resourceId}") {
        fun createRoute(resourceId: String) = "resource_detail/$resourceId"

        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/exercises/{resourceId}"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/resource/{resourceId}"
    }

    object ProfileDetails : Screen("profile_details") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/profile"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/profile"
    }

    object EditProfile : Screen("edit_profile") {
        const val DEEPLINK_WEB_PATTERN = "https://kjomindcare.netlify.app/app/profile/edit"
        const val DEEPLINK_APP_PATTERN = "kjoapp://app.kjo-mind-care.com/profile/edit"
    }
}
