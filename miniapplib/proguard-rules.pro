# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# กันไม่ให้ Public API ของ lib ถูกลบ/เปลี่ยนชื่อมากเกินไป
-keep class com.example.miniapplib.PaymentLib {
    *;
}

-keep class com.example.miniapplib.PaymentResult {
    *;
}

-keep class com.example.miniapplib.QrScanResult {
    *;
}

-keep class com.example.miniapplib.MiniAppWebviewActivity {
    *;
}

-keep class com.example.miniapplib.ScannerActivity {
    *;
}

# สำคัญมาก: อย่าลบ method ที่ติด @JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
