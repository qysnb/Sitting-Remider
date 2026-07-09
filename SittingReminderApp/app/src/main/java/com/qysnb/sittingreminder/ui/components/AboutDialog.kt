package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "关于",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AboutItem("软件名", "久坐助手")
                AboutItem("版本号", "v 0.2.0")
                AboutItem("GitHub", "待定")
                AboutItem(
                    "简介",
                    "久坐助手是一款轻量级提醒应用，定时提醒您起身活动，预防久坐带来的健康问题。"
                )
                AboutItem("开发者", "Qysnb with DeepSeek V4 Flash")
                AboutItem("开源许可", "Apache Licence 2.0")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
private fun AboutItem(label: String, value: String) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
