package app.pawpaws

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import app.pawpaws.core.navigation.AppNavigation
import app.pawpaws.core.theme.PawPawsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PawPawsTheme {

                val navController = rememberNavController()

                AppNavigation(navController = navController)
            }
        }
    }
}