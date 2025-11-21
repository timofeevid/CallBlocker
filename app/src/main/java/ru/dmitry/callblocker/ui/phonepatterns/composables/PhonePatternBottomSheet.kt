package ru.dmitry.callblocker.ui.phonepatterns.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.core.formatters.mask.TextInputMask
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.data.model.PhonePatternType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePatternBottomSheet(
    pattern: PhonePattern?,
    onDismiss: () -> Unit,
    onSave: (PhonePattern) -> Unit,
    onDelete: (PhonePattern) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var patternText by remember(pattern) { mutableStateOf(pattern?.pattern.orEmpty()) }
    val isBlocking = remember(pattern) { mutableStateOf(pattern?.isNegativePattern ?: true) }
    val selectedPatternType = remember(pattern) {
        mutableStateOf(pattern?.type ?: PhonePatternType.RUSSIAN_MOBILE)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = if (pattern == null) stringResource(id = R.string.add_phone_pattern) else stringResource(
                    id = R.string.phone_patterns_screen_title
                ),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            ChoosePatternField(selectedPatternType)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = patternText,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '*' }) {
                        patternText = it
                    }
                },
                label = { Text(stringResource(id = R.string.phone_pattern_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = TextInputMask(selectedPatternType.value.pattern),
                maxLines = 1,
                singleLine = true,
            )

            ChoosePatternType(isBlocking)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        bottom = 16.dp
                    )
            ) {
                if (pattern != null) {
                    TextButton(
                        onClick = {
                            onDelete(pattern)
                        },
                        modifier = Modifier
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(stringResource(id = R.string.delete))
                        }
                    }
                } else {
                    Spacer(modifier = Modifier)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }

                    TextButton(
                        onClick = {
                            val newPattern = PhonePattern(
                                pattern = patternText,
                                isNegativePattern = isBlocking.value,
                                type = selectedPatternType.value
                            )
                            onSave(newPattern)
                        },
                        enabled = patternText.isNotBlank()
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoosePatternType(isBlocking: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = !isBlocking.value,
                onClick = { isBlocking.value = false }
            )
            Text(stringResource(id = R.string.allow))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isBlocking.value,
                onClick = { isBlocking.value = true }
            )
            Text(stringResource(id = R.string.block))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChoosePatternField(
    selectedPatternType: MutableState<PhonePatternType>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            value = selectedPatternType.value.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(id = R.string.select_pattern)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PhonePatternType.entries.toTypedArray().forEach { patternType ->
                DropdownMenuItem(
                    text = { Text(patternType.displayName) },
                    onClick = {
                        selectedPatternType.value = patternType
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}