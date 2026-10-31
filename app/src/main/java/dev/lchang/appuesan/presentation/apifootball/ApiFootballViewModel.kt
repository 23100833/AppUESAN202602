
package dev.lchang.appuesan.presentation.apifootball

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.lchang.appuesan.data.local.AppDatabase
import dev.lchang.appuesan.data.local.FavoriteTeamEntity
import dev.lchang.appuesan.data.remote.apifootball.Country
import dev.lchang.appuesan.data.remote.apifootball.RetrofitInstance
import dev.lchang.appuesan.data.remote.apifootball.TeamWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ApiFootballViewModel(application: Application) :
    AndroidViewModel(application) {

    private val favoriteDao =
        AppDatabase.getDatabase(application).favoriteTeamDao()

    private val _countries =
        MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries

    private val _selectedCountry =
        MutableStateFlow<Country?>(null)
    val selectedCountry: StateFlow<Country?> = _selectedCountry

    private val _teams =
        MutableStateFlow<List<TeamWrapper>>(emptyList())
    val teams: StateFlow<List<TeamWrapper>> = _teams

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessages =
        MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _errorMessages

    val favorites = favoriteDao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    init {
        loadCountries()
    }

    fun loadCountries() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessages.value = null

            try {
                val response = RetrofitInstance.api.getCountries()
                _countries.value = response.response
            } catch (e: Exception) {
                _errorMessages.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTeamsByCountry(country: Country) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessages.value = null

            try {
                val response =
                    RetrofitInstance.api.getTeamsByCountry(country.name)

                _teams.value = response.response
                _selectedCountry.value = country

            } catch (e: Exception) {
                _errorMessages.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCountrySelected(country: Country) {
        _selectedCountry.value = country
        loadTeamsByCountry(country)
    }

    fun toggleFavorite(team: TeamWrapper) {
        viewModelScope.launch {
            val apiTeam = team.team

            val favorite = FavoriteTeamEntity(
                id = apiTeam.id,
                name = apiTeam.name,
                country = apiTeam.country,
                logo = apiTeam.logo,
                founded = apiTeam.founded
            )

            if (favoriteDao.isFavorite(apiTeam.id)) {
                favoriteDao.deleteFavorite(favorite)
            } else {
                favoriteDao.insertFavorite(favorite)
            }
        }
    }
}
