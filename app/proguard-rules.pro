# Add project specific ProGuard rules here.

# Ktor and Serialization
# Keep members annotated with @Serializable
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# Resolve "Unresolved class name" error by ignoring library-internal references
-dontwarn io.ktor.**
-dontwarn kotlinx.serialization.**
-dontwarn kotlin.reflect.**
