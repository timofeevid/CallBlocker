package ru.dmitry.callblocker.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.ThemeColor

@Composable
fun SettingsScreen(viewModel: SettingsScreenViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_screen_title),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Push Notifications Setting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.push_notifications_setting),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = uiState.isPushEnable,
                onCheckedChange = { viewModel.updatePushEnabled(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Language Setting
        ExpandableSettingItem(
            title = R.string.language_setting,
            currentValue = uiState.appLanguage,
            values = AppLanguage.entries.toTypedArray(),
            getValueLabel = { language ->
                when (language) {
                    AppLanguage.ENG -> R.string.english_language_option
                    AppLanguage.RU -> R.string.russian_language_option
                }
            },
            onValueChanged = { viewModel.updateLanguage(it) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Theme Setting
        ExpandableSettingItem(
            title = R.string.theme_setting,
            currentValue = uiState.theme,
            values = ThemeColor.entries.toTypedArray(),
            getValueLabel = { theme ->
                when (theme) {
                    ThemeColor.LIGHT -> R.string.light_theme_option
                    ThemeColor.DARK -> R.string.dark_theme_option
                }
            },
            onValueChanged = { viewModel.updateTheme(it) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}