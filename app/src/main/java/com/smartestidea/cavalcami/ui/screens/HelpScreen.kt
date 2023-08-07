package com.smartestidea.cavalcami.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.smartestidea.cavalcami.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.help)) },navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))
            }
        })
        }, modifier = Modifier.imePadding()) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(modifier= Modifier
            .padding(innerPadding)
            .scrollable(scrollState, Orientation.Vertical)
            .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically)){
            Row {
                Text(text = "CAVAL", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(text = "CAMI", color = MaterialTheme.colorScheme.secondary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Box{
                Column() {
                    Card(modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(IntrinsicSize.Min),) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(ButtonDefaults.MinHeight / 2)) {
                            Text(text = stringResource(id = R.string.version), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier= Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            Text(text = stringResource(id = R.string.app_long_desc), fontSize = 14.sp, fontWeight = FontWeight.Light, modifier= Modifier.fillMaxWidth(), textAlign = TextAlign.Justify, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier= Modifier.height(10.dp))
                            TextButton(onClick = {  },modifier= Modifier, contentPadding = PaddingValues(0.dp)) {
                                Image(painter = painterResource(id = R.drawable.round_perm_device_information_24), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "How does it work?", color = MaterialTheme.colorScheme.onBackground)
                            }
                            TextButton(onClick = {  }, contentPadding = PaddingValues(0.dp)) {
                                Image(painter = painterResource(id = R.drawable.round_policy_24), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "Terms and conditions", color = MaterialTheme.colorScheme.onBackground)
                            }
                            TextButton(onClick = {  }, contentPadding = PaddingValues(0.dp)) {
                                Image(painter = painterResource(id = R.drawable.round_security_24), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "Security", color = MaterialTheme.colorScheme.onBackground)
                            }
                            TextButton(onClick = {  }, contentPadding = PaddingValues(0.dp)) {
                                Image(painter = painterResource(id = R.drawable.baseline_share_24), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "Share the App", color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                        Spacer(modifier= Modifier.height(10.dp))
                    }
                    Spacer(modifier = Modifier.height(ButtonDefaults.MinHeight/2))
                }
                Button(onClick = {  }, modifier = Modifier.align(Alignment.BottomCenter)) {
                    Image(painter = painterResource(id = R.drawable.operator_svgrepo_com), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = stringResource(id = R.string.operator))
                }
            }
            Spacer(modifier= Modifier.height(10.dp))
            Text(text = stringResource(id = R.string.contact_us), fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)){
                IconButton(onClick = { /*TODO*/ }) {
                    Image(painter = painterResource(id = R.drawable.gmail_icon__2020_), contentDescription = null, )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Image(painter = painterResource(id = R.drawable.whatsapp_symbol_logo_svgrepo_com), contentDescription = null,)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Image(painter = painterResource(id = R.drawable.telegram_logo), contentDescription = null, )
                }
            }
        }
    }
}