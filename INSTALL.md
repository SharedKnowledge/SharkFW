# How to install/build the framework

To build the SDK yourself you need to have maven3 installed.
Clone the repository and switch to the `sharkfw` folder with the root of the repository.
With the command `mvn clean install` you will create the core package of the SDK.
It will contain the core functionality to create an application with Shark.
The built jar can be found at `core/target/shark-core.jar` .

We also provide two more profiles. One for a package containing j2se specific code and one for Android specific code.
The build process for j2se specific code can be triggered with `mvn -P shark-j2se clean install`, but this isn't working correctly at the moment.
The android specific code can be built with `mvn -P shark-android clean install` .
The built jar can be found at `android/android-core/target/shark-android.jar` .

## Script for jar installing
Edit the SOURCE location of your Android-Project and execute the copy-jar.sh .