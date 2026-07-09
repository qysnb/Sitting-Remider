package com.qysnb.sittingreminder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TimeSelector(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember(value) { mutableStateOf(value.toString()) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }

    var hasGainedFocus by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (!compact) {
            TextButton(onClick = {
                onValueChange(if (value + 1 > range.last) range.first else value + 1)
            }) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
        }
        if (isEditing) {
            OutlinedTextField(
                value = textValue,
                onValueChange = { textValue = it.filter { c -> c.isDigit() } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val parsed = textValue.toIntOrNull()
                        val clamped = when {
                            parsed == null -> value
                            parsed < range.first -> range.first
                            parsed > range.last -> range.last
                            else -> parsed
                        }
                        onValueChange(clamped)
                        isEditing = false
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) hasGainedFocus = true
                        if (!it.isFocused && isEditing && hasGainedFocus) {
                            val parsed = textValue.toIntOrNull()
                            val clamped = when {
                                parsed == null -> value
                                parsed < range.first -> range.first
                                parsed > range.last -> range.last
                                else -> parsed
                            }
                            onValueChange(clamped)
                            isEditing = false
                        }
                    }
                    .widthIn(min = if (compact) 40.dp else 56.dp),
                textStyle = if (compact)
                    MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                else
                    MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
            )
        } else {
            Box(
                modifier = if (compact)
                    Modifier.widthIn(min = 36.dp).heightIn(min = 28.dp)
                        .clickable { isEditing = true; hasGainedFocus = false }
                else
                    Modifier.clickable { isEditing = true; hasGainedFocus = false }
            ) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = if (compact)
                        Modifier.widthIn(min = 36.dp).heightIn(min = 28.dp)
                    else
                        Modifier.widthIn(min = 56.dp)
                ) {
                    Text(
                        text = String.format("%02d", value),
                        style = if (compact)
                            MaterialTheme.typography.bodyLarge
                        else
                            MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(
                            horizontal = if (compact) 6.dp else 12.dp,
                            vertical = if (compact) 2.dp else 4.dp
                        )
                    )
                }
            }
        }
        if (!compact) {
            TextButton(onClick = {
                onValueChange(if (value - 1 < range.first) range.last else value - 1)
            }) {
                Text("-", style = MaterialTheme.typography.titleMedium)
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TimeRangeSelector(
    startHour: Int,
    startMinute: Int,
    endHour: Int,
    endMinute: Int,
    onStartChange: (Int, Int) -> Unit,
    onEndChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("时间范围", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("开始", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimeSelector(
                        value = startHour, range = 0..23, label = "",
                        onValueChange = { onStartChange(it, startMinute) }
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    TimeSelector(
                        value = startMinute, range = 0..59, label = "",
                        onValueChange = { onStartChange(startHour, it) }
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("结束", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TimeSelector(
                        value = endHour, range = 0..23, label = "",
                        onValueChange = { onEndChange(it, endMinute) }
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    TimeSelector(
                        value = endMinute, range = 0..59, label = "",
                        onValueChange = { onEndChange(endHour, it) }
                    )
                }
            }
        }
    }
}
