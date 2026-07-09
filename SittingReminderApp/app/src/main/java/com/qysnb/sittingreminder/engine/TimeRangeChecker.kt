package com.qysnb.sittingreminder.engine

import java.util.Calendar

class TimeRangeChecker {

    fun isInActiveWindow(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        now: Calendar = Calendar.getInstance()
    ): Boolean {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        return if (startMinutes <= endMinutes) {
            currentMinutes in startMinutes..endMinutes
        } else {
            currentMinutes >= startMinutes || currentMinutes <= endMinutes
        }
    }

    fun nextActiveStart(
        startHour: Int, startMinute: Int,
        endHour: Int, endMinute: Int,
        now: Calendar = Calendar.getInstance()
    ): Calendar {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        val result = now.clone() as Calendar

        when {
            startMinutes <= endMinutes -> {
                if (currentMinutes < startMinutes) {
                    result.set(Calendar.HOUR_OF_DAY, startHour)
                    result.set(Calendar.MINUTE, startMinute)
                    result.set(Calendar.SECOND, 0)
                } else if (currentMinutes > endMinutes) {
                    result.add(Calendar.DAY_OF_YEAR, 1)
                    result.set(Calendar.HOUR_OF_DAY, startHour)
                    result.set(Calendar.MINUTE, startMinute)
                    result.set(Calendar.SECOND, 0)
                }
            }
            else -> {
                if (currentMinutes in endMinutes..startMinutes) {
                    result.set(Calendar.HOUR_OF_DAY, startHour)
                    result.set(Calendar.MINUTE, startMinute)
                    result.set(Calendar.SECOND, 0)
                }
            }
        }
        return result
    }
}
