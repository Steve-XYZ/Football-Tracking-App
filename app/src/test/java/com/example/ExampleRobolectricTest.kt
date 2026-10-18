package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.FutbolDatabase
import com.example.data.model.FavoriteTeamEntity
import com.example.data.sample.InitialData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: FutbolDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, FutbolDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Futbol Pulse", appName)
    }

    @Test
    fun `room database inserts and retrieves matches and favorites offline`() = runBlocking {
        val dao = database.futbolDao()

        // Seed matches and standings
        dao.insertMatches(InitialData.sampleMatches)
        dao.insertStandings(InitialData.sampleStandings)

        val retrievedMatches = dao.getAllMatchesFlow().first()
        assertTrue(retrievedMatches.isNotEmpty())

        val elClasico = retrievedMatches.find { it.homeTeam == "Real Madrid" && it.awayTeam == "Barcelona" }
        assertNotNull(elClasico)
        assertEquals(2, elClasico?.homeScore)
        assertEquals(1, elClasico?.awayScore)

        // Test favorites
        dao.insertFavoriteTeam(FavoriteTeamEntity("Arsenal", "Premier League", notifyGoals = true))
        val favorites = dao.getFavoriteTeamsFlow().first()
        assertEquals(1, favorites.size)
        assertEquals("Arsenal", favorites[0].teamName)
        assertTrue(favorites[0].notifyGoals)
    }
}
