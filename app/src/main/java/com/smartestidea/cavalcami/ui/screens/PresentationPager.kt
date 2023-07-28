package com.smartestidea.cavalcami.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartestidea.cavalcami.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class PresentationPage(
    val title:String,
    val desc:String,
    val img:Int,
)
val presentationPages = listOf(
    PresentationPage("First","first desc", R.drawable.undraw_order_ride_re_372k),
    PresentationPage("Second","second desc", R.drawable.undraw_map_re_60yf),
    PresentationPage("Mark in te Map","mark in the map your route with your chofer and lorem ipsum etcccc...ipsum lorum with your chofer", R.drawable.undraw_map_re_60yf),
)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PresentationPager(){
    val pagerState = rememberPagerState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val scope  = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally,modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Text(text = "CAVAL", color = MaterialTheme.colorScheme.primary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text(text = "CAMI", color = MaterialTheme.colorScheme.secondary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Text(text = stringResource(id = R.string.app_desc),
                Modifier
                    .alpha(0.6f)
                    .fillMaxWidth(0.6f), fontSize = 12.sp, textAlign = TextAlign.Center)
        }
        HorizontalPager(pageCount = presentationPages.size, state = pagerState, modifier = Modifier.fillMaxWidth()) {
            Image(painter = painterResource(id = presentationPages[it].img), contentDescription = presentationPages[it].title, modifier = Modifier
                .widthIn(max = 250.dp)
                .heightIn(250.dp)
                .align(Alignment.End) )
        }
                Row(modifier= Modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    repeat(3) {
                        Canvas(modifier = Modifier.size(10.dp),onDraw = {drawCircle(color = if (pagerState.currentPage == it) primaryColor else primaryColor.copy(alpha = 0.2f))})
                    }
                }
        Column(Modifier.fillMaxWidth(0.8f), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = presentationPages[pagerState.currentPage].title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(text = presentationPages[pagerState.currentPage].desc, Modifier.alpha(0.8f), textAlign = TextAlign.Center)
        }
                ElevatedButton(onClick = {
                    if(pagerState.currentPage != presentationPages.size-1){
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage+1)
                        }
                    }
                }, modifier = Modifier.fillMaxWidth(0.6f)) {
                    Text(text = if(pagerState.currentPage != presentationPages.size-1) stringResource(id = R.string.continue_) else stringResource(id = R.string.get_started), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(5.dp))
                }
    }

}