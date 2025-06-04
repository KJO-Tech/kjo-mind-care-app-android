package tech.kjo.kjo_mind_care

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import tech.kjo.kjo_mind_care.ui.navigation.KJOMindCareNavHost
import tech.kjo.kjo_mind_care.ui.theme.KJOMindCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KJOMindCareTheme {
                KJOMindCareNavHost()
            }
        }
    }
}
