package com.smartestidea.cavalcami

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.parse.ParseUser
import com.smartestidea.cavalcami.ui.screens.CavalCamiApp
import com.smartestidea.cavalcami.ui.screens.Login
import com.smartestidea.cavalcami.ui.screens.USER_AGENT
import com.smartestidea.cavalcami.ui.theme.CavalCamiTheme
import com.smartestidea.cavalcami.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel:UserViewModel by viewModels()
    private var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().apply {
            load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
            userAgentValue = USER_AGENT
        }
        setContent {
            CavalCamiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    PresentationPager()

                    val user = ParseUser.getCurrentUser()
                    if(user != null) CavalCamiApp(userViewModel){
                        mapView = it
                    } else Login(userViewModel)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
}
