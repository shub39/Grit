-dontwarn io.ktor.util.debug.**

# Glance fuckups
-keep class androidx.work.InputMerger { *; }
-keep class * extends androidx.work.InputMerger { *; }
-keep class androidx.work.impl.** { *; }
-keepattributes *Annotation*
-keepattributes Signature