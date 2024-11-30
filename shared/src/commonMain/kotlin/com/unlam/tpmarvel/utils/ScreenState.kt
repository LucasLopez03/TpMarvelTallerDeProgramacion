package com.unlam.tpmarvel.utils

import com.unlam.tpmarvel.model.Character

sealed class ScreenState {
    data object Loading : ScreenState()
    data class ShowCharacters(val list: List<Character>) : ScreenState()
    data class Error(val message: String) : ScreenState()
}