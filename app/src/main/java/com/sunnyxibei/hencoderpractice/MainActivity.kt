package com.sunnyxibei.hencoderpractice

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity // Changed from AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.* // Using Material 3
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView

// It's good practice to define a theme for Compose
// For now, a basic MaterialTheme will be used.
// Usually, this would be in a separate Theme.kt file.
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(), // Or darkColorScheme()
        content = content
    )
}

class MainActivity : ComponentActivity() { // Changed from AppCompatActivity

    private val handler = Handler(Looper.getMainLooper())
    private var mapViewInstance: MapView? = null // To hold the MapView instance for animation control
    private var animatorSet: AnimatorSet? = null // To control the animator set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        var showMenu by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Google Map") },
                                onClick = {
                                    mapViewInstance?.setBitmap(
                                        BitmapFactory.decodeResource(
                                            context.resources,
                                            R.drawable.google_map
                                        )
                                    )
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("FlipBoard") },
                                onClick = {
                                    mapViewInstance?.setBitmap(
                                        BitmapFactory.decodeResource(
                                            context.resources,
                                            R.drawable.flip_board
                                        )
                                    )
                                    showMenu = false
                                }
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                MapViewComposable()
            }
        }
    }

    @Composable
    fun MapViewComposable() {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(context).apply {
                    mapViewInstance = this
                    val animator1 = ObjectAnimator.ofFloat(this, "degreeY", 0f, -45f).apply {
                        duration = 1000
                        startDelay = 500
                    }
                    val animator2 = ObjectAnimator.ofFloat(this, "degreeZ", 0f, 270f).apply {
                        duration = 800
                        startDelay = 500
                    }
                    val animator3 = ObjectAnimator.ofFloat(this, "fixDegreeY", 0f, 30f).apply {
                        duration = 500
                        startDelay = 500
                    }

                    animatorSet = AnimatorSet().apply {
                        addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                handler.postDelayed({
                                    mapViewInstance?.let {
                                        if (!isDestroyed && !isFinishing) {
                                            it.reset()
                                            start()
                                        }
                                    }
                                }, 500)
                            }
                        })
                        playSequentially(animator1, animator2, animator3)
                        start()
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        animatorSet?.cancel()
    }
}
