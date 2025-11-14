package ru.dmitry.callblocker.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.navigation.Screen

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var numberOfCallsText by remember(uiState.numberOfBlockCallToStore) {
        mutableStateOf(uiState.numberOfBlockCallToStore.toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings_screen_title),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.push_notifications_setting),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = uiState.isPushEnable,
                onCheckedChange = { viewModel.updatePushEnabled(it) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = numberOfCallsText,
            onValueChange = { newText ->
                numberOfCallsText = newText
                newText.toIntOrNull()?.let { number ->
                    viewModel.updateNumberOfCallsToStore(number)
                }
            },
            label = { Text(stringResource(id = R.string.number_of_calls_to_store_setting)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
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
        
        ExpandableSettingItem(
            title = R.string.theme_setting,
            currentValue = uiState.theme,
            values = AppThemeColor.entries.toTypedArray(),
            getValueLabel = { theme ->
                when (theme) {
                    AppThemeColor.LIGHT -> R.string.light_theme_option
                    AppThemeColor.DARK -> R.string.dark_theme_option
                }
            },
            onValueChanged = { viewModel.updateTheme(it) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.PhonePatterns.route) }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.phone_patterns_setting),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}