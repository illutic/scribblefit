# 🚀 ScribbleFit v1.0

We are thrilled to announce the official launch of **ScribbleFit 1.0**! 

ScribbleFit redefines how you track your workouts by combining the simplicity of journaling with the power of artificial intelligence. No more rigid forms or complicated menus—just scribble your workout, and we handle the rest. Available now for both Android and iOS!

## ✨ Key Features

### 📝 The Canvas: AI-Powered Workout Logging
- **Freeform Scribbles**: Just type naturally (e.g., "Did 3 sets of bench press 135x10, 145x8, 155x5 and ran 2 miles").
- **Intelligent Parsing**: Our advanced on-device and cloud LLM capabilities instantly parse your unstructured text into structured exercise data, identifying movements, sets, reps, and weights automatically.
- **Minimalist Interface**: Distraction-free, glassmorphic "Digital Atelier" design that keeps the focus entirely on your training.

### 📊 Exercise Trends & History
- **Deep Dive Insights**: Track your progress over time with dedicated Exercise Details screens.
- **Performance Charts**: Visualize your lifting trends, volume, and max weight with beautiful, native charts (using Swift Charts on iOS and Compose on Android).
- **Session History**: Easily review your past performances for any specific movement.

### 📅 The Ledger
- **Your Training Diary**: A comprehensive timeline of all your logged scribbles and sessions.
- **Reactive Data**: Add or delete an exercise, and watch your ledger update instantly across the app thanks to our fully reactive MVI architecture.

### ⚙️ Smart Settings
- **AI Model Selection**: Seamlessly toggle between local and cloud-based LLMs based on your privacy preferences and network availability.
- **Cross-Platform Parity**: 100% native experiences crafted specifically for Android (Jetpack Compose) and iOS 17+ (SwiftUI), sharing the same robust domain logic and design system.

## 🛠 Under the Hood
- **Offline-First Persistence**: Powered by Room (Android) and SwiftData (iOS) for lightning-fast, offline-capable saving.
- **Privacy First**: Your workout data stays on your device unless you explicitly opt into cloud sync or cloud AI processing.
- **Performance**: Optimized startup times, fluid micro-animations, and dynamic adaptive layouts for larger screens and tablets.

Thank you for being part of the journey. Get ready to ditch the spreadsheets and just scribble your fit!
