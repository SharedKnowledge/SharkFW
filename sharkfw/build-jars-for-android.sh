#!/usr/bin/env bash

DEST=~/Work/Shark2/SharkNetNG/AndroidSharkFW/libs/

mvn clean install
rm -rf $DEST*
mkdir -p $DEST

cp ./core/target/*.jar $DEST

echo "Done"