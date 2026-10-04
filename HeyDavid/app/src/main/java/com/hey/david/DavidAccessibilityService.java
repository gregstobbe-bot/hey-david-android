package com.hey.david;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
public final class DavidAccessibilityService extends AccessibilityService {
    @Override protected void onServiceConnected() { super.onServiceConnected(); }
    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
}
