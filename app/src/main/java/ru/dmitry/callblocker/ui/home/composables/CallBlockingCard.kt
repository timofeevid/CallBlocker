package ru.dmitry.callblocker.ui.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.dmitry.callblocker.R

@Composable
fun CallBlockingCard(
    isBlockUnknownCalls: Boolean,
    isBlockByPattern: Boolean,
    canToggle: Boolean,
    onToggleBlockUnknowCalls: (Boolean) -> Unit,
    onToggleBlockByPattern: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.call_blocking_title),
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.block_unknown_numbers_label),
                    style = MaterialTheme.typography.bodySmall
                )
                Switch(
                    checked = isBlockUnknownCalls,
                    onCheckedChange = onToggleBlockUnknowCalls,
                    enabled = canToggle
                )
            }

            Text(
                text = stringResource(R.string.block_unknown_numbers_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.pattern_blocking_title),
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.block_by_pattern_label),
                    style = MaterialTheme.typography.bodySmall
                )
                Switch(
                    checked = isBlockByPattern,
                    onCheckedChange = onToggleBlockByPattern,
                    enabled = canToggle
                )
            }

            Text(
                text = stringResource(R.string.block_by_pattern_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}