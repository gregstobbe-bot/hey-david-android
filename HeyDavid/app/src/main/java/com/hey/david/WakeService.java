package com.hey.david;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Locale;

public final class WakeService extends Service implements RecognitionListener {
    private static final String CHANNEL = "hey_david_listener";
    private final Handler handler = new Handler();
    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;
    @Override public void onCreate() {
        super.onCreate();
        NotificationManager nm = getSystemService(NotificationManager.class);
        if (nm != null) nm.createNotificationChannel(new NotificationChannel(CHANNEL, getString(R.string.listener_channel), NotificationManager.IMPORTANCE_LOW));
        Notification n = new Notification.Builder(this, CHANNEL)
                .setContentTitle("Hey David").setContentText(getString(R.string.listener_notification))
                .setSmallIcon(android.R.drawable.ic_btn_speak_now).setOngoing(true).build();
        startForeground(1001, n);
        if (!SpeechRecognizer.isRecognitionAvailable(this)) return;
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        listen();
    }
    private void listen() { try { if (recognizer != null) recognizer.startListening(recognizerIntent); } catch (Exception ignored) {} }
    private void restart() { handler.postDelayed(this::listen, 400); }
    private void inspect(ArrayList<String> results) {
        if (results == null) return;
        for (String s : results) {
            String n = s.toLowerCase(Locale.US).replaceAll("[^a-z0-9 ]", " ").replaceAll("\\s+", " ").trim();
            if (n.contains("hey david") || n.equals("david")) { ChatHandoff.open(this); return; }
        }
    }
    @Override public void onResults(Bundle b) { inspect(b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)); restart(); }
    @Override public void onPartialResults(Bundle b) { inspect(b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)); }
    @Override public void onError(int e) { restart(); }
    @Override public void onReadyForSpeech(Bundle b) {}
    @Override public void onBeginningOfSpeech() {}
    @Override public void onRmsChanged(float v) {}
    @Override public void onBufferReceived(byte[] b) {}
    @Override public void onEndOfSpeech() {}
    @Override public void onEvent(int e, Bundle b) {}
    @Override public void onDestroy() { handler.removeCallbacksAndMessages(null); if (recognizer != null) { recognizer.destroy(); recognizer=null; } super.onDestroy(); }
    @Override public IBinder onBind(Intent intent) { return null; }
}
