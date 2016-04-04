#!/usr/bin/env bash

SOURCE=~/dev/AndroidStudioProjects/SharkNetNG

mvn -P shark-android clean install
cp ./android/android-core/target/shark-android.jar $SOURCE/app/libs/
