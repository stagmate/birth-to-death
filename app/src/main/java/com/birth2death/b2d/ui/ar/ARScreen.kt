package com.birth2death.b2d.ui.ar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.sceneview.ar.ARScene
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Position
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.node.Node

@Composable
fun ARScreen(
    taskId: String?,
    onBack: () -> Unit
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    var childNodes by remember { mutableStateOf<List<Node>>(emptyList()) }

    LaunchedEffect(Unit) {
        val instance = modelLoader.loadModelInstance(
            fileLocation = "dragon_red.glb"
        )
        if (instance != null) {
            val node = ModelNode(
                modelInstance = instance,
                scaleToUnits = 0.5f
            ).apply {
                position = Position(0.0f, 0.0f, -1.0f)
            }
            childNodes = listOf(node)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            childNodes = childNodes,
            planeRenderer = true
        )
        
        FloatingActionButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
    }
}
