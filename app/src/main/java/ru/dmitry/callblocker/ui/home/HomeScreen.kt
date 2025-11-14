package ru.dmitry.callblocker.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.dmitry.callblocker.ui.home.composables.CallBlockingCard
import ru.dmitry.callblocker.ui.home.composables.CallLogCard
import ru.dmitry.callblocker.ui.home.composables.StatusCard

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenData(
        uiState = uiState,
        viewModel = viewModel
    )
}

@Composable
private fun HomeScreenData(
    uiState: HomeScreenUiState,
    viewModel: HomeScreenViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            StatusCard(
                uiState = uiState,
                viewModel = viewModel,
            )
        }

        item {
            CallBlockingCard(
                isBlockUnknownCalls = uiState.blockUnknownCalls,
                isBlockByPattern = uiState.blockByPattern,
                canToggle = uiState.hasPermissions && uiState.hasScreeningRole,
                onToggleBlockUnknowCalls = { viewModel.toggleBlockUnknownCalls(it) },
                onToggleBlockByPattern = { viewModel.toggleBlockByPattern(it) }
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