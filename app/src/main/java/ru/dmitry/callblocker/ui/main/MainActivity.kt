package ru.dmitry.callblocker.ui.main

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ru.dmitry.callblocker.ui.widget.CallScreenerWidgetProvider
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.domain.model.ScreenedCall
import ru.dmitry.callblocker.ui.theme.CallBlockerTheme
import ru.dmitry.callblocker.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallBlockerTheme() {
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
fun CallScreenerApp(viewModel: MainScreenViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG
        )
    )
    val callScreen =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.hasScreeningRole(true)
            }
        }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager =
                getSystemService(context, RoleManager::class.java) ?: return@LaunchedEffect
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            callScreen.launch(intent)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadCallLog(context)
        // Update widget when app opens
        CallScreenerWidgetProvider.Companion.updateAllWidgets(context)
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        viewModel.updatePermissionStatus(permissionState.allPermissionsGranted)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.call_screener_title)) },
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
            // Service Status Widget
            item {
                ServiceStatusWidget(
                    isActive = uiState.hasScreeningRole,
                    lastCallScreenedTime = uiState.lastCallScreenedTime,
                    lastBlockedCall = uiState.lastBlockedCall
                )
            }

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
fun ServiceStatusWidget(
    isActive: Boolean,
    lastCallScreenedTime: Long,
    lastBlockedCall: ScreenedCall?
) {
    val context = LocalContext.current
    val currentTime = remember { System.currentTimeMillis() }
    val timeSinceLastCall = if (lastCallScreenedTime > 0) {
        getTimeAgoString(currentTime - lastCallScreenedTime, context)
    } else {
        stringResource(R.string.never)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (isActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                    Text(
                        text = if (isActive) stringResource(R.string.service_active) else stringResource(R.string.service_inactive),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.last_call_screened, timeSinceLastCall),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onErrorContainer
                    }
                )

                lastBlockedCall?.let { call ->
                    val contactName = ContactsRepository.getContactName(context, call.phoneNumber)
                    Text(
                        text = stringResource(R.string.last_blocked_call, contactName ?: call.phoneNumber),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        }
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isActive) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.3f)
                }
            )
        }
    }
}

fun getTimeAgoString(millisAgo: Long, context: android.content.Context): String {
    val seconds = millisAgo / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> context.getString(R.string.days_ago, days, if (days > 1) "s" else "")
        hours > 0 -> context.getString(R.string.hours_ago, hours, if (hours > 1) "s" else "")
        minutes > 0 -> context.getString(R.string.minutes_ago, minutes, if (minutes > 1) "s" else "")
        seconds > 0 -> context.getString(R.string.seconds_ago, seconds, if (seconds > 1) "s" else "")
        else -> context.getString(R.string.just_now)
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
                text = stringResource(R.string.setup_status_title),
                style = MaterialTheme.typography.titleLarge
            )

            StatusRow(
                label = stringResource(R.string.permissions_label),
                isGranted = hasPermissions
            )

            StatusRow(
                label = stringResource(R.string.call_screening_label),
                isGranted = hasScreeningRole
            )

            if (!hasPermissions) {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.request_permissions_button))
                }
            }

            if (hasPermissions && !hasScreeningRole) {
                Button(
                    onClick = onSetupScreening,
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
                text = if (isGranted) stringResource(R.string.granted_status) else stringResource(R.string.not_granted_status),
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
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    enabled = canToggle
                )
            }

            Text(
                text = stringResource(R.string.block_unknown_numbers_description),
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
        ContactsRepository.getContactName(context, call.phoneNumber)
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