#!/bin/sh

COMY_VERSION=0.1
SCALA_VERSION=2.8.0.RC5

cd ..

rm -rf target/comy-$COMY_VERSION*

mkdir -p target/comy-$COMY_VERSION
cp README target/comy-$COMY_VERSION
cp release/INSTALL target/comy-$COMY_VERSION

mkdir target/comy-$COMY_VERSION/lib
sbt package
cp target/scala_$SCALA_VERSION/comy_$SCALA_VERSION-$COMY_VERSION.jar target/comy-$COMY_VERSION/lib
cp lib_managed/scala_$SCALA_VERSION/compile/*.jar target/comy-$COMY_VERSION/lib
cp project/boot/scala-$SCALA_VERSION/lib/scala-library.jar target/comy-$COMY_VERSION/lib/scala-library-$SCALA_VERSION.jar

mkdir target/comy-$COMY_VERSION/bin
echo "#!/bin/sh\n" > target/comy-$COMY_VERSION/bin/server.sh
echo java -cp ../lib/log4j-1.2.14.jar:../lib/mongo-java-driver-2.0.jar:../lib/netty-3.2.0.CR1.jar:../lib/scala-library-$SCALA_VERSION.jar:../lib/slf4j-api-1.5.10.jar:../lib/slf4j-log4j12-1.5.6.jar:../lib/comy_$SCALA_VERSION-$COMY_VERSION.jar -server -Xms2000m -Xmx6000m comy.Main server config.properties >> target/comy-$COMY_VERSION/bin/server.sh
chmod +x target/comy-$COMY_VERSION/bin/server.sh
echo "#!/bin/sh\n" > target/comy-$COMY_VERSION/bin/gc.sh
echo java -cp ../lib/log4j-1.2.14.jar:../lib/mongo-java-driver-2.0.jar:../lib/netty-3.2.0.CR1.jar:../lib/scala-library-$SCALA_VERSION.jar:../lib/slf4j-api-1.5.10.jar:../lib/slf4j-log4j12-1.5.6.jar:../lib/comy_$SCALA_VERSION-$COMY_VERSION.jar comy.Main gc config.properties >> target/comy-$COMY_VERSION/bin/gc.sh
chmod +x target/comy-$COMY_VERSION/bin/gc.sh
cp release/config.properties.sample target/comy-$COMY_VERSION/bin/config.properties

cd target
tar cjf comy-$COMY_VERSION.tar.bz2 comy-$COMY_VERSION
