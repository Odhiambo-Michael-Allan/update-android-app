package com.squad.update.core.common.network

import javax.inject.Qualifier

@Qualifier
@Retention( AnnotationRetention.RUNTIME )
annotation class Dispatcher( val updateDispatcher: UpdateDispatchers )

enum class UpdateDispatchers {
    Default,
    IO
}