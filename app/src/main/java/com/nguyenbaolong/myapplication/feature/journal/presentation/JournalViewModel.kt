package com.nguyenbaolong.myapplication.feature.journal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogEntity
import com.nguyenbaolong.myapplication.feature.journal.data.local.InputType
import com.nguyenbaolong.myapplication.feature.journal.data.repository.JournalRepository
import com.nguyenbaolong.myapplication.feature.journal.domain.NlpParser
import com.nguyenbaolong.myapplication.feature.journal.domain.NlpResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface JournalUiState {
    data object Loading : JournalUiState
    data class Success(val logs: List<ActivityLogEntity>) : JournalUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModel(
    private val repository: JournalRepository,
    private val nlpParser: NlpParser = NlpParser()
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _pendingConfirmation = MutableStateFlow<Pair<NlpResult, InputType>?>(null)
    val pendingConfirmation: StateFlow<Pair<NlpResult, InputType>?> = _pendingConfirmation.asStateFlow()

    val uiState: StateFlow<JournalUiState> = _selectedDate
        .flatMapLatest { date -> repository.getLogsForDate(date) }
        .map<List<ActivityLogEntity>, JournalUiState> { JournalUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JournalUiState.Loading)

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun processTextInput(text: String, targetDate: LocalDate = _selectedDate.value) {
        if (text.isBlank()) return
        val result = nlpParser.parse(input = text, baseDate = targetDate)
        _pendingConfirmation.value = Pair(result, InputType.TEXT)
    }

    fun processVoiceInput(transcript: String, targetDate: LocalDate = _selectedDate.value) {
        if (transcript.isBlank()) return
        val result = nlpParser.parse(input = transcript, baseDate = targetDate)
        _pendingConfirmation.value = Pair(result, InputType.VOICE)
    }

    fun clearPendingConfirmation() {
        _pendingConfirmation.value = null
    }

    fun saveConfirmedLog(log: ActivityLogEntity) {
        viewModelScope.launch {
            repository.addLog(log)
            clearPendingConfirmation()
        }
    }

    fun updateLog(log: ActivityLogEntity) {
        viewModelScope.launch {
            repository.updateLog(log)
        }
    }

    fun deleteLog(log: ActivityLogEntity) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}

