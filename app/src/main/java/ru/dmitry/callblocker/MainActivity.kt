package ru.dmitry.callblocker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ru.dmitry.callblocker.ui.theme.CallBlockerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallBlockerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CallScreenerApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CallScreenerApp(viewModel: CallScreenerViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_CALL_LOG
        )
    )

    LaunchedEffect(Unit) {
        viewModel.loadCallLog(context)
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        viewModel.updatePermissionStatus(permissionState.allPermissionsGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Call Screener") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Setup Status Card
            item {
                SetupStatusCard(
                    hasPermissions = uiState.hasPermissions,
                    hasScreeningRole = uiState.hasScreeningRole,
                    onRequestPermissions = { permissionState.launchMultiplePermissionRequest() },
                    onSetupScreening = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                )
            }

            // Call Blocking Card
            item {
                CallBlockingCard(
                    isEnabled = uiState.blockUnknownCalls,
                    canToggle = uiState.hasPermissions && uiState.hasScreeningRole,
                    onToggle = { viewModel.toggleBlockUnknownCalls(context, it) }
                )
            }

            // Call Log Card
            item {
                CallLogCard(
                    calls = uiState.screenedCalls,
                    onClearLog = { viewModel.clearCallLog(context) }
                )
            }
        }
    }
}

@Composable
fun SetupStatusCard(
    hasPermissions: Boolean,
    hasScreeningRole: Boolean,
    onRequestPermissions: () -> Unit,
    onSetupScreening: () -> Unit
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
                text = "Setup Status",
                style = MaterialTheme.typography.titleLarge
            )

            StatusRow(
                label = "Permissions",
                isGranted = hasPermissions
            )

            StatusRow(
                label = "Call Screening",
                isGranted = hasScreeningRole
            )

            if (!hasPermissions) {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Request Permissions")
                }
            }

            if (hasPermissions && !hasScreeningRole) {
                Button(
                    onClick = onSetupScreening,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Enable Call Screening")
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Text(
                text = if (isGranted) "Granted" else "Not Granted",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun CallBlockingCard(
    isEnabled: Boolean,
    canToggle: Boolean,
    onToggle: (Boolean) -> Unit
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
                text = "Call Blocking",
                style = MaterialTheme.typography.titleLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Block Unknown Numbers",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    enabled = canToggle
                )
            }

            Text(
                text = "When enabled, calls from numbers not in your contacts will be automatically rejected.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

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
                    text = "Screened Calls",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = onClearLog) {
                    Text("Clear")
                }
            }

            if (calls.isEmpty()) {
                Text(
                    text = "No screened calls yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                calls.forEach { call ->
                    CallLogItem(call)
                    if (call != calls.last()) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun CallLogItem(call: ScreenedCall) {
    val context = LocalContext.current
    val contactName = remember(call.phoneNumber) {
        ContactsHelper.getContactName(context, call.phoneNumber)
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
                text = contactName ?: call.phoneNumber,
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
                text = if (call.wasBlocked) "BLOCKED" else "SCREENED",
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