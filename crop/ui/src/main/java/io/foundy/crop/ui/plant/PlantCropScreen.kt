package io.foundy.crop.ui.plant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import io.foundy.core.designsystem.component.CamstudyText

@Composable
@Destination
fun PlantCropRoute() {
    PlantCropScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantCropScreen() {
    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            CamstudyText(text = "심는화면")
        }
    }
}
