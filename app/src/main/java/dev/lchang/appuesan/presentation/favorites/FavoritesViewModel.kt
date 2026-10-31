
package dev.lchang.appuesan.presentation.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.lchang.appuesan.data.local.AppDatabase
import dev.lchang.appuesan.data.local.FavoriteTeamEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) :
    AndroidViewModel(application) {

    private val favoriteDao =
        AppDatabase.getDatabase(application).favoriteTeamDao()

    val favorites = favoriteDao.getAllFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    fun removeFavorite(team: FavoriteTeamEntity) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(team)
        }
    }
}
