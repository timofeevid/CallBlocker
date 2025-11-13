package ru.dmitry.callblocker.ui.phonepatterns

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePatternsScreen(
    viewModel: PhonePatternsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var patternText by remember { mutableStateOf("") }
    var isBlocking by remember { mutableStateOf(true) }
    var editingPattern by remember { mutableStateOf<PhonePattern?>(null) }
    
    val sheetState = rememberModalBottomSheetState()
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with back button
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
                    style = MaterialTheme.typography.headlineSmall, // Reduced size
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.patterns) { pattern ->
                    PhonePatternItem(
                        pattern = pattern,
                        onClick = {
                            // Set up the bottom sheet for editing
                            editingPattern = pattern
                            patternText = pattern.pattern
                            isBlocking = !pattern.isNegativePattern // Inverted because we store negative patterns
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
        
        FloatingActionButton(
            onClick = { 
                // Reset for adding new pattern
                editingPattern = null
                patternText = ""
                isBlocking = true
                showBottomSheet = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_phone_pattern))
        }
    }
    
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = patternText,
                    onValueChange = { 
                        // Allow only digits, spaces, and asterisks
                        if (it.all { char -> char.isDigit() || char == ' ' || char == '*' }) {
                            patternText = it
                        }
                    },
                    label = { Text(stringResource(id = R.string.phone_pattern_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Checkbox(
                        checked = isBlocking,
                        onCheckedChange = { isBlocking = it }
                    )
                    Text(stringResource(id = R.string.block_this_pattern))
                }
                
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    // Show delete button only when editing
                    if (editingPattern != null) {
                        TextButton(
                            onClick = {
                                editingPattern?.let { pattern ->
                                    viewModel.deletePattern(pattern)
                                }
                                patternText = ""
                                isBlocking = true
                                editingPattern = null
                                showBottomSheet = false
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(stringResource(id = R.string.delete))
                            }
                        }
                    }
                    
                    TextButton(
                        onClick = { showBottomSheet = false }
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    
                    TextButton(
                        onClick = {
                            if (patternText.isNotBlank()) {
                                editingPattern?.let { pattern ->
                                    // Update existing pattern
                                    viewModel.updatePattern(pattern, patternText, !isBlocking) // Inverted because we store negative patterns
                                } ?: run {
                                    // Add new pattern
                                    viewModel.addPattern(patternText, !isBlocking) // Inverted because we store negative patterns
                                }
                                patternText = ""
                                isBlocking = true
                                editingPattern = null
                                showBottomSheet = false
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun PhonePatternItem(
    pattern: PhonePattern,
    onClick: () -> Unit = {}
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pattern.pattern,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (pattern.isNegativePattern) stringResource(id = R.string.allow) else stringResource(id = R.string.block),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (pattern.isNegativePattern) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            
            // Add a visual indicator for blocking status
            Icon(
                imageVector = if (pattern.isNegativePattern) Icons.Default.ArrowDownward else Icons.Default.Block,
                contentDescription = if (pattern.isNegativePattern) stringResource(id = R.string.allow) else stringResource(id = R.string.block),
                tint = if (pattern.isNegativePattern) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}