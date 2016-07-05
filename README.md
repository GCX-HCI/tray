# Tray - a SharedPreferences replacement for Android

[![Build Status](https://travis-ci.org/grandcentrix/tray.svg?branch=master)](https://travis-ci.org/grandcentrix/tray) [![License](https://img.shields.io/badge/license-Apache%202-green.svg?style=flat)](https://github.com/grandcentrix/tray/blob/master/LICENSE.txt)

If you have read the documentation of the [`SharedPreferences`](http://developer.android.com/reference/android/content/SharedPreferences.html) you might have seen one of these warnings:

>Note: currently this class does not support use across multiple processes. This will be added later.

**Sometimes _later_ becomes _never_!** Google even deprecated the multiprocess support because it never worked relieable

[![](https://cloud.githubusercontent.com/assets/1096485/9793296/110575d2-57e5-11e5-9728-34d3597771b8.png)](http://developer.android.com/reference/android/content/Context.html#MODE_MULTI_PROCESS)

Tray is this mentioned _explicit cross-process data management approach_ powered by a [`ContentProvider`](http://developer.android.com/reference/android/content/ContentProvider.html). Tray also provides an advanced API which makes it super easy to access and maintain your data with upgrade and migrate mechanisms. Welcome to SharedPreferences 2.0 aka Tray.

## Features

- **works multiprocess**
- stores simple data types as key value pairs
- automatically saves metadata for each entry (created, last updated, ...)
- manage your Preferences in modules [TrayPreference](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/TrayPreferences.java)
- Delete single modules, all modules, or [all modules except some very important ones](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/Tray.java#L79)
- update and migrate your data from one app version to next one with versioned Preferences and a [`onUpgrade()`](https://github.com/grandcentrix/tray/blob/14325e182e225e668218fc539f5de0c9b9e524e7/library/src/main/java/net/grandcentrix/tray/core/Preferences.java#L196) method
- Migrate your current data stored in the SharedPreferences to Tray with [`SharedPreferencesImport`](https://github.com/grandcentrix/tray/blob/master/library/src/main/java/net/grandcentrix/tray/core/SharedPreferencesImport.java)
- **tray is 100% unit tested!**
- 0 lint warnings/errors
- ![new_badge](https://cloud.githubusercontent.com/assets/1096485/9856970/37791f1c-5b18-11e5-97e4-53b8984c76e1.gif) Android 6.0 [Auto Backup for Apps](https://developer.android.com/preview/backup/index.html) support! [Read more in the wiki](https://github.com/grandcentrix/tray/wiki/Android-M-Auto-Backup-for-Apps-support)

## Usage

Simple tutorial how to use Tray in your project instead of the SharedPreferences

### Save and read preferences

```java
// create a preference accessor. This is for global app preferences.
final AppPreferences appPreferences = new AppPreferences(getContext()); // this Preference comes for free from the library
// save a key value pair
appPreferences.put("key", "lorem ipsum");

// read the value for your key. the second parameter is a fallback (optional otherwise throws)
final String value = appPreferences.getString("key", "default");
Log.v(TAG, "value: " + value); // value: lorem ipsum

// read a key that isn't saved. returns the default (or throws without default)
final String defaultValue = appPreferences.getString("key2", "default");
Log.v(TAG, "value: " + defaultValue); // value: default
```

No `Editor`, no `commit()` or `apply()` :wink:

### Create your own preference module

It's recommended to bundle preferences in groups, so called modules instead of putting everything in one global module. If you were using `SharedPreferences` before, you might have used different files to group your preferences. Extending the `TrayModulePreferences` and put all Keys inside this class is a recommended way to keep your code clean.

```java
// create a preference accessor for a module
public class MyModulePreference extends TrayPreferences {

    public static String KEY_IS_FIRST_LAUNCH = "first_launch";

    public MyModulePreference(final Context context) {
        super(context, "myModule", 1);
    }
}
```

```java
// accessing the preferences for my own module
final MyModulePreference myModulePreference = new MyModulePreference(getContext());
myModulePreference.put(MyModulePreference.KEY_IS_FIRST_LAUNCH, false);
```

See the [sample project](https://github.com/grandcentrix/tray/tree/master/sample) for more

Like the Android [`SQLiteOpenHelper`](http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html) a `TrayPreference` lets you implement methods to handle versioning.

```java
public class MyModulePreference extends TrayPreferences {

    public MyModulePreference(final Context context) {
        super(context, "myModule", 1);
    }

    @Override
    protected void onCreate(final int initialVersion) {
        super.onCreate(initialVersion);
    }

    @Override
    protected void onUpgrade(final int oldVersion, final int newVersion) {
        super.onUpgrade(oldVersion, newVersion);
    }

    @Override
    protected void onDowngrade(final int oldVersion, final int newVersion) {
        super.onDowngrade(oldVersion, newVersion);
    }
}

```

`// TOOD add clear sample`

### Migrate from SharedPreferences to Tray

`// TODO`

## Getting Started [![Download](https://api.bintray.com/packages/passsy/maven/Tray/images/download.svg) ](https://bintray.com/passsy/maven/Tray/_latestVersion)

##### Add Tray to your project

Tray is available via [jcenter](http://blog.bintray.com/2015/02/09/android-studio-migration-from-maven-central-to-jcenter/)

```java

dependencies {
    compile 'net.grandcentrix.tray:tray:0.10.0'
}

```

##### Set the authority

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

Clean your project afterwards to generate the `/build/generated/res/generated/BUILDTYPE/values/generated.xml` which should then contain the following value:

```xml
    <!-- Values from default config. -->
    <item name="tray__authority" type="string">your.app.id.tray</item>
```

Tray is based on a ContentProvider. A ContentProvider needs a **unique** authority. When you use the same authority for multiple apps you will be unable to install the app due to a authority conflict with the error message:

```
Failure [INSTALL_FAILED_CONFLICTING_PROVIDER]
```

Changing the authority from one version to another app version is no problem! Tray always uses the same database.

If you are using different applicationIds for different buildTypes of flavors read [this](https://blog.grandcentrix.net/how-to-install-different-app-variants-on-one-android-device/) article.

## Project state

Tray is currently in active development by [grandcentrix](http://www.grandcentrix.net/). We decided to go open source after reaching 100% test coverage.
[grandcentrix](http://www.grandcentrix.net/) uses Tray in production in two apps without problems.

You can follow the development in the [`develop`](https://github.com/grandcentrix/tray/tree/develop) branch.

## Testcoverage 100%

Tray has 100% test coverage and we'll try to keep it at that level for stable releases.

You can run the coverage report with `./gradlew createDebugCoverageReport`. You'll find the output in `library/build/outputs/coverage/debug/index.html` which looks like this:

![coverage report](https://cloud.githubusercontent.com/assets/1096485/9990484/fe61888c-6061-11e5-890d-a76f1ef60304.png)

You can check the coverage report at [codecov.io](https://codecov.io/github/grandcentrix/tray?branch=master)

Those ~170 tests will help us indicate bugs in the future before we publish them. Don't think the code is 100% bug free based on the test coverage.


## Build state

Branch | Status | Coverage
------------- | ------------- | -------------
[`master`](https://github.com/grandcentrix/tray/tree/master) | [![Build Status](https://travis-ci.org/grandcentrix/tray.svg?branch=master)](https://travis-ci.org/grandcentrix/tray) | [![codecov.io](http://codecov.io/github/grandcentrix/tray/branch.svg?branch=master)](https://codecov.io/github/grandcentrix/tray?branch=master)
[`develop`](https://github.com/grandcentrix/tray/tree/develop) | [![Build Status](https://travis-ci.org/grandcentrix/tray.svg?branch=develop)](https://travis-ci.org/grandcentrix/tray) | [![codecov.io](http://codecov.io/github/grandcentrix/tray/branch.svg?branch=develop)](https://codecov.io/github/grandcentrix/tray?branch=develop)

## ContentProvider is overkill

At first, it was the simplest way to use IPC with [`Binder`](http://developer.android.com/reference/android/os/Binder.html) to solve the multiprocess problem. Using the `ContentProvider` with a database turned out to be very handy when it comes to save metadata. We thought about replacing the database with the real `SharedPreferences` to boost the performance (the SharedPreferences do not access the disk for every read/write action which causes the multiprocess problem btw) but the metadata seemed to be more valuable to us. see [more informations](https://github.com/grandcentrix/tray/issues/28#issuecomment-108282253)

If you have found a better solution implement the [`TrayStorage`](https://github.com/grandcentrix/tray/blob/14325e182e225e668218fc539f5de0c9b9e524e7/library/src/main/java/net/grandcentrix/tray/core/TrayStorage.java) and contribute to this project! We would appreciate it.

That said, yes the performance isn't as good as the SharedPreferences. But the performance is good enough to save/access single key value pairs synchron. If you want to save more you should think about a simple database.

## Missing Features

Tray is ready to use without showblockers! But here are some nice to have features for the future:
- Reactive wrapper to observe values 
- no support to save `Set<String>`. Is someone using this?
- more metadata fields: (i.e. app version code/name)

## Roadmap

- performance tests
- memory cache for based on contentobservers
- prevent app crashes due to database errors
- rx wrapper for changes
- save additional data types (`Set<String>`, `byte[]`)

## Versions

##### Version 0.10.0 `31.05.16`
- All features and changes of the 1.0.0-rc preview builds
- #65 Fix deletion of non string migrated shared preferences.

>##### Version 1.0.0 preview - postponed until the memory cache is ready

>###### 1.0.0-rc3 `05.11.15`
- hotfix for listener on Android 6.0 which has caused a infinity loop #55
- the sample project includes now a way to test the multi process support compared to the `SharedPreferences`
- removed unnecessary write operation for every version check #54

>###### 1.0.0-rc2 `24.09.15`
- added logging for all data changing methods. Enable via `adb shell setprop log.tag.Tray VERBOSE`

>###### 1.0.0-rc1 `21.09.15`
- **Android M Auto Backup feature support** (see the [Documentation](https://github.com/grandcentrix/tray/wiki/Android-M-Auto-Backup-for-Apps-support))
    - split up database for *user* and *device* specific data (device specific data can now be excluded from the auto backup)
    - `TrayPreferences` has now an optional 3. constructor parameter `TrayStorage.Type`, `USER` or `DEVICE` indicating the internal database (required for Android M Auto Backup). Default is `USER`
- **New methods and changes**
    - `PreferenceAccessor#wipe()` clears the preference data and it's internal data (version)
    - `TrayPreferences#annexModule(String name)` imports a module by name and wipes it afterwards. This allows renaming of preferences without losing data
    - `AbstractTrayPreference#annex(ModularizedStorage<TrayItem>)` allows a storage to import another storage, wipes the imported afterwards
    - `Preference` `#onCreate(...)` and `#onUpgrade(...)` aren't abstract anymore because they don't require an implementation
- **Deprecations** (will be removed soon)
    - `TrayAppPreferences` is now deprecated. Use `AppPreferences` instead (renaming)
    - `TrayModulePreferences` is now deprecated. Use `TrayPreferences` instead to extend from for your own Preferences
- **Internal structure**
    - new package structure. merged packages `accessor`, `migration` and `storage` into `core`
    - package `provider` contains a `TrayStorage` implementation with a `ContentProvider`. Is easy exchangeable with another `TrayStorage` implementation
    - `ModularizedTrayPreference` is now called `AbstractTrayPreference`
    - `ModularizedStorage` was renamed to `TrayStorage`


##### Version 0.9.2 `02.06.15`
- `getContext()` is working in `TrayModulePreference#onCreate`

##### Version 0.9.1 `18.05.15`
- saving `null` with `mPref.put(KEY, null)` works now
- access to preference with throwing methods instead of default value (throws ItemNotFoundException). Example: `mPref.getString(KEY);` instead of `mPref.getString(KEY, "defaultValue");`
- WrongTypeException when accessing a preference with a different type and the data isn't parsable. Float (`10.1f`) -> String works, String (`"10.1"`) -> Float works, String (`"test"`) -> Float throws!
- javadoc in now included in aar

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
