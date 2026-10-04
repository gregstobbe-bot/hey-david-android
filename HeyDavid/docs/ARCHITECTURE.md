# Hey David architecture

Moto G Pure / Android 11 -> selected assistant -> VoiceInteractionService -> foreground SpeechRecognizer fallback -> phrase match -> ChatGPT.

The assistant service is the system integration point. The foreground recognizer is the practical arbitrary-phrase fallback. Android 11 permits microphone use by a foreground service when the required permission is granted and the service is correctly started.

The project deliberately does not use root, unrestricted shell access, credential extraction, hidden surveillance, or arbitrary remote control. Accessibility is present only as an optional, user-enabled scaffold for future explicitly scoped actions.

Termux/native wake-lock integration remains deferred.
