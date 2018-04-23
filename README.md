# HappySupportAndroid

[![](https://jitpack.io/v/happyfresh/HappySupportAndroid.svg)](https://jitpack.io/#happyfresh/HappySupportAndroid) ![Downloads](https://jitpack.io/v/happyfresh/HappySupportAndroid/month.svg)

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
        implementation 'com.github.happyfresh.HappySupportAndroid:happysupport:1.0.0'
        // extentions for kotlin
        implementation 'com.github.happyfresh.HappySupportAndroid:happysupport-kotlinextentions:1.0.0'
}
```

## StringHelper
Basicaly string helper will call `Context.getString`, but in our string we can put another resource string like below
```xml
<string name="app_name">HappySupportAndroid</string>
<!-- {@string/app_name} will be replace into HappySupportAndroid -->
<string name="hallo">Hallo {@string/app_name}</string>
```
For support that, we just call get string with StringHelper like below
```kotlin
class MainActivity : AppCompatActivity() {
  
  ...
  
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ...
        
        StringHelper.getString(this, R.string.hallo)
        
        // using kotlin extentions
        getStringHelper(R.string.hallo)
        
        ...
        
    }

  ...
  
}
```

We also support formatArgs and another resource will be read as string.
```xml
<color name="colorPrimary">#3F51B5</color>

<string name="app_name">HappySupportAndroid</string>
<string name="hallo">Hallo {@string/app_name} %1$s</string>
<!-- {@color/colorPrimary} will be replace into #3f51b5 -->
<string name="hallo_2"><![CDATA[Hi %1$s %2$d <b>{@string/hallo}</b> <font color="{@color/colorPrimary}">{@color/colorPrimary}</font>]]></string>
```
```kotlin
class MainActivity : AppCompatActivity() {
  
  ...
  
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        ...
        
        StringHelper.getString(this, R.string.hallo_2, arrayOf("Happy", 2), arrayOf("Fresh"))
        
        // using kotlin extentions
        getStringHelper(R.string.hallo_2, arrayOf("Happy", 2), arrayOf("Fresh"))
        
        ...
        
    }

  ...
  
}
```
