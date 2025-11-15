package ru.dmitry.callblocker.ui.phonepatterns.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.ui.utils.PhoneNumberVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhonePatternBottomSheet(
    pattern: PhonePattern?,
    onDismiss: () -> Unit,
    onSave: (String, Boolean) -> Unit,
    onDelete: (PhonePattern) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    var patternText by remember(pattern) { mutableStateOf(pattern?.pattern ?: "") }
    var isBlocking by remember(pattern) { mutableStateOf(!(pattern?.isNegativePattern ?: false)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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

            OutlinedTextField(
                value = patternText,
                onValueChange = {
                    if (
                        it.length <= PhoneNumberVisualTransformation.MAX_PHONE_DIGITS &&
                        it.all { char -> char.isDigit() || char == '*' || char == '+' }
                    ) {
                        patternText = it
                    }
                },
                label = { Text(stringResource(id = R.string.phone_pattern_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PhoneNumberVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                maxLines = 1,
                singleLine = true,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Checkbox(
                    checked = isBlocking,
                    onCheckedChange = { isBlocking = it }
                )
                Text(stringResource(id = R.string.block_this_pattern))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
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
                            onSave(patternText, !isBlocking)
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