# Wi-Fi Direct Example Android Application

This is a demo of the Shark framework used together with Wi-Fi Direct on an Android device.

## Idea and aim of the application
The idea behind this app was to demonstrate how devices running a Shark application can communicate and share knowledge _automatically_ and _adhoc_ (via Wi-Fi Direct) when in reach. The application should synchronize knowledge between itself and any other device running this application it connects to, so that both eventually have the exact same knowledge. Any knowledge already known by the other peer should not be synchronized.  
Knowledge received this way can then again be synchronized with the next device that comes in reach, but again, only if that applications knowledge base does not already know about it.
Also, the synchronization process should only happen once within a reasonable amount of time to reduce battery drain and allow connections to different devices in reach. 
Eventually, all applications on all devices that came within Wi-Fi Direct range, should share the same knowledge in their knowledge base.


## Prerequisites and installation
This app uses Androids Wi-Fi Direct Feature and therefore requires at least two Android devices running Android 4.4.2 or higher.  
_Note: It did not work on a Huawei Honor 6 device. We could not find out why._ 

It was developed using Android Studio. Therefore the easiest way to install this application on a device is to open the code with Android Studio and run it. 

## Using the application

### Starting the app
After starting the application, one of three example peers can be chosen for each device. They are
* Alice
* Bob
* Clara

Choosing a peer will automatically create a small, prefilled knowledge base. This knowledge base is different for each peer.

### Knowledge base and log
The options menu holds buttons to switch between the **Log view** and the **Knowledge base view**.

The **Knowledge base view** is the default view of the application. Here, the current knowledge base is printed with its context space and knowledge.

The **Log view**.. shows the log. It is filled with useful information about connection attempts, incoming/outgoing interest and knowledge as well as debug information.  


### Enabling Wi-Fi Direct
Wi-Fi direct can be enabled by selecting "Enable Wifi P2P" in the options menu. **There is currently a bug** that immediately prints out a "**_disconnected_**" message, which can be safely ignored.

When Wi-Fi Direct is enabled, the device will look for other devices with enabled Wi-Fi Direct. Two devices will try to establish a connection, whereas the better device (Hardware, battery status etc..) usually becomes the server. 
The application on the clients device will then send an interest to initiate communication. If the other device responds (e.g. runs this application), a protocol is started that will synchronize knowledge.  
Unfortunately, this protocol might take a little time but should eventually finish. 

### Terminating the app
Before closing the application, Wi-Fi Direct must be disabled. The option to do this can be found in the options menu. **Not disabling Wi-Fi Direct might leave the thread that is looking for other devices running. This quickly drains the phones battery.** If Wi-Fi Direct was not disabled by accident, the only way to really ensure that this thread is stopped is by restarting the phone. 

## Contributors
This app was developed in the winter semester 2014/2015 at the University of applied sciences Berlin (HTW Berlin) by

* Jetmir Gigollaj
* Ruben Ahlhelm
* Veit Heller (veit@veitheller.de, @hellerve)
* Simon Arnold  (simonh.arnold@gmail.com, @simonArnold)
