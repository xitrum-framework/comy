#!/bin/sh

cd ..

# Collect files
mkdir target/comy-0.1
sbt package
cp target/scala_2.8.0.RC5/comy_2.8.0.RC5-0.1.jar target/comy-0.1
cp lib_managed/scala_2.8.0.RC5/*.jar target/comy-0.1
cp project/boot/scala-2.8.0.RC5/lib/scala-library.jar target/comy-0.1/scala-library-2.8.0.RC5.jar
cp release/config.properties.sample target/comy-0.1/config.properties
cp release/INSTALL target/comy-0.1

echo "java -server -Xms2000m -Xmx6000m -cp log4j-1.2.14.jar:mongo-java-driver-2.0.jar:netty-3.2.0.CR1.jar:scala-library-2.8.0.RC5.jar:slf4j-api-1.5.10.jar:slf4j-log4j12-1.5.6.jar:comy_2.8.0.RC5-0.1.jar comy.Main server config.properties" > target/comy-0.1/server.sh
echo "java -cp log4j-1.2.14.jar:mongo-java-driver-2.0.jar:netty-3.2.0.CR1.jar:scala-library-2.8.0.RC5.jar:slf4j-api-1.5.10.jar:slf4j-log4j12-1.5.6.jar:comy_2.8.0.RC5-0.1.jar comy.Main gc config.properties" > target/comy-0.1/gc.sh

cd target
tar cjf comy-0.1.tar.bz2 comy-0.1
