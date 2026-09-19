# SmartRemote Pro ProGuard Rules

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Gson
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Domain and Data Models
-keep class com.smartremote.pro.data.local.database.entities.** { *; }
-keep class com.smartremote.pro.domain.models.** { *; }
-keep class com.smartremote.pro.core.ir.** { *; }
-keep class com.smartremote.pro.core.voice.** { *; }

# Coroutines
-dontwarn kotlinx.coroutines.**
