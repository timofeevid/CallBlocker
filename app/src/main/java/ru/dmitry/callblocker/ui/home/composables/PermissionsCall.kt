package ru.dmitry.callblocker.ui.home.composables

import android.Manifest
import android.app.Activity
import android.app.role.RoleManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import ru.dmitry.callblocker.domain.model.ScreenedCall
import ru.dmitry.callblocker.ui.widget.WidgetUpdate

@Composable
fun PermissionsCall(
    lastBlockedCallStatus: ScreenedCall?,
    onScreenRoleChanged: (Boolean) -> Unit,
    permissionResult: (Boolean) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val allGranted = permissions.values.all { it }
            permissionResult.invoke(allGranted)
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
                onScreenRoleChanged.invoke(true)
            }
        }

    LaunchedEffect(Unit) {
        val roleManager =
            getSystemService(context, RoleManager::class.java) ?: return@LaunchedEffect
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        callScreen.launch(intent)
        permissionRequest.invoke()
    }

    LaunchedEffect(lastBlockedCallStatus) {
        WidgetUpdate.updateWidgets(context)
    }

    return permissionRequest
}