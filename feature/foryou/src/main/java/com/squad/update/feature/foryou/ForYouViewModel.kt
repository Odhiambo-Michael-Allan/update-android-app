package com.squad.update.feature.foryou

import androidx.lifecycle.ViewModel
import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.data.repository.UserNewsResourceRepository
import com.squad.update.core.data.util.SyncManager
import com.squad.update.core.domain.GetFollowableTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForYouViewModel @Inject constructor(
//    syncManager: SyncManager,
    userNewsResourceRepository: UserNewsResourceRepository,
    getFollowableTopics: GetFollowableTopicsUseCase,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {
}