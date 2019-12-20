# Agora UI KIT

## How to Use

1. Import Library

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
