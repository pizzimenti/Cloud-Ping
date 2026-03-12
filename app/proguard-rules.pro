# Add project specific ProGuard rules here.
# You can find general rules for popular libraries at
# https://github.com/consumer-pro/proguard-snippets/

# Rules for Ktor
# Using more specific rules to avoid overly broad keep warnings
-keepattributes Signature, *Annotation*
-keep class io.ktor.client.engine.cio.** { *; }
-keep class io.ktor.serialization.kotlinx.json.** { *; }
-dontwarn io.ktor.**

# Rules for Kotlinx Serialization
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}
-keep class kotlinx.serialization.json.** { *; }
-dontwarn kotlinx.serialization.**
