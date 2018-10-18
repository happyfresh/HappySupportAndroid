# HappySupportAndroid

[![](https://jitpack.io/v/happyfresh/HappySupportAndroid.svg)](https://jitpack.io/#happyfresh/HappySupportAndroid)

This library for supporting our android development

## How to Get
To get our support for android development into your build:

<b>Step 1.</b> Add the JitPack repository to your build file
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
<b>Step 2.</b> Add the dependency
```gradle
dependencies {
        // HappySupport
        implementation "com.github.happyfresh.HappySupportAndroid:happysupport:$latest_version"
        // extentions for kotlin
        implementation "com.github.happyfresh.HappySupportAndroid:happysupport-kotlinextentions:$latest_version"
        
        // HappyTracker
        implementation "com.github.happyfresh.HappySupportAndroid:happytracker:$latest_version"
        
        // HappyRouter
        implementation "com.github.happyfresh.HappySupportAndroid:happyrouter:$latest_version"
        annotationProcessor "com.github.happyfresh.HappySupportAndroid:happyrouter-processor:$latest_version"
}
```

## [Index](https://github.com/happyfresh/HappySupportAndroid/wiki)
### Helper
* [StringHelper](https://github.com/happyfresh/HappySupportAndroid/wiki/StringHelper)

### [HappyTracker](https://github.com/happyfresh/HappySupportAndroid/wiki/HappyTracker)

### [HappyRouter](https://github.com/happyfresh/HappySupportAndroid/wiki/HappyRouter)
