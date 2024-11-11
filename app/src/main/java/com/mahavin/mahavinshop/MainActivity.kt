package com.mahavin.mahavinshop

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.mahavin.mahavinshop.ui.theme.MAHAVINSHOPTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAHAVINSHOPTheme {
                val context = LocalContext.current
                var screen by remember {
                    mutableStateOf(0)
                }
                LaunchedEffect(Unit) {
                    delay(3000)
                    screen = 1
                }
                if (screen == 0) {
                    SplashScreenView()
                } else {
                    MainScreen(context)
                }
            }
        }
    }
}

@Composable
fun SplashScreenView() {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(model = R.drawable.logo_data, contentDescription = "Animated logo" , imageLoader = imageLoader)
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(100.dp))
        LinearProgressIndicator()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(context: Context) {
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(checkInternetConnection(context)) }

    // Scaffold UI
    Scaffold(modifier = Modifier.fillMaxSize(), floatingActionButton = {
        if (isConnected) {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 70.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_message_24),
                        contentDescription = "Message"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Contact Us", fontSize = 24.sp, fontWeight = FontWeight.W500)
                }
            }
        }
    }) { innerPadding ->

        // Show NoInternetScreen if there's no internet
        if (!isConnected) {
            NoInternetScreen(
                onRetry = {
                    isConnected = checkInternetConnection(context) // Retry checking connection
                }
            )
        } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    WebViewScreen(url = "https://shop.mahavinbd.com/")

                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = bottomSheetState
                        ) {
                            ContactUsBottomSheetContent(context)
                        }
                    }
                }
        }
    }
}

// Internet connection checker
fun checkInternetConnection(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}


@Composable
fun ContactUsBottomSheetContent(context: Context) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Contact Us", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            IconButton(onClick = { openPhone(context) }) {
                Image(
                    painter = painterResource(id = R.drawable.telephone),
                    contentDescription = "Phone"
                )
            }
            IconButton(onClick = { openMessenger(context) }) {
                Image(
                    painter = painterResource(id = R.drawable.messenger),
                    contentDescription = "Messenger"
                )
            }
            IconButton(onClick = { openWhatsApp(context) }) {
                Image(
                    painter = painterResource(id = R.drawable.whatsapp),
                    contentDescription = "WhatsApp"
                )
            }
            IconButton(onClick = { openTelegram(context) }) {
                Image(
                    painter = painterResource(id = R.drawable.telegram),
                    contentDescription = "Telegram"
                )
            }

        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    val webView = rememberWebViewWithLifecycle() // This is the WebView state holder

    Box(modifier = Modifier.fillMaxSize()) {
        // WebView Composable
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?
                        ) {
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                        }
                    }
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            },
            update = { webViewRef ->
                webView.value = webViewRef
                webViewRef.loadUrl(url)
            }
        )

        // Display loader when loading
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
    }

    // Handle back button
    BackHandler {
        webView.value?.let { webView ->
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                showDialog = true
            }
        }
    }

    // Exit confirmation dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text("Exit Shop")
            },
            text = {
                Text("Are you sure you want to exit?")
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    (context as ComponentActivity).finish() // Close the app
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    webView.value?.loadUrl(url) // Reload the initial page if needed
                }) {
                    Text("No")
                }
            }
        )
    }
}

// Utility functions for phone, Telegram, WhatsApp, and Messenger opening remain the same

@Composable
fun rememberWebViewWithLifecycle(): MutableState<WebView?> {
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> webViewState.value?.onPause()
                Lifecycle.Event.ON_RESUME -> webViewState.value?.onResume()
                Lifecycle.Event.ON_DESTROY -> webViewState.value?.destroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    return webViewState
}

fun openPhone(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:+8801953563498")
    context.startActivity(intent)
}

fun openTelegram(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("https://t.me/mahavinshop") // Replace with your Telegram link
    context.startActivity(intent)
}

fun openWhatsApp(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("https://wa.me/message/UDCBZUGI4SKOE1") // Replace with your WhatsApp link
    context.startActivity(intent)
}

fun openMessenger(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse("https://m.me/mahavinshop") // Replace with your Messenger link
    context.startActivity(intent)
}

@Composable
fun NoInternetScreen(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // Minimal background color
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Replace with a minimal error image, ensure you have a drawable
            val image: Painter = painterResource(id = R.drawable.no_wifi)
            Image(
                painter = image,
                contentDescription = "No Internet",
                modifier = Modifier
                    .size(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Oops! No Internet Connection",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Check your connection and try again",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Retry button
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Retry",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
