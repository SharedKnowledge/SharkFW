# How to install/build the framework

To build the SDK yourself you need to have maven3 installed.
Clone the repository and ensure that you are at the root of the repository.
With the command `mvn clean install` you will create a jar containing the newest sources.

## Script for installing the jar at the SharkFW-Android repository
This part is just helpful if you are working on the SharkFW-Android repository and need to amend something at this core repository. This script will just run the `mvn clean install` command and will copy the resulting jar to the SharkFW-Android repository.
You need to edit the SOURCE variable in the copy-jar.sh script file to the path of your SharkFW-Android repository. Afterwards you can run the script so it can build and copy the jar.
