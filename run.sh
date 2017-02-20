#!/bin/sh
gradle clean
gradle bootRepackage
cd build/libs
java -Xms128m -Xmx256M -jar ./leo-1.0.jar --spring.profiles.active=prod
