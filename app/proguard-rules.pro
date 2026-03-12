# Add project specific ProGuard rules here.
# You can find general rules for popular libraries at
# https://github.com/consumer-pro/proguard-snippets/

# Rules for Ktor
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Rules for Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class ** { @kotlinx.serialization.Serializable *; }
