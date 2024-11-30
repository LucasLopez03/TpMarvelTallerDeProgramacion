package com.unlam.tpmarvel.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.unlam.tpmarvel.AppDatabase
import com.unlam.tpmarvel.data.KtorCharactersRepository
import com.unlam.tpmarvel.data.AndroidMarvelApi
import com.unlam.tpmarvel.data.AndroidNetworkConnectivityChecker
import com.unlam.tpmarvel.data.CharactersService
import com.unlam.tpmarvel.data.DatabaseDriverFactory
import com.unlam.tpmarvel.utils.PRIVATE_KEY
import com.unlam.tpmarvel.utils.PUBLIC_KEY

class CharactersViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val marvelApi = AndroidMarvelApi(PUBLIC_KEY, PRIVATE_KEY)
        val appDatabase = AppDatabase(DatabaseDriverFactory(context).createDriver())
        val networkChecker = AndroidNetworkConnectivityChecker(context)  // Crear la instancia
        val charactersRepository = KtorCharactersRepository(marvelApi, appDatabase, networkChecker)  // Pasar networkChecker
        val charactersService = CharactersService(charactersRepository)
        @Suppress("UNCHECKED_CAST")
        return CharactersViewModel(charactersService) as T
    }
}