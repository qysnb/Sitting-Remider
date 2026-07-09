package com.qysnb.sittingreminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.qysnb.sittingreminder.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAbout by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("久坐助手") },
                actions = {
                    IconButton(onClick = { showAbout = true }) {
                        Icon(Icons.Default.Info, contentDescription = "关于")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            MasterToggle(
                enabled = state.masterEnabled,
                onToggle = viewModel::toggleMaster
            )

            NextReminderDisplay(
                isActive = state.masterEnabled,
                nextTriggerTimeMillis = state.nextTriggerTimeMillis
            )

            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "提醒设置",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CompactTimePicker(
                            title = "提醒间隔",
                            totalSeconds = state.reminderIntervalSeconds,
                            onValueChange = viewModel::updateReminderInterval,
                            modifier = Modifier.weight(1f)
                        )
                        CompactTimePicker(
                            title = "入座延迟",
                            totalSeconds = state.sitBackDelaySeconds,
                            onValueChange = viewModel::updateSitBackDelay,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    TimeRangeSelector(
                        startHour = state.startHour,
                        startMinute = state.startMinute,
                        endHour = state.endHour,
                        endMinute = state.endMinute,
                        onStartChange = viewModel::updateStartTime,
                        onEndChange = viewModel::updateEndTime
                    )
                }
            }

            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "铃声设置",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RingtoneSelector(
                        ringtoneUri = state.ringtoneUri,
                        onRingtoneSelected = viewModel::updateRingtoneUri
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
private fun CompactTimePicker(
    title: String,
    totalSeconds: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            TimeSelector(
                value = hours, range = 0..99, label = "时",
                onValueChange = { h ->
                    onValueChange((h * 3600L + minutes * 60L + seconds))
                },
                compact = true
            )
            TimeSelector(
                value = minutes, range = 0..59, label = "分",
                onValueChange = { m ->
                    onValueChange((hours * 3600L + m * 60L + seconds))
                },
                compact = true
            )
            TimeSelector(
                value = seconds, range = 0..59, label = "秒",
                onValueChange = { s ->
                    onValueChange((hours * 3600L + minutes * 60L + s))
                },
                compact = true
            )
        }
    }
}
