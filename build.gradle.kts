plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.android.library") version "8.1.2" apply false
    // 移除 Google Services 插件
    id("com.google.gms.google-services") version "4.3.15" apply false
    kotlin("android") version "1.8.20" apply false  // 添加 Kotlin 插件
}