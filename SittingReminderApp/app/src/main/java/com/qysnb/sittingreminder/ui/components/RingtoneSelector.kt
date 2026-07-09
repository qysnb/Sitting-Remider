package com.qysnb.sittingreminder.ui.components

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.qysnb.sittingreminder.audio.AudioPlayer
import kotlinx.coroutines.launch

@Composable
fun RingtoneSelector(
    ringtoneUri: String?,
    onRingtoneSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val audioPlayer = remember { AudioPlayer() }

    val ringtonePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java
                )
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            onRingtoneSelected(uri?.toString())
        }
    }

    val displayName = remember(ringtoneUri) {
        if (ringtoneUri == null) {
            "默认闹钟铃声"
        } else {
            try {
                val uri = Uri.parse(ringtoneUri)
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex >= 0) cursor.getString(nameIndex) else "自定义铃声"
                    } else "自定义铃声"
                } ?: "默认闹钟铃声"
            } catch (e: Exception) {
                "默认闹钟铃声"
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { audioPlayer.release() }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(displayName, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = {
                val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                    putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "选择闹钟铃声")
                    ringtoneUri?.let {
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(it))
                    }
                }
                ringtonePickerLauncher.launch(intent)
            }) {
                Text("选择铃声")
            }
            OutlinedButton(onClick = {
                scope.launch {
                    val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    val uri = ringtoneUri?.let { Uri.parse(it) } ?: defaultUri
                    audioPlayer.play(context, uri, durationMs = 2000L)
                }
            }) {
                Text("测试")
            }
        }
    }
}
