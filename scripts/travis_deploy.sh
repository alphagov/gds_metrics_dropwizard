#!/bin/bash

set -ue

GRADLE_VERSION=$(./gradlew properties | grep version | awk '{ print $2 }')

if [ $TRAVIS_TAG != $GRADLE_VERSION ]; then
    echo tag $TRAVIS_TAG disagrees with gradle version $GRADLE_VERSION. aborting
    exit 1
fi

./gradlew bintrayUpload
