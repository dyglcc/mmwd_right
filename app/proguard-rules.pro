-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * implements java.io.Serializable {*;}
-keep class **.R$* { *;}

-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
-keep class qfpay.wxshop.data.** { *;}
-keep class qfpay.wxshop.data.handler.MainHandler$*{ *;}
-keep class qfpay.wxshop.activity.menu.MyInComeActivity$*{ *;}
-keep class qfpay.wxshop.activity.menu.ForumActivity$*{ *;}

-keepattributes Signature 
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keepnames class qfpay.wxshop.netImpl.** {*;}
-keepnames class qfpay.wxshop.imageprocesser.** {*;}
-keepnames class qfpay.wxshop.beans.** {*;}

-keep class com.de.greenrobot.event.** { *;}
-dontwarn com.de.greenrobot.event.**

-keep class retrofit.** { *;}
-dontwarn retrofit.**

-keep class com.squareup.okhttp.** { *;}
-dontwarn com.squareup.okhttp.**

-keep class com.squareup.picasso.** { *;}
-dontwarn com.squareup.picasso.**

-keep class okio.** { *;}
-dontwarn okio.**
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep class org.androidannotations.** { *;}
-dontwarn org.androidannotations.**

-keep class com.google.gson.** { *;}
-dontwarn com.google.gson.**

-keep class android.androidquery.** { *;}
-dontwarn android.androidquery.**

-keep class org.apache.http.entity.mine.** { *;}
-dontwarn org.apache.http.entity.mine.**

-keep class m.framework.** { *;}
-dontwarn m.framework.**

-dontwarn com.umeng.**
-keepattributes *Annotation*
-keep class com.umeng*.** {*; }

-dontwarn com.igexin.**
-keep class com.igexin.** { *; }

-keep class cn.sharesdk.** { *;}
-dontwarn cn.sharesdk.**

-keep class com.j256.**
-keepclassmembers class com.j256.** {*;}
-keep enum com.j256.**
-keepclassmembers enum com.j256.**  {*;}
-keep interface com.j256.**
-keepclassmembers interface com.j256.**  {*;}

-keep public class com.umeng.fb.ui.ThreadView {
}

-keep public class * implements java.io.Serializable{ 
    public protected private *; 
}

-keep public class qfpay.wxshop.R$*{
    public static final int *;
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class * extends com.actionbarsherlock.app.SherlockFragmentActivity {
   *;
}

-keepclassmembers class ** {
    public void onEvent*(**);
}

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}