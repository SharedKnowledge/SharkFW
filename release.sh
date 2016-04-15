#!/bin/bash

while getopts ":Mmp" Option
do
  case $Option in
    M ) major=true;;
    m ) minor=true;;
    p ) patch=true;;
  esac
done

shift $(($OPTIND - 1))

version=$(git describe)

# Build array from version string.

a=( ${version//./ } )

# If version string is missing or has the wrong number of members, show usage message.

if [ ${#a[@]} -ne 3 ]
then
  echo "usage: $(basename $0) [-Mmp] major.minor.patch"
  exit 1
fi

# Increment version numbers as requested.

if [ ! -z $major ]
then
  ((a[0]++))
  a[1]=0
  a[2]=0
fi

if [ ! -z $minor ]
then
  ((a[1]++))
  a[2]=0
fi

if [ ! -z $patch ]
then
  ((a[2]++))
fi

newversion="${a[0]}.${a[1]}.${a[2]}"

echo $newversion 
message="New Release with Version ${newversion}"

git tag -a $newversion -m "${message}"

echo "New Tag ${newversion} with the message '${message}' created."
echo "Pushing to origin..."
git push origin $newversion
echo "Successfully pushed!"
gnome-open https://github.com/SharedKnowledge/SharkFW/releases > /dev/null
