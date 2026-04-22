package com.ganeshrashinkar.networkstateminichallange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ganeshrashinkar.networkstateminichallange.ui.theme.NetworkStateMiniChallangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NetworkStateMiniChallangeTheme {
                val viewModel = viewModel<ConnectivityViewModel> {
                    ConnectivityViewModel(
                        connectivityObserver = AndroidConnectivityObserver(
                            context = applicationContext
                        )
                    )
                }

                val isConnected=viewModel.isConnected.collectAsStateWithLifecycle()
                val isAirplaneMode=viewModel.isAirplaneMode.collectAsStateWithLifecycle()

                val connectionStatus=if(isAirplaneMode.value){
                    NetworkState.STATE_AIRPLANE
                }else{
                    if(isConnected.value)
                        NetworkState.STATE_CONNECTED
                    else NetworkState.STATE_DISCONNECTED
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NetworkStateComposable(
                         connectionStatus,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NetworkStateComposable(networkState: NetworkState, modifier: Modifier = Modifier) {
    Column(modifier=modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when(networkState){
                NetworkState.STATE_CONNECTED -> ConnectionCard(R.drawable.connected,R.string.connected)
                NetworkState.STATE_DISCONNECTED -> ConnectionCard(R.drawable.disconnected,R.string.disconnected)
                NetworkState.STATE_AIRPLANE ->ConnectionCard(R.drawable.airplane,R.string.airplane_mode)
            }
        }
}

@Composable
fun ConnectionCard(@DrawableRes img:Int,@StringRes msg:Int){
    Column (verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Box(){
            val brush= Brush.verticalGradient(
                colors =listOf(Color(0xffFFd7e6),Color(0xffFFd7e6).copy(alpha = 0.2f)),

            )
            Box(modifier = Modifier.background(
                brush = brush,
                CircleShape
            )
                .align(Alignment.Center)
                .size(230.dp)
            )
            Image(painterResource(img),
                contentDescription = stringResource(msg),

            )
        }

        Spacer(Modifier.height(8.dp))
        Text(stringResource(msg))
    }
}

enum class NetworkState(){
    STATE_CONNECTED,
    STATE_DISCONNECTED,
    STATE_AIRPLANE
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NetworkStateMiniChallangeTheme {
        NetworkStateComposable(NetworkState.STATE_DISCONNECTED)
    }
}