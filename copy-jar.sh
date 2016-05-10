#!/usr/bin/env bash

SOURCE=~/dev/shark/projects/SharkNetNG/AndroidSharkFW

mvn clean install
cp ./target/sharkfw-core-1.0-SNAPSHOT.jar $SOURCE/libs/
