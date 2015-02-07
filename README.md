# Tray - a SharedPreferences replacement for Android

Uses a ContentProvider to store the data. The big benefit is that it works for multiple processes.

## Usage

### Getting Started

Add the library as module to your project and add it to your `build.gradle`

```
dependencies {
    ...
    compile project(':tray:library')
}
```

Add the ContentProvider to your `AndroidManifest.xml`

```
<provider
    android:name="net.grandcentrix.tray.provider.TrayProvider"
    android:authorities="<!-- put your AUTHORITY here -->"
    android:exported="false"
    android:multiprocess="false" />
```

`ContentProviders` in a library are difficult because the AUTHORITY needs to be **unique**. Make sure you use your package name as a port of the AUTHORITY.

The last step is to initialize `Tray` with the same Authority in your `Application#onCreate()`

```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Tray.init(/*AUTHORITY*/);
    }
}
```

Don't forget to reference your Application Class in your AndroidManifest. Otherwise onCreate will be never called.

```
<application
    android:name=".MyApplication"
    ...
```

### Save and read preferences

```java
// create a preference accessor. This is for global app preferences.
final TrayAppPreferences appPreferences = new TrayAppPreferences(getContext());
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

It's recommended to bundle preferences in groups, so called modules. It you where using `SharedPreferences` before you might have used different files to group your preferences. Extending the `TrayModulePreferences` and put all Keys inside this class is a recommended way to keep your code clean.

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