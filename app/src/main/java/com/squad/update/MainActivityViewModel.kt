package com.squad.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        MainActivityUiState.Success( it )
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed( 5_000 )
    )

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success( val userData: UserData ) : MainActivityUiState
}