package com.squad.update.feature.foryou

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun ForYouRoute(
    modifier: Modifier = Modifier,
//    viewModel: ForYouViewModel = hiltViewModel(),
    onTopicClick: ( String ) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.align( Alignment.Center ),
            text = "For You Screen"
        )
    }
}