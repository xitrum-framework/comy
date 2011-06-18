organization := "tv.cntt"

name         := "comy"

version      := "1.3-SNAPSHOT"

scalaVersion := "2.9.0-1"

// For Xitrum
resolvers += "Sonatype Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots"

// For Netty 4, remove this when Netty 4 is released
resolvers += "Local Maven Repository"       at "file://" + Path.userHome.absolutePath + "/.m2/repository"

libraryDependencies += "tv.cntt"        %% "xitrum"            % "1.1-SNAPSHOT"

libraryDependencies += "ch.qos.logback" %  "logback-classic"   % "0.9.29"

libraryDependencies += "org.mongodb"    %  "mongo-java-driver" % "2.5.3"

mainClass := Some("comy.Boot")

unmanagedClasspath in Runtime <<= (unmanagedClasspath in Runtime, baseDirectory) map { (cp, bd) => cp :+ Attributed.blank(bd / "config") }
