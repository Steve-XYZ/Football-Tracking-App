package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FavoriteTeamEntity
import com.example.data.model.HistoricalMatchEntity
import com.example.data.model.MatchEntity
import com.example.data.model.NewsHighlightEntity
import com.example.data.model.StandingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FutbolDao {

    // Matches
    @Query("SELECT * FROM matches ORDER BY CASE status WHEN 'LIVE' THEN 1 WHEN 'UPCOMING' THEN 2 ELSE 3 END, id ASC")
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    suspend fun getMatchById(id: String): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    // Standings
    @Query("SELECT * FROM standings WHERE league = :league ORDER BY position ASC")
    fun getStandingsByLeagueFlow(league: String): Flow<List<StandingEntity>>

    @Query("SELECT * FROM standings ORDER BY league, position ASC")
    fun getAllStandingsFlow(): Flow<List<StandingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandings(standings: List<StandingEntity>)

    // News & Highlights
    @Query("SELECT * FROM news_highlights ORDER BY id DESC")
    fun getAllNewsFlow(): Flow<List<NewsHighlightEntity>>

    @Query("SELECT * FROM news_highlights WHERE matchId = :matchId")
    fun getNewsForMatchFlow(matchId: String): Flow<List<NewsHighlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsHighlightEntity>)

    @Update
    suspend fun updateNews(news: NewsHighlightEntity)

    // Historical Database
    @Query("SELECT * FROM historical_matches ORDER BY matchDate DESC")
    fun getAllHistoricalMatchesFlow(): Flow<List<HistoricalMatchEntity>>

    @Query("SELECT * FROM historical_matches WHERE homeTeam LIKE '%' || :query || '%' OR awayTeam LIKE '%' || :query || '%' OR tournament LIKE '%' || :query || '%' ORDER BY matchDate DESC")
    fun searchHistoricalMatchesFlow(query: String): Flow<List<HistoricalMatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalMatches(matches: List<HistoricalMatchEntity>)

    // Favorite Teams
    @Query("SELECT * FROM favorite_teams")
    fun getFavoriteTeamsFlow(): Flow<List<FavoriteTeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTeam(team: FavoriteTeamEntity)

    @Query("DELETE FROM favorite_teams WHERE teamName = :teamName")
    suspend fun deleteFavoriteTeam(teamName: String)

    @Query("UPDATE favorite_teams SET notifyGoals = :notify WHERE teamName = :teamName")
    suspend fun updateFavoriteNotification(teamName: String, notify: Boolean)
}
