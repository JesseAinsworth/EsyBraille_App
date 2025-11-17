# Add project specific ProGuard rules here.

# --- Reglas para Gson (usado por Retrofit) ---
# Mantiene los nombres de los campos en tus clases de modelos de datos (AuthResponse, etc.)
# Si no haces esto, Gson no sabrá cómo mapear el JSON a tus objetos.
-keepclassmembers,allowobfuscation class com.easybraille.network.** { *; }
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken

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
