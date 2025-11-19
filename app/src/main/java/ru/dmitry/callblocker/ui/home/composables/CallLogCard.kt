package ru.dmitry.callblocker.ui.home.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.core.formatters.PhoneNumberFormatter
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.domain.model.ScreenedCall

@Composable
fun CallLogCard(
    calls: List<ScreenedCall>,
    onClearLog: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.screened_calls_title),
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = onClearLog) {
                    Text(stringResource(R.string.clear_button))
                }
            }

            if (calls.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_screened_calls),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                PaginatedCallLog(calls = calls)
            }
        }
    }
}

@Composable
fun PaginatedCallLog(calls: List<ScreenedCall>) {
    var itemsToShow by remember { mutableIntStateOf(10) }
    
    // Display the calls up to the current limit
    val callsToShow = calls.take(itemsToShow)
    
    Column {
        callsToShow.forEachIndexed { index, call ->
            CallLogItem(call)
            if (index < callsToShow.size - 1) {
                HorizontalDivider()
            }
        }
        
        // Show "Show More" button if there are more items to display
        if (calls.size > itemsToShow) {
            HorizontalDivider()
            TextButton(
                onClick = { itemsToShow += 10 },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.show_more_button))
            }
        }
    }
}

@Composable
fun CallLogItem(call: ScreenedCall) {
    val context = LocalContext.current
    val contactName = remember(call.phoneNumber) {
        ContactsRepository(context).getContactName(call.phoneNumber)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contactName ?: PhoneNumberFormatter.format(call.phoneNumber),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = call.formattedDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Surface(
            color = if (call.wasBlocked) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            },
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = if (call.wasBlocked) stringResource(R.string.blocked_status) else stringResource(R.string.screened_status),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                color = if (call.wasBlocked) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                }
            )
        }
    }
}