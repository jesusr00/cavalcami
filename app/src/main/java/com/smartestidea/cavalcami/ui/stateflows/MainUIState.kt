package com.smartestidea.cavalcami.ui.stateflows

sealed class MainUIState{
    object Idle : MainUIState()
    object Loading : MainUIState()
    object Success: MainUIState()
    data class Error (val errorMsgRes: Int) : MainUIState()
}
