#!/bin/sh

VERSION_COMY=0.9.4
VERSION_SCALA=2.8.0

DIR_BASE=target/comy-$VERSION_COMY
DIR_LIB=$DIR_BASE/lib
DIR_BIN=$DIR_BASE/bin

cd ..

rm -rf target/comy*

mkdir -p $DIR_BASE
cp README $DIR_BASE
cp release/INSTALL $DIR_BASE

mkdir $DIR_LIB
sbt package
cp target/scala_$VERSION_SCALA/comy_$VERSION_SCALA-$VERSION_COMY.jar $DIR_LIB
cp lib_managed/scala_$VERSION_SCALA/compile/*.jar $DIR_LIB
cp lib/*.jar $DIR_LIB
cp project/boot/scala-$VERSION_SCALA/lib/scala-library.jar $DIR_LIB/scala-library-$VERSION_SCALA.jar

mkdir $DIR_BIN
cp release/config.properties.sample $DIR_BIN/config.properties

echo "#!/bin/sh\n" > $DIR_BIN/http.sh
# Quoted so that the * isn't expanded by the shell
echo java -cp '"../lib/*"' -Xms2000m -Xmx6000m -server -Djava.awt.headless=true comy.main.Http config.properties >> $DIR_BIN/http.sh
chmod +x $DIR_BIN/http.sh

echo "#!/bin/sh\n" > $DIR_BIN/gc.sh
echo java -cp '"../lib/*"' comy.main.GC config.properties >> $DIR_BIN/gc.sh
chmod +x $DIR_BIN/gc.sh

cd target
tar cjf comy-$VERSION_COMY.tar.bz2 comy-$VERSION_COMY
