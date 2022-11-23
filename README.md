# Agora VideoUIKit for Android

<p align="center">
	<a href="https://jitpack.io/#AgoraIO-Community/VideoUIKit-Android">
    <img src="https://jitpack.io/v/AgoraIO-Community/VideoUIKit-Android.svg"/></a>
	<img src="https://github.com/AgoraIO-Community/VideoUIKit-Android/workflows/Build/badge.svg"/>
  <img src="https://img.shields.io/github/license/AgoraIO-Community/VideoUIKit-Android"/>
  <a href="https://www.agora.io/en/join-slack/">
    <img src="https://img.shields.io/badge/slack-@RTE%20Dev-blue.svg?logo=slack">
  </a>
</p>


Instantly integrate Agora in your own Android application or prototype.

<p align="center"><img src="https://camo.githubusercontent.com/affd109caf06f0014a55bc411b66b34b8dc68f8d86befe6a2f27dad1fc7c6a5b/68747470733a2f2f692e6962622e636f2f5853576d57397a2f4e65772d50726f6a6563742d372e706e67"/>
</p>


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

### Roadmap

- [x] Muting/Unmuting a remote member
- [ ] Usernames
- [ ] Promoting an audience member to a broadcaster role.
- [ ] Layout for Voice Calls
- [ ] Cloud recording

## VideoUIKits

The plan is to grow this library and have similar offerings across all supported platforms. There are already similar libraries for [Flutter](https://github.com/AgoraIO-Community/VideoUIKit-Flutter/), [React Native](https://github.com/AgoraIO-Community/ReactNative-UIKit), and [iOS](https://github.com/AgoraIO-Community/iOS-UIKit/), so be sure to check them out.