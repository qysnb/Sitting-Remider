package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun NextReminderDisplay(
    isActive: Boolean,
    nextTriggerTimeMillis: Long?,
    modifier: Modifier = Modifier
) {
    var countdownText by remember { mutableStateOf("--") }

    LaunchedEffect(isActive, nextTriggerTimeMillis) {
        if (!isActive || nextTriggerTimeMillis == null) {
            countdownText = "--"
            return@LaunchedEffect
        }
        while (true) {
            val diff = nextTriggerTimeMillis - System.currentTimeMillis()
            if (diff <= 0) {
                countdownText = "--"
                delay(1000)
                continue
            }
            val totalSec = diff / 1000
            val h = (totalSec / 3600).toInt()
            val m = ((totalSec % 3600) / 60).toInt()
            val s = (totalSec % 60).toInt()
            countdownText = if (h > 0) {
                "${h}时${m}分${s}秒"
            } else {
                "${m}分${s}秒"
            }
            val now = System.currentTimeMillis()
            delay((now / 1000 + 1) * 1000 - now)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "下次提醒",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                if (isActive) countdownText else "提醒已关闭",
                style = MaterialTheme.typography.headlineLarge,
                color = if (isActive)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
