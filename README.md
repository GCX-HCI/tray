# Tray - a SharedPreferences replacement for Android

If you have read the documentation of the [`SharedPreferences`](http://developer.android.com/reference/android/content/SharedPreferences.html) you might have seen this:

>Note: currently this class does not support use across multiple processes. This will be added later.

**Sometimes _later_ becomes _never_!**

Tray solves this problem with a [`ContentProvider`](http://developer.android.com/reference/android/content/ContentProvider.html) based storage. Tray also provides a more advanced API which makes it super easy to access and maintain your data with upgrade and migrate mechanisms. Therefore it can be used as a SharedPreferences replacement.

## Features

- **works multiprocess**
- stores simple data types as key value pairs
- automatically saves metadata for each entry (created, last updated, ...)
- manage your Preferences in modules [TrayModulePreference](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/TrayModulePreferences.java#L37)
- update and migrate your data from one app version to next one with versioned Preferences and a [`onUpgrade()`](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/accessor/Preference.java#L69) method.
- Migrate your current data stored in the SharedPreferences to Tray with [`SharedPreferencesImport`](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/migration/SharedPreferencesImport.java)
- 

## Usage


## Getting Started

### Add Tray to your project

#### Maven

**Sorry, Maven integration is missing. We are working on it for the final 1.0 release!**

#### As module

Add the library as module to your project *(protip: git submodule)* and add tray as dependency to the `build.gradle` of your app.

```java
dependencies {
    ...
    compile project(':tray:library')     //if your module is named tray
}
```

Don't forget to add the module to the `settings.gradle`
```java
include ':tray:library'
```

### Set the authority

Tray is based on a ContentProvider. A ContentProvider needs a **unique** authority. When you use the same authority for multiple apps you will be unable to install the app due to a authority conflict with the error message:

```
Failure [INSTALL_FAILED_CONFLICTING_PROVIDER]
```

To set the authority you need to override the string resource of the library with `resValue` in your `build.gradle`
```java
android {
    ...
    defaultConfig {
        applicationId "your.app.id" // this is your unique applicationId

        resValue "string", "tray__authority", "${applicationId}.tray" // add this to set a unique tray authority based on your applicationId
    }
}
```

Clean your project afterwards to genaterate the `/build/generated/res/generated/BUILDTYPE/values/generated.xml` which should contain the following value:

```xml
    <!-- Values from default config. -->
    <item name="tray__authority" type="string">your.app.id.tray</item>
```

Changing the authority from one version to another app version is no problem! Tray always uses the same database.

## Save and read preferences

```java
// create a preference accessor. This is for global app preferences.
final TrayAppPreferences appPreferences = new TrayAppPreferences(getContext()); //this Preference comes for free from the library
// save a key value pair
appPreferences.put("key", "lorem ipsum");

// read the value for your key. the second parameter is a fallback
final String value = appPreferences.getString("key", "default");
Log.v(TAG, "value: " + value); // value: lorem ipsum

// read a key that isn't saved. returns the default
final String defaultValue = appPreferences.getString("key2", "default");
Log.v(TAG, "value: " + defaultValue); // value: default
```



## Versions
- no version published so far


# License

```
Copyright 2015 grandcentrix GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
