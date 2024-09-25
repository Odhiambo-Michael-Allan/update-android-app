package com.squad.update.core.data.util.impl

import com.squad.update.core.data.util.TimeZoneMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TimeZoneMonitorImpl @Inject constructor() : TimeZoneMonitor {

    private val timeZoneFlow = MutableStateFlow( defaultTimeZone )

    override val currentTimeZone: Flow<TimeZone> = timeZoneFlow

    companion object {
        val defaultTimeZone: TimeZone = TimeZone.of( "Europe/Warsaw" )
    }
}