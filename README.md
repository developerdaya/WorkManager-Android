# How to get a Git project into your build:
# 🖥️ A Passionate Android Engineer | Developer Daya
## 💙 Love to Do Magic ✨💫 By CODE! 
## 🎨 Let’s Develop Something Lajawab

## Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
## Step 2. Open the project in Android Studio.

 [![](https://jitpack.io/v/developerdaya/GPSWorkManagerExample.svg)](https://jitpack.io/#developerdaya/GPSWorkManagerExample)
## Step 3. Add the dependency

	dependencies {
	        implementation 'com.github.developerdaya:GPSWorkManagerExample:Tag'
	}
## 4. Build the project and run it on an Android device or emulator.
   
 ### Key Components of the GPS Tracking App:
- **Tracking Service**: The app continuously tracks the user's GPS location and logs it in the background.
- **Phone Unlock Detection**: Tracks the phone unlock time using a broadcast receiver.
- **Location Display**: The app shows the user's last known location and the time it was updated.
- **WorkManager**: Periodically updates the location every 15 minutes.
- **Data Persistence**: Location data is stored in a Room database for persistent storage.
- **Foreground Service**: Used to ensure the app can track the location while running in the background.
- **Permissions**: The app requests multiple permissions, including fine location, coarse location, background location, and usage stats.

markdown
The GPS Tracking App is an Android application that tracks device activities such as screen unlocks, GPS location updates, and app usage. It uses a combination of background services and receivers to continuously monitor and log data.

## Features
- **GPS Location Tracking**: Tracks the user's location in the background and saves it to a local database.
- **Screen Unlock Detection**: Logs the last time the device was unlocked using a broadcast receiver.
- **Persistent Data Storage**: Uses Room database for storing location logs.
- **Background Service**: Uses a Foreground service to ensure the app continues to function even in the background.
- **WorkManager Integration**: Schedules periodic location updates every 15 minutes using `WorkManager`.
- **User Interface**: Displays the last known location and unlock time to the user.

## Permissions
The following permissions are required for the app to function:
- `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`: To get precise and approximate location data.
- `PACKAGE_USAGE_STATS`: To monitor app usage.
- `FOREGROUND_SERVICE`: To run a background service.
- `ACCESS_BACKGROUND_LOCATION`: To track location while the app is in the background.
- `POST_NOTIFICATIONS`: To show notifications when the app is running in the foreground.

Make sure to request these permissions at runtime and handle denial cases properly.

## Usage
1. **Enable Location Tracking**: The user can enable or disable location tracking using a switch in the app.
2. **View Logs**: The app shows the last known location and last unlock time on the main screen.
3. **Background Location**: The app runs a background service to periodically update the location using `WorkManager`.

## Key Components

### Manifest File
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### MainActivity
- **Location Tracking**: The app tracks the user's GPS location every 5 seconds and logs it in the database.
- **Phone Unlock Detection**: Uses `PhoneUnlockReceiver` to capture unlock events and log the timestamp.
- **Database Interaction**: Saves the location data into a Room database and retrieves it to display in the UI.

### WorkManager
- **LocationWorker**: A worker class responsible for updating the location every 15 minutes in the background.

### Foreground Service
- **ForegroundService**: Runs a persistent foreground service that keeps the app active in the background for location tracking.

### Shared Preferences
- **SharedPreferenceUtil**: Manages simple settings like whether tracking is enabled and the last known location.

## Libraries
- **Room Database**: For persistent data storage.
- **WorkManager**: For scheduling background tasks efficiently.
- **FusedLocationProviderClient**: For accessing location services.
- **Kotlin Coroutines**: For managing asynchronous background tasks.
### Key Suggestions for Code Improvement:

1. **Permission Handling**: 
   Ensure you're handling permission requests properly at runtime, especially for background location and usage stats.
   
   ```kotlin
   if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
       ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
   }
   ```

2. **BroadcastReceiver Registration**: 
   Make sure to register and unregister the `PhoneUnlockReceiver` properly in `onResume()` and `onPause()` methods, as you've already implemented.

3. **WorkManager for Background Tasks**: 
   Using `WorkManager` ensures the app performs periodic tasks in a battery-efficient way, so this is a good choice for background location tracking.

4. **UI Updates**: 
   Update the UI whenever new data is logged. For instance, when the phone unlock time is updated, reflect the change on the main screen.

5. **Data Persistence**: 
   Using `Room` is a great approach for persisting data. Ensure you're handling database interactions off the main thread, ideally with Kotlin coroutines.
   ## Screenshots
![image](https://github.com/user-attachments/assets/3077e4fa-1626-4dd9-8da0-94738c86f31b) 


## Happy Coding : 💗
