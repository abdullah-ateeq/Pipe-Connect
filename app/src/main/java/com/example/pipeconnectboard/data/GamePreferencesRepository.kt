package com.example.pipeconnectboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pipe_connect_prefs")

data class UserSettings(
    val themeMode: String = "System", // "System", "Dark", "Light"
    val boardTheme: String = "Classic", // "Classic", "Midnight"
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val highlightConnected: Boolean = true,
    val showTimer: Boolean = true
)

data class GameStatistics(
    val gamesPlayed: Int = 0,
    val pipelinesCompleted: Int = 0,
    val bestMoves: Int = 0,
    val bestTimeMillis: Long = 0L
)

class GamePreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOARD_THEME = stringPreferencesKey("board_theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val HIGHLIGHT_CONNECTED = booleanPreferencesKey("highlight_connected")
        val SHOW_TIMER = booleanPreferencesKey("show_timer")

        val GAMES_PLAYED = intPreferencesKey("games_played")
        val PIPELINES_COMPLETED = intPreferencesKey("pipelines_completed")
        val BEST_MOVES = intPreferencesKey("best_moves")
        val BEST_TIME_MILLIS = longPreferencesKey("best_time_millis")
    }

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        UserSettings(
            themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "System",
            boardTheme = preferences[PreferencesKeys.BOARD_THEME] ?: "Classic",
            soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
            hapticEnabled = preferences[PreferencesKeys.HAPTIC_ENABLED] ?: true,
            highlightConnected = preferences[PreferencesKeys.HIGHLIGHT_CONNECTED] ?: true,
            showTimer = preferences[PreferencesKeys.SHOW_TIMER] ?: true
        )
    }

    val statisticsFlow: Flow<GameStatistics> = context.dataStore.data.map { preferences ->
        GameStatistics(
            gamesPlayed = preferences[PreferencesKeys.GAMES_PLAYED] ?: 0,
            pipelinesCompleted = preferences[PreferencesKeys.PIPELINES_COMPLETED] ?: 0,
            bestMoves = preferences[PreferencesKeys.BEST_MOVES] ?: 0,
            bestTimeMillis = preferences[PreferencesKeys.BEST_TIME_MILLIS] ?: 0L
        )
    }

    suspend fun setThemeMode(theme: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = theme }
    }

    suspend fun setBoardTheme(boardTheme: String) {
        context.dataStore.edit { it[PreferencesKeys.BOARD_THEME] = boardTheme }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HAPTIC_ENABLED] = enabled }
    }

    suspend fun setHighlightConnected(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.HIGHLIGHT_CONNECTED] = enabled }
    }

    suspend fun setShowTimer(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_TIMER] = enabled }
    }

    suspend fun incrementGamesPlayed() {
        context.dataStore.edit { prefs ->
            val current = prefs[PreferencesKeys.GAMES_PLAYED] ?: 0
            prefs[PreferencesKeys.GAMES_PLAYED] = current + 1
        }
    }

    suspend fun recordGameCompletion(moves: Int, timeMillis: Long) {
        context.dataStore.edit { prefs ->
            val completed = (prefs[PreferencesKeys.PIPELINES_COMPLETED] ?: 0) + 1
            prefs[PreferencesKeys.PIPELINES_COMPLETED] = completed

            val prevBestMoves = prefs[PreferencesKeys.BEST_MOVES] ?: 0
            if (prevBestMoves == 0 || moves < prevBestMoves) {
                prefs[PreferencesKeys.BEST_MOVES] = moves
            }

            val prevBestTime = prefs[PreferencesKeys.BEST_TIME_MILLIS] ?: 0L
            if (prevBestTime == 0L || timeMillis < prevBestTime) {
                prefs[PreferencesKeys.BEST_TIME_MILLIS] = timeMillis
            }
        }
    }

    suspend fun resetStatistics() {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GAMES_PLAYED] = 0
            prefs[PreferencesKeys.PIPELINES_COMPLETED] = 0
            prefs[PreferencesKeys.BEST_MOVES] = 0
            prefs[PreferencesKeys.BEST_TIME_MILLIS] = 0L
        }
    }
}
