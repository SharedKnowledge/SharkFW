# How to install/build the framework

To build the SDK yourself you need to have maven3 installed.
Clone the repository and switch to the sharkfw folder with the root of the repository.
With the command `mvn clean install` you will create the core package of the SDK.
It will contain the core functionality to create an application with Shark.

We also provide two more profiles. One for a package containing j2se specific code and one for Android specific code.
The build process using a profile can be triggered with `mvn -P shark-j2se clean install` or `mvn -P shark-android clean install` .

The built .jar file will be in the folder sharkfw/target.
