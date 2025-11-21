package ru.dmitry.callblocker.ui.phonepatterns.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.core.formatters.PhoneNumberFormatter
import ru.dmitry.callblocker.data.model.PhonePattern

@Composable
fun PhonePatternItem(
    modifier: Modifier = Modifier,
    pattern: PhonePattern,
    onClick: () -> Unit = {}
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = PhoneNumberFormatter.format(pattern.pattern, pattern.type),
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (pattern.isNegativePattern.not()) stringResource(id = R.string.allow) else stringResource(id = R.string.block),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (pattern.isNegativePattern.not()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            
            Icon(
                imageVector = if (pattern.isNegativePattern.not()) Icons.Default.CheckCircleOutline else Icons.Default.Block,
                contentDescription = if (pattern.isNegativePattern.not()) stringResource(id = R.string.allow) else stringResource(id = R.string.block),
                tint = if (pattern.isNegativePattern.not()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}