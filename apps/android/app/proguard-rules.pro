# Custom ProGuard rules for ScribbleFit

# Keep kotlinx-serialization classes and members
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# Keep AI DTOs from being obfuscated, as they are parsed from JSON
-keep class com.scribblefit.feature.ai.data.entity.** { *; }

# Firebase AI rules
-keep class com.google.firebase.ai.** { *; }
