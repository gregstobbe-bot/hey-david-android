package com.hey.david;
import android.content.Intent;
import android.speech.RecognitionService;
public final class DavidRecognitionService extends RecognitionService {
    @Override protected void onStartListening(Intent intent, Callback listener) { listener.error(RecognitionService.ERROR_CLIENT); }
    @Override protected void onStopListening(Callback listener) {}
    @Override protected void onCancel(Callback listener) {}
}
