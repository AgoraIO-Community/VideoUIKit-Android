# Agora UI KIT

## How to Use

1. Import Library

Go to your app build.gradle and add the following

```
implementation 'io.agora.uikit:agorauikit:1.0.0'
```

2. Create a videocall object

```
AgoraVideoCall videoCall = new AgoraVideoCall(getApplicationContext(), appid, token, channel);
```

3. Get the predefined intent

```
Intent intent = videocall.start();
```

4. Start the activity

```
startActivity(intent);
```

## Customization Options

Important: For customization to work, data binding must be enabled

Go to your app build.gradle file

Add the following under android

```
    dataBinding {
        enabled = true
    }
```


For customizing the call screen a few methods are provided  

First create the config object

```
UIConfig config = new UIConfig();
```

Call the methods that you want to use

```
config.hideSwitchCamera();
config.hideVideoMute();
config.setSwitchCameraColor("#FFFFFF", "#000000");
```

You can also chain the methods

```
config.hideAudioMute().showCheckButton();
```

We also provide a check button which you can use to start your custom intent. For example:  

Say you want the check button to start your custom activity called TestActivity

```
config.showCheckButton();
Intent intent = new Intent(getBaseContext(), TestActivity.class);
videocall.setIntent(intent);
```

You can then call the start method 

```
startActivity(videocall.start())
```


## Docs

### Methods for AgoraVideoCall object

```setConfig(UIConfig)```

Takes a UIConfig objects and sets that as the config for the call

```setIntent(Intent)```

Takes an intent that will be started when the check button is pressed


```start()```

Starts the call activity

### Methods for UIConfig object

```hideSwitchCamera()```

This function will hide the button responsible for switching between front and rear camera

```hideVideoMute()```

This function will hide the button responsible for muting the local video feed

```hideAudioMute()```

This function will hide the button repsosible for mute the local audio feed

```showCheckButton()```

This function adds a check button which can be remappable to what the user wants to do

```setSwitchCameraColor(String background, String foreground)```

Sets the background and foreground color of the Switch Camera Button.  
Example: config.setSwitchCameraColor("#00FFFF", "#FF0000")

```setSwitchCameraBackgroundColor(String background)```

Sets the background color of the Switch Camera Button.  
Example: config.setSwitchCameraBackgroundColor("#00FFFF")

```setSwitchCameraForegroundColor(String foreground)```

Sets the foreground color of the Switch Camera Button.  
Example: config.setSwitchCameraForegroundColor("#FF0000")

```setAudioMuteColor(String background, String foreground)```

Sets the background and foreground color of the Mute Audio Button.  
Example: config.setAudioMuteColor("#00FFFF", "#FF0000")

```setAudioMuteBackgroundColor(String background)```

Sets the background color of the Mute Audio Button.  
Example: config.setAudioMuteBackgroundColor("#00FFFF")

```setAudioMuteForegroundColor(String foreground)```

Sets the foreground color of the Mute Audio Button.  
Example: config.setAudioMuteForegroundColor("#FF0000")

```setVideoMuteColor(String background, String foreground)```

Sets the background and foreground color of the Mute Video Button.  
Example: config.setVideoMuteColor("#00FFFF", "#FF0000")

```setVideoMuteBackgroundColor(String background)```

Sets the background color of the Mute Video Button.  
Example: config.setVideoMuteBackgroundColor("#00FFFF")

```setVideoMuteForegroundColor(String foreground)```

Sets the foreground color of the Mute Video Button.  
Example: config.setVideoMuteForegroundColor("#FF0000")

```setCheckColor(String background, String foreground)```

Sets the background and foreground color of the Check Button.  
Example: config.setCheckColor("#00FFFF", "#FF0000")

```setCheckBackgroundColor(String background)```

Sets the background color of the Check Button.  
Example: config.setCheckBackgroundColor("#00FFFF", "#FF0000")

```setCheckForegroundColor(String foreground)```

Sets the background and foreground color of the Check Button.  
Example: config.setCheckForegroundColor("#00FFFF", "#FF0000")

```setCallColor(String background, String foreground)```

Sets the background and foreground color of the End Call Button.
Example: config.setCallColor("#00FFFF", "#FF0000")

```setCallForegroundColor(String foreground)```

Sets the foreground color of the End Call Button.  
Example: config.setCallForegroundColor("#FF0000")

```setCallBackgroundColor(String background)```

Sets the background color of the End Call Button.  
Example: config.setCallBackgroundColor("#00FFFF")

```setSwitchCameraPressedColor(String background, String foreground)```

Sets the background and foreground color of the Switch Camera Button when the button is pressed.  
Example: config.setSwitchCameraPressedColor("#00FFFF", "#FF0000")

```setSwitchCameraPressedForegroundColor(String foreground)```

Sets the foreground color of the Switch Camera Button when the button is pressed.  
Example: config.setSwitchCameraPressedForegroundColor("#FF0000")

```setSwitchCameraPressedBackgroundColor(String background)```

Sets the background color of the Switch Camera Button when the button is pressed.  
Example: config.setSwitchCameraPressedBackgroundColor("#00FFFF")

```setAudioMutePressedColor(String background, String foreground)```

Sets the background and foreground color of the Mute Audio Button when the button is pressed.  
Example: config.setAudioMutePressedColor("#00FFFF", "#FF0000")

```setAudioMutePressedForegroundColor(String foreground)```

Sets the foreground color of the Mute Audio Button when the button is pressed.  
Example: config.setAudioMutePressedForegroundColor("#FF0000")

```setAudioMutePressedBackgroundColor(String background)```

Sets the background color of the Mute Audio Button when the button is pressed.  
Example: config.setAudioMutePressedBackgroundColor("#00FFFF")

```setVideoMutePressedColor(String background, String foreground)```

Sets the background and foreground color of the Mute Video Button when the button is pressed.  
Example: config.setVideoMutePressedColor("#00FFFF", "#FF0000")

```setVideoMutePressedForegroundColor(String foreground)```

Sets the foreground color of the Mute Video Button when the button is pressed.  
Example: config.setVideoMutePressedForegroundColor("#FF0000")

```setVideoMutePressedBackgroundColor(String background)```

Sets the background color of the Mute Video Button when the button is pressed.  
Example: config.setVideoMutePressedBackgroundColor("#FF0000")

```setSwitchCameraIcon(@DrawableRes int switchCameraIcon)```

This function is used to change the icon for the switch camera button when it is not selected

```setSwitchCameraPressedIcon(@DrawableRes int switchCameraPressedIcon)```

This function is used to change the icon for the switch camera button when it is selected

```setMuteAudioIcon(@DrawableRes int muteAudioIcon)```

This function is used to change the icon for the Mute Audio button when it is not selected

```setMuteAudioPressedIcon(@DrawableRes int muteAudioPressedIcon)```

This function is used to change the icon for the Mute Audio button when it is selected

```setMuteVideoIcon(@DrawableRes int muteVideoIcon)```

This function is used to change the icon for the Mute Video button when it is not selected

```setMuteVideoPressedIcon(@DrawableRes int muteVideoPressedIcon)```

This function is used to change the icon for the Mute Video button when it is selected

```setCheckIcon(@DrawableRes int checkIcon)```

This function is used to change the icon for the Check button.  
Check Icon does not have a separate function for when it is selected since tapping on this button should launch a new Activity.

```setCallIcon(@DrawableRes int callIcon)```

This function is used to change the icon for the End Call button when it is selected.  
End call Icon does not have a separate function for when it is selected since tapping on this button should end the call activity.
