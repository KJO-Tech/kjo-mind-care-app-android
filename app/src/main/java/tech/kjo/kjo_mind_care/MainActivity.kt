package tech.kjo.kjo_mind_care

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.kjo.kjo_mind_care.ui.login.ui.LoginScreen
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import tech.kjo.kjo_mind_care.ui.login.ui.LoginViewModel
import tech.kjo.kjo_mind_care.ui.register.ui.RegisterScreen
import tech.kjo.kjo_mind_care.ui.register.ui.RegisterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KJOMindCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }

}

