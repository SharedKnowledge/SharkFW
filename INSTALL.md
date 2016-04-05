# How to install/build the framework

To build the SDK yourself you need to have maven3 installed.
Clone the repository and switch to the `sharkfw` folder with the root of the repository.
With the command `mvn clean install` you will create the jars of the modules.

SharkFW contains multiple submodules. Each built module will be placed in the modules folder under target.

## Script for jar installing
Edit the SOURCE location of your Android-Project and execute the copy-jar.sh .