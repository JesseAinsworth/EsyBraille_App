# Add project specific ProGuard rules here.

# --- Reglas para Gson (usado por Retrofit) ---
# Mantiene los nombres de los campos en tus clases de modelos de datos (AuthResponse, etc.)
# Si no haces esto, Gson no sabrá cómo mapear el JSON a tus objetos.
-keepclassmembers,allowobfuscation class com.easybraille.network.** { *; }
-keep class com.easybraille.network.** { *; }
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken

# Mantener anotaciones de Gson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod

# Reglas específicas para modelos de datos
-keep class com.easybraille.network.AdminModels** { *; }
-keep class com.easybraille.network.AuthResponse** { *; }
-keep class com.easybraille.network.TranslationModels** { *; }

# Mantener nombres de campos para serialización JSON
-keepclassmembers class com.easybraille.network.** {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Reglas para OkHttp y Retrofit ---
# Estas son las reglas estándar recomendadas por los desarrolladores de OkHttp.
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# --- Reglas para Conscrypt (Proveedor de Seguridad de Red) ---
# Evita que se eliminen las clases necesarias para las conexiones seguras (HTTPS).
-keep class org.conscrypt.** { *; }
-dontwarn org.conscrypt.**

# --- Ignorar advertencias de dependencias transitivas no utilizadas ---
# Silencia la advertencia sobre BlockHound, que no estás usando directamente.
-dontwarn reactor.blockhound.**

# --- Reglas generales recomendadas para Android ---
-keepattributes SourceFile,LineNumberTable
