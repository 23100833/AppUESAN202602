
package dev.lchang.appuesan.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTeamDao {

    @Query("SELECT * FROM favorite_teams ORDER BY name ASC")
    fun getAllFavorites(): Flow<List<FavoriteTeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(team: FavoriteTeamEntity)

    @Delete
    suspend fun deleteFavorite(team: FavoriteTeamEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_teams WHERE id = :teamId)")
    suspend fun isFavorite(teamId: Int): Boolean
}
