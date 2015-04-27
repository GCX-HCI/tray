# Tray - a SharedPreferences replacement for Android

If you have read the documentation of the [`SharedPreferences`](http://developer.android.com/reference/android/content/SharedPreferences.html) you might have seen this:

>Note: currently this class does not support use across multiple processes. This will be added later.

**Sometimes _later_ becomes _never_!**

Tray solves this problem with a [`ContentProvider`](http://developer.android.com/reference/android/content/ContentProvider.html) based storage. Tray also provides a advanced API which makes it super easy to access and maintain your data with upgrade and migrate mechanisms. Welcome to SharedPreferences 2.0 aka Tray.

## Features

- **works multiprocess**
- stores simple data types as key value pairs
- automatically saves metadata for each entry (created, last updated, ...)
- manage your Preferences in modules [TrayModulePreference](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/TrayModulePreferences.java#L37)
- Delete single modules, all modules, or [all modules except some very important ones](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/Tray.java#L108)
- update and migrate your data from one app version to next one with versioned Preferences and a [`onUpgrade()`](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/accessor/Preference.java#L69) method
- Migrate your current data stored in the SharedPreferences to Tray with [`SharedPreferencesImport`](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/migration/SharedPreferencesImport.java)
- **tray is 100% unit tested!**

## Usage

### Save and read preferences

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

### Create your own preference module

It's recommended to bundle preferences in groups, so called modules instead of putting everyting in one global module. If you where using `SharedPreferences` before, you might have used different files to group your preferences. Extending the `TrayModulePreferences` and put all Keys inside this class is a recommended way to keep your code clean.

```java
// create a preference accessor for a module
public class MyModulePreference extends TrayModulePreferences {

    public static String KEY_IS_FIRST_LAUNCH = "first_launch";

    public MyModulePreference(final Context context) {
        super(context, "myModule");
    }
}
```

```java
// accessing the preferences for my own module
final MyModulePreference myModulePreference = new MyModulePreference(getContext());
myModulePreference.put(MyModulePreference.KEY_IS_FIRST_LAUNCH, false);
```

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

Clean your project afterwards to genaterate the `/build/generated/res/generated/BUILDTYPE/values/generated.xml` which should then contain the following value:

```xml
    <!-- Values from default config. -->
    <item name="tray__authority" type="string">your.app.id.tray</item>
```

Changing the authority from one version to another app version is no problem! Tray always uses the same database.

## Project state

Tray is currently in active development by [grandcentrix](http://www.grandcentrix.net/). We decided to go open source after reaching 100% test coverage.
[grandcentrix](http://www.grandcentrix.net/) uses Tray in producation in two apps without problems. 

Before version 1.0 we'd like to have some feedback.

## ContentProvider is overkill

At first, it was the simpst way to use IPC with [`Binder`](http://developer.android.com/reference/android/os/Binder.html) to solve the multiprocess problem. Using the `ContentProvider` with a database turned out to be very handy when it comes to save metadata. We thought about replacing the database with the real `SharedPreferences` to boost the performance (the SharedPreferences do not access the disk for every read/write action which causes the multiprocess problem btw) but the metadata seemed to be more valuable to us.
If you have found a better solution implement the [`ModularizedStorage`](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/storage/ModularizedStorage.java) and contribute to this project! We would appreciate it.

That said, yes the performance isn't as good as the SharedPreferences. But the performance is good enough to save/access single key value pairs synchron. If you want to save more you should think about a simple database.

## Missing Features

Tray is ready to use without showblockers! Here are some nice to have features:
- maven integration
- saving `null` doesn't work
- no support to save `Set<String>`. Is someone using this?
- more metadata fields: (i.e. app version code/name)

## Versions

##### Version 0.9 `27.04.15`
- initial public release

##### Version 0.2 - 0.8
- Refactoring
- 100% Testing
- Bugfixing

##### Version 0.1 `17.09.14`
- first working prototype


## Contributors

- Pascal Welsch - https://github.com/passsy
- Jannis Veerkamp - https://github.com/jannisveerkamp

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
