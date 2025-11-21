package ru.dmitry.callblocker.ui.phonepatterns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.ui.phonepatterns.composables.PhonePatternBottomSheet
import ru.dmitry.callblocker.ui.phonepatterns.composables.PhonePatternItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePatternsScreen(
    viewModel: PhonePatternsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var bottomSheetPattern by remember { mutableStateOf<PhonePattern?>(null) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cancel)
                        )
                    }

                    Text(
                        text = stringResource(id = R.string.phone_patterns_screen_title),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            items(uiState.patterns) { pattern ->
                PhonePatternItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    pattern = pattern,
                    onClick = {
                        bottomSheetPattern = pattern
                        isBottomSheetOpen = true
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                bottomSheetPattern = null
                isBottomSheetOpen = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_phone_pattern)
            )
        }
    }

    if (isBottomSheetOpen) {
        PhonePatternBottomSheet(
            pattern = bottomSheetPattern,
            onDismiss = {
                bottomSheetPattern = null
                isBottomSheetOpen = false
            },
            onSave = { newPattern ->
                if (newPattern.pattern.isNotBlank()) {
                    bottomSheetPattern?.let { oldPattern ->
                        viewModel.updatePattern(oldPattern, newPattern)
                    } ?: run {
                        viewModel.addPattern(newPattern)
                    }
                    bottomSheetPattern = null
                    isBottomSheetOpen = false
                }
            },
            onDelete = { pattern -> viewModel.deletePattern(pattern) }
        )
    }
}