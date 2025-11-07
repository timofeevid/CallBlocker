package ru.dmitry.callblocker.ui.home

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.dmitry.callblocker.ui.widget.WidgetUpdate

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            viewModel.updatePermissionStatus(allGranted)
        }
    )
    val permissionRequest: () -> Unit = {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG
            )
        )
    }
    val callScreen =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.hasScreeningRole(true)
            }
        }

    LaunchedEffect(Unit) {
        val roleManager =
            getSystemService(context, RoleManager::class.java) ?: return@LaunchedEffect
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        callScreen.launch(intent)
        permissionRequest.invoke()
    }

    LaunchedEffect(uiState.blockUnknownCalls, uiState.lastBlockedCall) {
        WidgetUpdate.updateWidgets(context)
    }

    HomeScreenData(
        uiState = uiState,
        permissionLauncher = permissionRequest,
        viewModel = viewModel
    )
}

@Composable
private fun HomeScreenData(
    uiState: HomeScreenUiState,
    permissionLauncher: () -> Unit,
    viewModel: HomeScreenViewModel
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SetupStatusCard(
                hasPermissions = uiState.hasPermissions,
                hasScreeningRole = uiState.hasScreeningRole,
                onRequestPermissions = permissionLauncher,
                onSetupScreening = {
                    val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }

        item {
            CallBlockingCard(
                isEnabled = uiState.blockUnknownCalls,
                canToggle = uiState.hasPermissions && uiState.hasScreeningRole,
                onToggle = { viewModel.toggleBlockUnknownCalls(it) }
            )
        }

        item {
            CallLogCard(
                calls = uiState.screenedCalls,
                onClearLog = { viewModel.clearCallLog() }
            )
        }
    }
}