# Agora UIKit for iOS and macOS

[![](https://jitpack.io/v/AgoraIO-Community/Android-UIKit.svg)](https://jitpack.io/#AgoraIO-Community/Android-UIKit)

Instantly integrate Agora in your own Android application or prototype.

## Requirements

- Android 24+
- Android Studio
- [An Agora developer account](https://www.agora.io/en/blog/how-to-get-started-with-agora?utm_source=github&utm_repo=agora-android-uikit)

## Installation

**Step 1:** Add it in your root build.gradle at the end of repositories:

```css
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

**Step 2:** Add the dependency

```css
dependencies {
  implementation 'com.github.AgoraIO-Community:Android-UIKit:version'
}
```

Then sync gradle build files. More information on [JitPack](https://jitpack.io/#AgoraIO-Community/Android-UIKit).

## Usage

Once installed, you can add the AgoraVideoViewer from within the context of your MainActivity like so:

```kotlin
// Create AgoraVideoViewer instance
val agView = AgoraVideoViewer(this, AgoraConnectionData("my-app-id"))
// Fill the parent ViewGroup (MainActivity)
this.addContentView(
  agView,
  FrameLayout.LayoutParams(
    FrameLayout.LayoutParams.MATCH_PARENT,
    FrameLayout.LayoutParams.MATCH_PARENT
  )
)
```

To join a channel, simply call:

```kotlin
agView.join("test", role=Constants.CLIENT_ROLE_BROADCASTER)
```