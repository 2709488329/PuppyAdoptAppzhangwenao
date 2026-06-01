# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep the game data model
-keep class com.puppyadopt.data.** { *; }

# Keep ViewModel
-keep class com.puppyadopt.data.GameViewModel { *; }
