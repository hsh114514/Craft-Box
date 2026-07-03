# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ============================================
# R8 配置：只裁剪，不混淆
# ============================================

# 禁用混淆，只进行代码裁剪和压缩
-dontobfuscate

# 优化代码（可选，会移除无用代码）
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-optimizationpasses 5

# ============================================
# 保持 Gson 相关类不被混淆
# ============================================

# Keep Gson data classes - 保持所有实体类字段名
-keep class com.start.craftbox.Entity.** { *; }
-keep class com.start.craftbox.Activitys.LoginActivity$LoginResponse { *; }
-keep class com.start.craftbox.Activitys.LoginActivity$LoginResponse$UserData { *; }

# Keep all fields in classes used with Gson
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic signature for Gson
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# ============================================
# 保持 AIDL 接口不被混淆
# ============================================

# Keep AIDL interfaces and generated classes
-keep interface com.start.craftbox.Services.** { *; }
-keep class com.start.craftbox.Services.**$Stub { *; }
-keep class com.start.craftbox.Services.**$Stub$Proxy { *; }

-keep interface android.content.pm.** { *; }
-keep class android.content.pm.**$Stub { *; }
-keep class android.content.pm.**$Stub$Proxy { *; }

# Keep Binder interfaces
-keep interface * extends android.os.IInterface { *; }
-keep class * extends android.os.Binder { *; }

# ============================================
# 保持 Android 框架所需的基本规则
# ============================================

# Keep Parcelable classes
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep native methods
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Keep View constructors (for layout inflation)
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep custom view setters/getters
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# Keep Activity methods referenced from manifest
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver

# Keep Fragment classes
-keep class * extends androidx.fragment.app.Fragment

# Keep Kotlin metadata (if using Kotlin libraries)
-keep class kotlin.Metadata { *; }

# ============================================
# 日志控制（发布版本可移除日志）
# ============================================

# 移除 Log 调用（可选）
#-assumenosideeffects class android.util.Log {
#    public static *** d(...);
#    public static *** v(...);
#    public static *** i(...);
#}
