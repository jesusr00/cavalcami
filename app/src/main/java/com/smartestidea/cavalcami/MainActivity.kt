package com.smartestidea.cavalcami

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.parse.ParseUser
import com.smartestidea.cavalcami.ui.screens.CavalCamiApp
import com.smartestidea.cavalcami.ui.screens.Login
import com.smartestidea.cavalcami.ui.screens.USER_AGENT
import com.smartestidea.cavalcami.ui.stateflows.MainUIState
import com.smartestidea.cavalcami.ui.theme.CavalCamiTheme
import com.smartestidea.cavalcami.ui.viewmodels.TripViewModel
import com.smartestidea.cavalcami.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val userViewModel:UserViewModel by viewModels()
    private val tripViewModel: TripViewModel by viewModels()
    private var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = applicationContext
        Configuration.getInstance().apply {
            load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
            userAgentValue = USER_AGENT
        }
        setContent {
            val uiState by userViewModel.mainUIState.collectAsState()
            CavalCamiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    PresentationPager()

                    if((uiState == MainUIState.Success || uiState == MainUIState.Idle) && ParseUser.getCurrentUser()!=null)
                        CavalCamiApp(userViewModel, tripViewModel){ mapView = it }
                    else
                        Login(userViewModel)
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
