package com.unlam.tpmarvel.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unlam.tpmarvel.data.CharactersService
import com.unlam.tpmarvel.utils.ScreenState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class CharactersViewModel(
    private val charactersService: CharactersService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _screenState: MutableStateFlow<ScreenState> = MutableStateFlow(ScreenState.Loading)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    private var searchJob: Job? = null
    private val searchDebounceTime = 300L

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch(ioDispatcher) {
            try {
                _screenState.value = ScreenState.Loading
                val characters = charactersService.getCharacters()
                withContext(Dispatchers.Main) {
                    _screenState.value = ScreenState.ShowCharacters(characters)
                }
            } catch (e: Exception) {
                Timber.tag("CharactersViewModel").e(e, "Error loading characters")
                handleError(e)
            }
        }
    }

    fun searchCharacter(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            loadCharacters()
            return
        }

        searchJob = viewModelScope.launch {
            try {
                delay(searchDebounceTime)
                _screenState.value = ScreenState.Loading

                withContext(ioDispatcher) {
                    val results = try {
                        charactersService.searchCharacter(query)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Timber.tag("CharactersViewModel").e(e, "Error en búsqueda")
                        emptyList()
                    }

                    withContext(Dispatchers.Main) {
                        if (results.isEmpty()) {
                            _screenState.value = ScreenState.Error("No se encontraron resultados para '$query'")
                        } else {
                            _screenState.value = ScreenState.ShowCharacters(results)
                        }
                    }
                }
            } catch (e: CancellationException) {
                Timber.tag("CharactersViewModel").d("Búsqueda cancelada")
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun handleError(e: Exception) {
        withContext(Dispatchers.Main) {
            _screenState.value = ScreenState.Error(
                when (e) {
                    is java.net.UnknownHostException -> "No hay conexión a Internet"
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                    else -> "Error: ${e.message ?: "desconocido"}"
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }
}