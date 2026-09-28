package com.example.pipeconnectboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pipeconnectboard.audio.SoundEffect
import com.example.pipeconnectboard.audio.SoundPlayer
import com.example.pipeconnectboard.data.GamePreferencesRepository
import com.example.pipeconnectboard.data.GameStatistics
import com.example.pipeconnectboard.data.UserSettings
import com.example.pipeconnectboard.haptics.HapticManager
import com.example.pipeconnectboard.haptics.HapticType
import com.example.pipeconnectboard.logic.ConnectivitySolver
import com.example.pipeconnectboard.logic.PuzzleFactory
import com.example.pipeconnectboard.model.GridPosition
import com.example.pipeconnectboard.model.PipeCell
import com.example.pipeconnectboard.model.PipeConnectState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = GamePreferencesRepository(application)
    private val soundPlayer = SoundPlayer(viewModelScope)
    private val hapticManager = HapticManager(application)

    val userSettings: StateFlow<UserSettings> = preferencesRepository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserSettings())

    val statistics: StateFlow<GameStatistics> = preferencesRepository.statisticsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, GameStatistics())

    private val _gameState = MutableStateFlow(PuzzleFactory.createInitialState())
    val gameState: StateFlow<PipeConnectState> = _gameState.asStateFlow()

    private val undoStack = ArrayDeque<PipeConnectState>()
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private var timerJob: Job? = null

    init {
        // Observe settings for sound and haptics sync
        viewModelScope.launch {
            userSettings.collect { settings ->
                soundPlayer.isEnabled = settings.soundEnabled
                hapticManager.isEnabled = settings.hapticEnabled
            }
        }
        startNewGame()
    }

    fun startNewGame() {
        timerJob?.cancel()
        undoStack.clear()
        _canUndo.value = false

        val initial = PuzzleFactory.createInitialState()
        _gameState.value = initial

        viewModelScope.launch {
            preferencesRepository.incrementGamesPlayed()
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                val current = _gameState.value
                if (!current.isComplete && !current.isFailed) {
                    _gameState.value = current.copy(elapsedMillis = current.elapsedMillis + 1000L)
                }
            }
        }
    }

    fun selectTile(tileId: Int) {
        val current = _gameState.value
        if (current.isComplete || current.isFailed || current.isAnimating) return
        if (current.selectedTileId == tileId) return

        _gameState.value = current.copy(selectedTileId = tileId)
        soundPlayer.play(SoundEffect.SELECT)
        hapticManager.trigger(HapticType.SELECTION)
    }

    fun rotateSelectedTile() {
        val current = _gameState.value
        if (current.isComplete || current.isFailed || current.isAnimating) return
        val selectedId = current.selectedTileId ?: return

        val updatedTray = current.tray.map { tile ->
            if (tile.id == selectedId) {
                tile.rotatedClockwise()
            } else {
                tile
            }
        }

        _gameState.value = current.copy(tray = updatedTray)
        soundPlayer.play(SoundEffect.ROTATE)
        hapticManager.trigger(HapticType.ROTATION)
    }

    fun placePipe(position: GridPosition) {
        val current = _gameState.value

        // 1. Verify game active
        if (current.isComplete || current.isFailed || current.isAnimating) return

        // 2. Verify selected tile exists
        val selected = current.selectedTile ?: return

        // 3. Verify destination cell is EMPTY
        if (position.row !in current.board.indices || position.col !in current.board[position.row].indices) return
        val targetCell = current.board[position.row][position.col]
        if (targetCell !is PipeCell.Empty) return

        // 4. Save Undo snapshot
        undoStack.addLast(current)
        _canUndo.value = true

        // 5. Lock input
        _gameState.value = current.copy(isAnimating = true)

        viewModelScope.launch {
            // Short placement animation duration (120ms)
            delay(120L)

            // 6. Place selected pipe
            val newBoard = current.board.map { it.toMutableList() }
            newBoard[position.row][position.col] = PipeCell.Pipe(selected)

            // 7. Remove it from tray
            val newTray = current.tray.filter { it.id != selected.id }.toMutableList()

            // 8. Increment move count
            val newMoveCount = current.moveCount + 1

            // 9. Draw replacement from bag
            val newBag = current.bag.toMutableList()
            if (newBag.isNotEmpty()) {
                val drawn = newBag.removeAt(0)
                newTray.add(drawn)
            }

            // Next selected tile in tray
            val nextSelectedId = newTray.firstOrNull()?.id

            // 10. Run connectivity search from source
            val newConnected = ConnectivitySolver.findConnectedFromSource(
                board = newBoard,
                sourcePosition = PuzzleFactory.SOURCE_POS
            )

            // Feedback check: did connection extend?
            val connectionExtended = newConnected.size > current.connectedCells.size

            // 12. Check if destination is reachable
            val isComplete = ConnectivitySolver.isDestinationConnected(
                connectedCells = newConnected,
                destinationPosition = PuzzleFactory.DESTINATION_POS
            )

            // 13. Else if tile supply exhausted
            val isFailed = !isComplete && newTray.isEmpty() && newBag.isEmpty()

            _gameState.value = current.copy(
                board = newBoard,
                tray = newTray,
                bag = newBag,
                selectedTileId = nextSelectedId,
                moveCount = newMoveCount,
                connectedCells = newConnected,
                isComplete = isComplete,
                isFailed = isFailed,
                isAnimating = false
            )

            // Trigger appropriate audio & haptic responses
            when {
                isComplete -> {
                    timerJob?.cancel()
                    soundPlayer.play(SoundEffect.WIN)
                    hapticManager.trigger(HapticType.SUCCESS)
                    preferencesRepository.recordGameCompletion(newMoveCount, current.elapsedMillis)
                }
                isFailed -> {
                    timerJob?.cancel()
                    soundPlayer.play(SoundEffect.FAIL)
                    hapticManager.trigger(HapticType.FAILURE)
                }
                connectionExtended -> {
                    soundPlayer.play(SoundEffect.EXTEND)
                    hapticManager.trigger(HapticType.CONNECTION_EXTENDED)
                }
                else -> {
                    soundPlayer.play(SoundEffect.PLACE)
                    hapticManager.trigger(HapticType.PLACEMENT)
                }
            }
        }
    }

    fun undo() {
        val current = _gameState.value
        if (current.isAnimating || undoStack.isEmpty()) return

        val restored = undoStack.removeLast()
        _canUndo.value = undoStack.isNotEmpty()
        _gameState.value = restored

        soundPlayer.play(SoundEffect.SELECT)
        hapticManager.trigger(HapticType.SELECTION)
    }

    fun restart() {
        startNewGame()
    }

    // Settings actions
    fun setThemeMode(theme: String) {
        viewModelScope.launch { preferencesRepository.setThemeMode(theme) }
    }

    fun setBoardTheme(boardTheme: String) {
        viewModelScope.launch { preferencesRepository.setBoardTheme(boardTheme) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setSoundEnabled(enabled) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setHapticEnabled(enabled) }
    }

    fun setHighlightConnected(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setHighlightConnected(enabled) }
    }

    fun setShowTimer(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setShowTimer(enabled) }
    }

    fun resetStatistics() {
        viewModelScope.launch { preferencesRepository.resetStatistics() }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
