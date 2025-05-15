package com.kavyakanaja.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.kavyakanaja.app.ui.navigation.NavGraph
import com.kavyakanaja.app.ui.theme.KavyaKanajaTheme
import com.kavyakanaja.app.viewmodel.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var languageManager: LanguageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KavyaKanajaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val poemViewModel  : PoemViewModel  = hiltViewModel()
                    val poetViewModel  : PoetViewModel  = hiltViewModel()
                    val authViewModel  : AuthViewModel  = hiltViewModel()
                    NavGraph(
                        navController   = navController,
                        poemViewModel   = poemViewModel,
                        poetViewModel   = poetViewModel,
                        authViewModel   = authViewModel,
                        languageManager = languageManager
                    )
                }
            }
        }
    }
}