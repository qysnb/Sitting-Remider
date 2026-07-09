package com.qysnb.sittingreminder

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.qysnb.sittingreminder.ui.MainScreen
import com.qysnb.sittingreminder.ui.theme.SittingReminderTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val permissionsDismissed = prefs.getBoolean(PREF_PERMISSIONS_DISMISSED, false)

        setContent {
            SittingReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                    if (!permissionsDismissed) {
                        PermissionDialogs(
                            onDismiss = {
                                prefs.edit().putBoolean(PREF_PERMISSIONS_DISMISSED, true).apply()
                            }
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "sitting_reminder_prefs"
        private const val PREF_PERMISSIONS_DISMISSED = "permissions_dismissed"
    }
}

@Composable
fun PermissionDialogs(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var showBattery by remember { mutableStateOf(true) }
    var showExactAlarm by remember { mutableStateOf(true) }
    var dismissed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (context as? android.app.Activity)?.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    if (dismissed) return

    if (showBattery) {
        AlertDialog(
            onDismissRequest = {
                showBattery = false
                dismissed = true
                onDismiss()
            },
            title = { Text("请关闭电池优化") },
            text = { Text("为了确保提醒准时送达，请允许久坐助手在后台运行。") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                    showBattery = false
                    dismissed = true
                    onDismiss()
                }) { Text("打开设置") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBattery = false
                    dismissed = true
                    onDismiss()
                }) { Text("稍后") }
            }
        )
    }

    if (showExactAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !dismissed) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog(
                onDismissRequest = {
                    showExactAlarm = false
                    dismissed = true
                    onDismiss()
                },
                title = { Text("请授予精确闹钟权限") },
                text = { Text("久坐助手需要精确闹钟权限才能准时提醒您。") },
                confirmButton = {
                    TextButton(onClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        context.startActivity(intent)
                        showExactAlarm = false
                        dismissed = true
                        onDismiss()
                    }) { Text("打开设置") }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showExactAlarm = false
                        dismissed = true
                        onDismiss()
                    }) { Text("取消") }
                }
            )
        }
    }
}
