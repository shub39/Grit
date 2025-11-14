package com.shub39.grit.core.presentation.settings.ui.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.shub39.grit.R
import com.shub39.grit.core.shared_ui.PageFill
import grit.shared.core.generated.resources.Res
import grit.shared.core.generated.resources.about_libraries
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutLibraries(
    onNavigateBack: () -> Unit
) = PageFill {
    Column(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxSize()
    ) {
        val libraries by produceLibraries(R.raw.aboutlibraries)

        TopAppBar(
            title = { Text(stringResource(Res.string.about_libraries)) },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Navigate Back"
                    )
                }
            }
        )

        LibrariesContainer(
            libraries = libraries,
            typography = MaterialTheme.typography,
            modifier = Modifier.fillMaxSize()
        )
    }
}