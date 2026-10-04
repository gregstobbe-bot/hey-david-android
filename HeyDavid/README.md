# Hey David Android

Android 11 assistant-control baseline for the Moto G Pure.

This repository contains the assistant-role service, voice-session scaffold, foreground speech-recognition fallback, optional accessibility scaffold, boot receiver, ChatGPT handoff, and GitHub Actions APK build.

Android does not expose a universal arbitrary offline hotword engine to an ordinary third-party app. The baseline therefore combines the user-selected assistant role with the foreground speech-recognition fallback for the phrase Hey David.

The Termux/native wake-lock experiment remains deferred.
