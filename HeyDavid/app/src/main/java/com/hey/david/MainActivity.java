package com.hey.david;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private static final int REQ_MIC = 10;
    private static final int REQ_ASSISTANT = 11;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(40,40,40,40);
        TextView status = new TextView(this);
        status.setText("Hey David\\n\\nAndroid assistant + wake-phrase control.");
        root.addView(status);
        Button mic = new Button(this);
        mic.setText("Grant microphone + start listener");
        mic.setOnClickListener(v -> requestMicAndStart());
        root.addView(mic);
        Button assistant = new Button(this);
        assistant.setText("Set Hey David as assistant");
        assistant.setOnClickListener(v -> requestAssistantRole());
        root.addView(assistant);
        Button chatgpt = new Button(this);
        chatgpt.setText("Test ChatGPT handoff");
        chatgpt.setOnClickListener(v -> ChatHandoff.open(this));
        root.addView(chatgpt);
        Button accessibility = new Button(this);
        accessibility.setText("Open accessibility settings (optional)");
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(accessibility);
        setContentView(root);
    }
    private void requestMicAndStart() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_MIC); return;
        }
        startWakeService();
    }
    private void startWakeService() {
        startForegroundService(new Intent(this, WakeService.class));
        Toast.makeText(this, "Hey David listener started", Toast.LENGTH_SHORT).show();
    }
    private void requestAssistantRole() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            RoleManager rm = getSystemService(RoleManager.class);
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_ASSISTANT) && !rm.isRoleHeld(RoleManager.ROLE_ASSISTANT)) {
                startActivityForResult(rm.createRequestRoleIntent(RoleManager.ROLE_ASSISTANT), REQ_ASSISTANT); return;
            }
        }
        Toast.makeText(this, "Assistant role is unavailable", Toast.LENGTH_LONG).show();
    }
    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grants) {
        super.onRequestPermissionsResult(requestCode, permissions, grants);
        if (requestCode == REQ_MIC && grants.length > 0 && grants[0] == PackageManager.PERMISSION_GRANTED) startWakeService();
    }
}
