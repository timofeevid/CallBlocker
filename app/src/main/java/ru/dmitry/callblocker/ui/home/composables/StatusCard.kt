package ru.dmitry.callblocker.ui.home.composables

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.ui.home.HomeScreenUiState
import ru.dmitry.callblocker.ui.home.HomeScreenViewModel

@Composable
fun StatusCard(
    uiState: HomeScreenUiState,
    viewModel: HomeScreenViewModel,
) {
    val context = LocalContext.current
    val onRequestPermissions = PermissionsCall(
        lastBlockedCallStatus = uiState.lastBlockedCall,
        onScreenRoleChanged = viewModel::hasScreeningRole,
        permissionResult = viewModel::updatePermissionStatus
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.setup_status_title),
                style = MaterialTheme.typography.titleLarge
            )

            StatusRow(
                label = stringResource(R.string.permissions_label),
                isGranted = uiState.hasPermissions
            )

            StatusRow(
                label = stringResource(R.string.call_screening_label),
                isGranted = uiState.hasScreeningRole
            )

            if (!uiState.hasPermissions) {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.request_permissions_button))
                }
            }

            if (uiState.hasPermissions && !uiState.hasScreeningRole) {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.enable_call_screening_button))
                }
            }
        }
    }
}

@Composable
fun StatusRow(label: String, isGranted: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}