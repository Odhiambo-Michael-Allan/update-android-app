package com.squad.update.sync.status

interface SyncSubscriber {
    suspend fun subscribe()
}