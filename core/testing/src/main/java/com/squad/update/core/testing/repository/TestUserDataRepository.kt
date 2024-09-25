package com.squad.update.core.testing.repository

import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.model.data.UserData

val emptyUserData = UserData(
    bookmarkedNewsResources = emptySet(),
    viewedNewsResources = emptySet(),
    followedTopics = emptySet(),
    themeBrand = ThemeBrand.DEFAULT,
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useDynamicColor = false,
    shouldHideOnboarding = false
)
