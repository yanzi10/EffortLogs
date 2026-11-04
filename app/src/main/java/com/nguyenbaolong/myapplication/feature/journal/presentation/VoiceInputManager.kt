package com.nguyenbaolong.myapplication.feature.journal.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed interface VoiceState {
    data object Idle : VoiceState
    data object Listening : VoiceState
    data object Processing : VoiceState
    data class Success(val text: String) : VoiceState
    data class Error(val message: String, val canFallbackToSystem: Boolean = true) : VoiceState
}

class VoiceInputManager(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val googleRecognitionComponent = ComponentName(
        "com.google.android.googlequicksearchbox",
        "com.google.android.voicesearch.serviceapi.GoogleRecognitionService"
    )

    fun isGoogleRecognitionAvailable(): Boolean {
        val pm = context.packageManager
        val intent = Intent(RecognitionService_ACTION)
        intent.component = googleRecognitionComponent
        val services = pm.queryIntentServices(intent, 0)
        return services.isNotEmpty()
    }

    fun isSystemRecognitionAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    fun createSpeechIntent(): Intent {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.getDefault().toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak effort (e.g. 'Studied English for 30 minutes this morning')")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        return intent
    }

    /**
     * Attempts to start in-app speech recognizer using Google's engine where possible.
     * If speech recognizer is busy or unavailable, it invokes [onAutoFallback] immediately
     * to seamlessly launch the system/Google speech recognizer without blocking the user.
     */
    fun startListening(onAutoFallback: (() -> Unit)? = null) {
        mainHandler.post {
            try {
                destroy()

                val recognizer = try {
                    if (isGoogleRecognitionAvailable()) {
                        SpeechRecognizer.createSpeechRecognizer(context, googleRecognitionComponent)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
                    ) {
                        try {
                            SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
                        } catch (_: Exception) {
                            SpeechRecognizer.createSpeechRecognizer(context)
                        }
                    } else {
                        SpeechRecognizer.createSpeechRecognizer(context)
                    }
                } catch (e: Exception) {
                    onAutoFallback?.invoke()
                    return@post
                }

                if (recognizer == null) {
                    onAutoFallback?.invoke()
                    return@post
                }

                speechRecognizer = recognizer

                val intent = createSpeechIntent()

                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _voiceState.value = VoiceState.Listening
                    }

                    override fun onBeginningOfSpeech() {
                        _voiceState.value = VoiceState.Listening
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _voiceState.value = VoiceState.Processing
                    }

                    override fun onError(error: Int) {
                        // On Xiaomi / MIUI / Android 12+, ERROR_CLIENT (5) or ERROR_RECOGNIZER_BUSY (8)
                        // happens when on-device engine refuses third-party binding.
                        // Automatically fallback to system Google recognizer without annoying popup!
                        if ((error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) && onAutoFallback != null) {
                            destroy()
                            onAutoFallback.invoke()
                            return
                        }

                        val (message, canFallback) = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check microphone." to false
                            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition busy. Switching to system voice..." to true
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required." to false
                            SpeechRecognizer.ERROR_NETWORK -> "Network issue. Tap mic to retry." to true
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Try speaking again." to true
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Tap mic to try again." to true
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Service busy. Switching to system voice..." to true
                            SpeechRecognizer.ERROR_SERVER -> "Recognition server error." to true
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Tap mic to speak." to true
                            else -> "Recognition error ($error). Tap mic to retry." to true
                        }
                        _voiceState.value = VoiceState.Error(message, canFallback)
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val recognizedText = matches?.firstOrNull()?.trim()
                        if (!recognizedText.isNullOrBlank()) {
                            _voiceState.value = VoiceState.Success(recognizedText)
                        } else {
                            _voiceState.value = VoiceState.Error("No words detected. Tap to try again.", true)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                recognizer.startListening(intent)
                _voiceState.value = VoiceState.Listening
            } catch (e: Exception) {
                if (onAutoFallback != null) {
                    onAutoFallback.invoke()
                } else {
                    _voiceState.value = VoiceState.Error(
                        message = e.localizedMessage ?: "Failed to start speech recognizer",
                        canFallbackToSystem = true
                    )
                }
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
            } catch (_: Exception) {}
        }
    }

    fun resetState() {
        _voiceState.value = VoiceState.Idle
    }

    fun destroy() {
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
            speechRecognizer = null
        }
    }

    companion object {
        private const val RecognitionService_ACTION = "android.speech.RecognitionService"
    }
}
