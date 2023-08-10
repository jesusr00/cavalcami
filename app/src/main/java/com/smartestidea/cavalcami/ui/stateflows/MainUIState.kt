package com.smartestidea.cavalcami.ui.stateflows

sealed class MainUIState{
    object Idle : MainUIState()
    object Loading : MainUIState()
    data class Success (val successMsgRes: Int?) : MainUIState()
    data class Error (val errorMsgRes: Int) : MainUIState()
}
