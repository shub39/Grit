-assumevalues public class androidx.compose.runtime.ComposeRuntimeFlags {
    static boolean isLinkBufferComposerEnabled return true;
}

# Glance fuckups
-keep class androidx.work.InputMerger { *; }
-keep class * extends androidx.work.InputMerger { *; }
-keep class androidx.work.impl.** { *; }
-keepattributes *Annotation*
-keepattributes Signature