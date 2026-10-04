package com.hey.david;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

final class ChatHandoff {
    private ChatHandoff() {}
    static void open(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent launch = pm.getLaunchIntentForPackage("com.openai.chatgpt");
        if (launch != null) {
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(launch);
            return;
        }
        Intent assist = new Intent(Intent.ACTION_ASSIST).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try { context.startActivity(assist); }
        catch (Exception ignored) {
            Intent settings = new Intent(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settings);
        }
    }
}
