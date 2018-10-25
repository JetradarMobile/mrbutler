MrButler
========

[![Release](https://jitpack.io/v/jetradarmobile/mrbutler.svg)](https://jitpack.io/#jetradarmobile/mrbutler)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)

Reactive Android App Permissions API with delegates and logging.

| API | Description |
|-------------------------------------------|--------------------------------------------------------|
| `request(vararg permissions: String)`     | emit `true` if all requested permissions granted       |
| `requestSingle(permission: String)`       | emit result of permission request                      |
| `requestEach(vararg permissions: String)` | emit results for each permission request               |
| `check(vararg permissions: String)`       | emit `true` if all permissions already granted         |
| `checkSingle(permission: String)`         | emit result of availability check permission           |
| `checkEach(vararg permissions: String)`   | emit result of availability check  each permission     |
| `require(vararg permissions: String)`     | emit `PermissionsDeniedException` if permission denied |


Download
--------

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

Add the dependency

```groovy
dependencies {
    compile 'com.github.jetradarmobile:mrbutler:1.0.2'
}
```


Usage
-----

Request LOCATION permission

```kotlin 
mrButler(activity)
    .request(Manifest.permission.ACCESS_FINE_LOCATION)
    .subscribe { granted -> ... }
```

A feature of this implementation is `Delegates`. Using delegates you can request permission anywhere.

Initialize MrButler

```kotlin
class App : Application() {
  val permissionsActivityDelegate = PermissionsActivityDelegate()
  val mrButler = MrButler(permissionsActivityDelegate) { message ->
    Log.i("Permissions", message)
  }

  override fun onCreate() {
    super.onCreate()
    instance = this
  }

  companion object {
    lateinit var instance: App
  }
}
```

Associate the permissions delegate with activity

```kotlin
class MainActivity : AppCompatActivity() {
  private val permissionsDelegate = App.instance.permissionsActivityDelegate

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    permissionsDelegate.attach(this)
    // ...
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    permissionsDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  override fun onDestroy() {
    super.onDestroy()
    permissionsDelegate.detach()
  }
}
```

Request permissions anywhere

```kotlin
App.instance.mrButler
    .request(Manifest.permission.ACCESS_FINE_LOCATION)
    .subscribe { granted -> ... }
```


License
-------

    Copyright 2018 JetRadar

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
