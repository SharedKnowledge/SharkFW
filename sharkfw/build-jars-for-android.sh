#!/usr/bin/env bash

DEST=~/Work/SharkNetNG/app/libs/

mvn -P clean install

rm -r $DEST*

cp ./core/target/*.jar $DEST
cp ./android/android-core/target/*.jar $DEST
cp ./android/android-wifi/target/*.jar $DEST